package com.google.cloud.genomics.utils;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import com.google.api.services.genomics.model.ReferenceBound;
import com.google.api.services.genomics.model.SearchReadsRequest;
import com.google.api.services.genomics.model.SearchVariantsRequest;
import com.google.common.base.Function;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.genomics.v1.StreamReadsRequest;
import com.google.genomics.v1.StreamVariantsRequest;

/**
 * Utility methods for creating sharded reads or variants request objects from contiguous
 * region of the genome for use in parallel processing pipelines.
 * 
 * DEV NOTE: Sharding can be tricky to get right.
 *  - Shuffling of shards is important for good request distribution.
 *  - The actual amount of data within each API shard can vary greatly per dataset.
 *  - Shard sizing is important.
 *  --- Shards that are very small are inefficient.
 *  --- Shards need to be retried (e.g., due to temporary network issues) result in
 *      large quantities of repeated work if they are too big.
 *  - The total number of shards is important.
 *  --- If we have fewer than the number of threads/core available, resources
 *      may be underutilized.
 *  --- If we have just a few more shards than the number of threads/core available,
 *      resources may be underutilized.
 *  --- If we have too many, depending on the algorithm, the amount of data to
 *      be shuffled for the next step could be quite large, if a combiner is not used.
 *  - Shard strategies can also differ based on paginated vs. streaming APIs.
 *  
 * This is clearly a work in progress, many of the issues described above are not addressed
 * in the code below.  But let's consolidate the tribal knowledge here for best practices
 * in manual sharding for reuse by Spark or any other systems processing data in parallel.
 * 
 * For Dataflow, custom sources should be preferred over this manual sharding approach.
 *
 */
public class ShardUtils {

  /**
   * Some analyses should not include data from the sex chromosomes.
   */
  public enum SexChromosomeFilter { 
    /**
     * Include data from the sex chromosomes in the shards.
     */
    INCLUDE_XY,
    /**
     * Exclude data from the sex chromosomes from the shards.
     */
    EXCLUDE_XY
    }

  /**
   * Constructs sharded StreamVariantsRequests for the specified contiguous region(s) of the genome.
   * 
   * @param variantSetId The variantSetId.
   * @param references The specified contiguous region(s) of the genome.
   * @param numberOfBasesPerShard The maximum number of bases to include per shard.
   * @return The shuffled list of sharded request objects.
   */
  public static ImmutableList<StreamVariantsRequest> getVariantRequests(final String variantSetId,
      String references, long numberOfBasesPerShard) {
    Iterable<Contig> shards = getSpecifiedShards(references, numberOfBasesPerShard);
    return FluentIterable.from(shards)
        .transform(new Function<Contig, StreamVariantsRequest>() {
          @Override
          public StreamVariantsRequest apply(Contig shard) {
            return shard.getStreamVariantsRequest(variantSetId);
          }
        }).toList();
  }

  /**
   * Constructs sharded SearchVariantsRequests for the specified contiguous region(s) of the genome.
   * 
   * @param variantSetId The variantSetId.
   * @param references The specified contiguous region(s) of the genome.
   * @param numberOfBasesPerShard The maximum number of bases to include per shard.
   * @return The shuffled list of sharded request objects.
   */
  @Deprecated // Remove this when fully migrated to gRPC.
  public static ImmutableList<SearchVariantsRequest> getPaginatedVariantRequests(final String variantSetId,
      String references, long numberOfBasesPerShard) {
    Iterable<Contig> shards = getSpecifiedShards(references, numberOfBasesPerShard);
    return FluentIterable.from(shards)
        .transform(new Function<Contig, SearchVariantsRequest>() {
          @Override
          public SearchVariantsRequest apply(Contig shard) {
            return shard.getSearchVariantsRequest(variantSetId);
          }
        }).toList();
  }
  
  /**
   * Constructs sharded StreamVariantsRequests for the all references in the variantSet.
   * 
   * @param variantSetId The variantSetId.
   * @param sexChromosomeFilter An enum value indicating how sex chromosomes should be
   *        handled in the result.
   * @param numberOfBasesPerShard The maximum number of bases to include per shard.
   * @param auth The OfflineAuth to be used to get the reference bounds for the variantSet.
   * @return The shuffled list of sharded request objects.
   * @throws IOException 
   * @throws GeneralSecurityException 
   */
  public static ImmutableList<StreamVariantsRequest> getVariantRequests(final String variantSetId,
      SexChromosomeFilter sexChromosomeFilter, long numberOfBasesPerShard,
      GenomicsFactory.OfflineAuth auth) throws IOException, GeneralSecurityException {
    Iterable<Contig> shards = getAllShardsInVariantSet(variantSetId,
        sexChromosomeFilter, numberOfBasesPerShard, auth);
    return FluentIterable.from(shards)
        .transform(new Function<Contig, StreamVariantsRequest>() {
          @Override
          public StreamVariantsRequest apply(Contig shard) {
            return shard.getStreamVariantsRequest(variantSetId);
          }
        }).toList();
  }

  /**
   * Constructs sharded SearchVariantsRequests for the all references in the variantSet.
   * 
   * @param variantSetId The variantSetId.
   * @param sexChromosomeFilter An enum value indicating how sex chromosomes should be
   *        handled in the result.
   * @param numberOfBasesPerShard The maximum number of bases to include per shard.
   * @param auth The OfflineAuth to be used to get the reference bounds for the variantSet.
   * @return The shuffled list of sharded request objects.
   * @throws IOException 
   * @throws GeneralSecurityException 
   */
  @Deprecated // Remove this when fully migrated to gRPC.
  public static ImmutableList<SearchVariantsRequest> getPaginatedVariantRequests(final String variantSetId,
      SexChromosomeFilter sexChromosomeFilter, long numberOfBasesPerShard,
      GenomicsFactory.OfflineAuth auth) throws IOException, GeneralSecurityException {
    Iterable<Contig> shards = getAllShardsInVariantSet(variantSetId,
        sexChromosomeFilter, numberOfBasesPerShard, auth);
    return FluentIterable.from(shards)
        .transform(new Function<Contig, SearchVariantsRequest>() {
          @Override
          public SearchVariantsRequest apply(Contig shard) {
            return shard.getSearchVariantsRequest(variantSetId);
          }
        }).toList();
  }
  
  /**
   * Constructs sharded StreamReadsRequests for the specified contiguous region(s) of the genome.
   * 
   * @param readGroupSetIds The list of readGroupSetIds.
   * @param references The specified contiguous region(s) of the genome.
   * @param numberOfBasesPerShard The maximum number of bases to include per shard.
   * @return The shuffled list of sharded request objects.
   */
  public static ImmutableList<StreamReadsRequest> getReadRequests(List<String> readGroupSetIds,
      String references, long numberOfBasesPerShard) {
    final Iterable<Contig> shards = getSpecifiedShards(references, numberOfBasesPerShard);

    // Work around lack of FluentIterable.shuffle() https://github.com/google/guava/issues/1358
    List<StreamReadsRequest> requests =
        Arrays.asList(FluentIterable.from(readGroupSetIds)
        .transformAndConcat(new Function<String, Iterable<StreamReadsRequest>>() {
          @Override
          public Iterable<StreamReadsRequest> apply(final String readGroupSetId) {
            return FluentIterable.from(shards)
                .transform(new Function<Contig, StreamReadsRequest>() {
                  @Override
                  public StreamReadsRequest apply(Contig shard) {
                    return shard.getStreamReadsRequest(readGroupSetId);
                  }
                });
          }
        }).toArray(StreamReadsRequest.class));
    // The shards were already shuffled, but now lets shuffle this list of concatenated shuffled requests.
    Collections.shuffle(requests);
    return FluentIterable.from(requests).toList();
  }

  /**
   * Constructs sharded SearchReadsRequests for the specified contiguous region(s) of the genome.
   * 
   * @param readGroupSetIds The list of readGroupSetIds.
   * @param references The specified contiguous region(s) of the genome.
   * @param numberOfBasesPerShard The maximum number of bases to include per shard.
   * @return The shuffled list of sharded request objects.
   */
  @Deprecated // Remove this when fully migrated to gRPC.
  public static ImmutableList<SearchReadsRequest> getPaginatedReadRequests(List<String> readGroupSetIds,
      String references, long numberOfBasesPerShard) {
    final Iterable<Contig> shards = getSpecifiedShards(references, numberOfBasesPerShard);

    // Work around lack of FluentIterable.shuffle() https://github.com/google/guava/issues/1358
   List<SearchReadsRequest> requests =
        Arrays.asList(FluentIterable.from(readGroupSetIds)
        .transformAndConcat(new Function<String, Iterable<SearchReadsRequest>>() {
          @Override
          public Iterable<SearchReadsRequest> apply(final String readGroupSetId) {
            return FluentIterable.from(shards)
                .transform(new Function<Contig, SearchReadsRequest>() {
                  @Override
                  public SearchReadsRequest apply(Contig shard) {
                    return shard.getSearchReadsRequest(readGroupSetId);
                  }
                });
          }
        }).toArray(SearchReadsRequest.class));
    // The shards were already shuffled, but now lets shuffle this list of concatenated shuffled requests.
    Collections.shuffle(requests);
    return FluentIterable.from(requests).toList();
  }

  /**
   * Constructs StreamReadsRequest for the readGroupSetIds, assuming that the user wants to
   * include all references.
   * 
   * TODO: Should this be sharded - by the referenceBounds for the associated referenceSet
   *     and/or by read groups?
   * 
   * @param readGroupSetIds The readGroupSetIds.
   * @return The shuffled list of sharded request objects.
   */
  public static ImmutableList<StreamReadsRequest> getReadRequests(List<String> readGroupSetIds) {
    // Work around lack of FluentIterable.shuffle() https://github.com/google/guava/issues/1358
    List<StreamReadsRequest> requests =
        Arrays.asList(FluentIterable.from(readGroupSetIds)
        .transform(new Function<String, StreamReadsRequest>() {
          @Override
          public StreamReadsRequest apply(String readGroupSetId) {
            return StreamReadsRequest.newBuilder()
                .setReadGroupSetId(readGroupSetId)
                .build();
          }
        }).toArray(StreamReadsRequest.class));
    Collections.shuffle(requests);
    return FluentIterable.from(requests).toList();
  }

  /**
   * Retrieve the list of all the reference names and their start=0/end positions for the variantSet.
   * 
   * Note that start is hardcoded to zero since the referenceBounds only include the upper bound. 
   * 
   * @param variantSetId - The id of the variantSet to query.
   * @param sexChromosomeFilter - An enum value indicating how sex chromosomes should be
   *        handled in the result.
   * @return The list of all references in the variantSet.
   * @throws IOException
   * @throws GeneralSecurityException 
   */
  private static List<Contig> getContigsInVariantSet(String variantSetId,
      SexChromosomeFilter sexChromosomeFilter, GenomicsFactory.OfflineAuth auth)
          throws IOException, GeneralSecurityException {
    List<Contig> contigs = Lists.newArrayList();
    for (ReferenceBound bound : GenomicsUtils.getReferenceBounds(variantSetId, auth)) {
      String contig = bound.getReferenceName().toLowerCase();
      if (sexChromosomeFilter == SexChromosomeFilter.EXCLUDE_XY
          && (contig.contains("x") || contig.contains("y"))) {
        // X and Y can skew some analysis results
        continue;
      }
      contigs.add(new Contig(bound.getReferenceName(), 0, bound.getUpperBound()));
    }
    return contigs;
  }

  private static List<Contig> getAllShardsInVariantSet(String variantSetId,
      SexChromosomeFilter sexChromosomeFilter, long numberOfBasesPerShard,
      GenomicsFactory.OfflineAuth auth) throws IOException, GeneralSecurityException {
    List<Contig> contigs = getContigsInVariantSet(variantSetId, sexChromosomeFilter, auth);
    return ShardUtils.getAllShardsForContigs(contigs, numberOfBasesPerShard);
  }

  private static List<Contig> getSpecifiedShards(String contigsArgument, long numberOfBasesPerShard) {
    Iterable<Contig> contigs = Contig.parseContigsFromCommandLine(contigsArgument);
    return ShardUtils.getAllShardsForContigs(contigs, numberOfBasesPerShard);
  }

  private static List<Contig> getAllShardsForContigs(Iterable<Contig> contigs, long numberOfBasesPerShard) {
    List<Contig> shardedContigs = Lists.newArrayList();
    for (Contig contig : contigs) {
      shardedContigs.addAll(contig.getShards(numberOfBasesPerShard));
    }
    // IMPORTANT: Shuffle shards for better backend performance.
    Collections.shuffle(shardedContigs);
    return shardedContigs;
  }

}