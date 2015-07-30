/*
 * Copyright (C) 2014 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.google.cloud.genomics.utils;

import com.google.api.services.genomics.Genomics;
import com.google.api.services.genomics.model.ReferenceBound;
import com.google.api.services.genomics.model.SearchReadsRequest;
import com.google.api.services.genomics.model.SearchVariantsRequest;
import com.google.api.services.genomics.model.VariantSet;
import com.google.common.base.Function;
import com.google.common.base.Splitter;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.genomics.v1.StreamReadsRequest;
import com.google.genomics.v1.StreamVariantsRequest;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.google.common.base.Objects.equal;
import static com.google.common.collect.Lists.newArrayList;
import static java.util.Objects.hash;
import static java.util.Objects.requireNonNull;

/**
 * This class encapsulates logic regarding genomic regions.
 *
 * It includes utility methods for sharding those regions appropriately
 * for use in parallel processing pipelines and for creating request
 * objects from those shards.
 *
 */
public class Contig implements Serializable {

  private static final long serialVersionUID = -1730387112193404207L;

  public static final long DEFAULT_NUMBER_OF_BASES_PER_SHARD = 1000000;

  public enum SexChromosomeFilter { INCLUDE_XY, EXCLUDE_XY }
  
  // If not running all contigs, we default to BRCA1
  public static final String BRCA1 = "17:41196311:41277499";

  public final String referenceName;
  public final long start;
  public final long end;

  public Contig(String referenceName, long start, long end) {
    this.referenceName = requireNonNull(referenceName);
    this.start = start;
    this.end = end;
  }
  @Override
  public int hashCode() {
    return hash(referenceName, start, end);
  }

  @Override
  public boolean equals(Object obj) {
    if (!(obj instanceof Contig)) {
      return false;
    }
    Contig c = (Contig) obj;
    return equal(referenceName, c.referenceName) && equal(start, c.start) && equal(end, c.end);
  }

  @Override
  public String toString() {
    return referenceName + ':' + start + ':' + end;
  }

  public SearchVariantsRequest getVariantsRequest(String variantSetId) {
    return new SearchVariantsRequest()
        .setVariantSetIds(Collections.singletonList(variantSetId))
        .setReferenceName(referenceName)
        .setStart(start)
        .setEnd(end);
  }

  public StreamVariantsRequest getStreamVariantsRequest(String variantSetId) {
    return StreamVariantsRequest.newBuilder()
        .setVariantSetId(variantSetId)
        .setReferenceName(referenceName)
        .setStart(start)
        .setEnd(end)
        .build();
  }

  public SearchReadsRequest getReadsRequest(String readGroupSetId) {
    return new SearchReadsRequest()
        .setReadGroupSetIds(Collections.singletonList(readGroupSetId))
        .setReferenceName(referenceName)
        .setStart(start)
        .setEnd(end);
  }

  public StreamReadsRequest getStreamReadsRequest(String readGroupSetId) {
    return StreamReadsRequest.newBuilder()
        .setReadGroupSetId(readGroupSetId)
        .setReferenceName(referenceName)
        .setStart(start)
        .setEnd(end)
        .build();
  }

  private List<Contig> getShards() {
    return getShards(DEFAULT_NUMBER_OF_BASES_PER_SHARD);
  }

  // This is private because the static methods to determine shards should be used
  // to ensure that they are shuffled all together before being returned to clients.
  private List<Contig> getShards(long numberOfBasesPerShard) {
    double shardCount = Math.ceil(end - start) / (double) numberOfBasesPerShard;
    List<Contig> shards = Lists.newArrayList();
    for (int i = 0; i < shardCount; i++) {
      long shardStart = start + (i * numberOfBasesPerShard);
      long shardEnd = Math.min(end, shardStart + numberOfBasesPerShard);

      shards.add(new Contig(referenceName, shardStart, shardEnd));
    }
    return shards;
  }

  /**
   * Parse the list of Contigs expressed in the command line argument.
   * 
   * @param contigsArgument - the command line parameter expressing the specified genomic regions
   *                            format is chromosome:start:end[,chromosome:start:end]
   * @return a list of contigs
   */
  public static Iterable<Contig> parseContigsFromCommandLine(final String contigsArgument) {
    return Iterables.transform(Splitter.on(",").split(contigsArgument),
        new Function<String, Contig>() {
          @Override
          public Contig apply(String contigString) {
            ArrayList<String> contigInfo = newArrayList(Splitter.on(":").split(contigString));
            return new Contig(contigInfo.get(0), Long.valueOf(contigInfo.get(1)), Long
                .valueOf(contigInfo.get(2)));
          }
        });
  }

  /**
   * Retrieve the list of all the reference names and their start/end positions for the variant set.
   * 
   * @param genomics - The {@link Genomics} stub.
   * @param variantSetId - The id of the variant set to query.
   * @param sexChromosomeFilter - An enum value indicating how sex chromosomes should be
   *        handled in the result.
   * @return The list of all references in the variant set.
   * @throws IOException
   */
  public static List<Contig> getContigsInVariantSet(Genomics genomics, String variantSetId,
      SexChromosomeFilter sexChromosomeFilter) throws IOException {
    List<Contig> contigs = Lists.newArrayList();

    VariantSet variantSet = genomics.variantsets().get(variantSetId).execute();
    for (ReferenceBound bound : variantSet.getReferenceBounds()) {
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
  
  /**
   * Get a list of Contigs representing sharded windows of all genomic regions within the variant set.
   * 
   * These are useful for pipelines that operate in parallel against data within a specific variant set.
   * 
   * @param genomics - the genomics client
   * @param variantSetId - the id of the variant set for which to create shards
   * @param sexChromosomeFilter - whether or not to include the sex chromosomes in the list of shards
   * @return a shuffled list of shards
   * @throws IOException
   */
  public static List<Contig> getAllShardsInVariantSet(final Genomics genomics, final String variantSetId,
      final SexChromosomeFilter sexChromosomeFilter) throws IOException {
    return getAllShardsInVariantSet(genomics, variantSetId, sexChromosomeFilter, DEFAULT_NUMBER_OF_BASES_PER_SHARD); 
  }
  
  /**
   * Get a list of Contigs representing sharded windows of all genomic regions within the variant set.
   * 
   * These are useful for pipelines that operate in parallel against data within a specific variant set.
   * 
   * @param genomics - the genomics client
   * @param variantSetId - the id of the variant set for which to create shards
   * @param sexChromosomeFilter - whether or not to include the sex chromosomes in the list of shards
   * @param numberOfBasesPerShard - the maximum size of the shard window in terms of number of bases
   * @return a shuffled list of shards
   * @throws IOException
   */
  public static List<Contig> getAllShardsInVariantSet(final Genomics genomics, final String variantSetId,
      final SexChromosomeFilter sexChromosomeFilter, final long numberOfBasesPerShard) throws IOException {
    List<Contig> contigs = getContigsInVariantSet(genomics, variantSetId, sexChromosomeFilter);
    return getAllShardsForContigs(contigs, numberOfBasesPerShard);
  }
  
  /**
   * Get a list of Contigs representing sharded windows of the specified genomic regions expressed in the command line argument.
   * 
   * These are useful for pipelines that operate in parallel against data within a specific variant set or read group set.
   * 
   * @param contigsArgument - the command line parameter expressing the specified genomic regions
   *                            format is chromosome:start:end[,chromosome:start:end]
   * @return a shuffled list of shards
   */
  public static List<Contig> getSpecifiedShards(final String contigsArgument) {
    return getSpecifiedShards(contigsArgument, DEFAULT_NUMBER_OF_BASES_PER_SHARD);
  }

  /**
   * Get a list of Contigs representing sharded windows of the specified genomic regions expressed in the command line argument.
   * 
   * These are useful for pipelines that operate in parallel against data within a specific variant set or read group set.
   * 
   * @param contigsArgument - the command line parameter expressing the specified genomic regions
   *                            format is chromosome:start:end[,chromosome:start:end]
   * @param numberOfBasesPerShard - the maximum size of the shard window in terms of number of bases
   * @return a shuffled list of shards
   */
  public static List<Contig> getSpecifiedShards(final String contigsArgument, final long numberOfBasesPerShard) {
    Iterable<Contig> contigs = parseContigsFromCommandLine(contigsArgument);
    return getAllShardsForContigs(contigs, numberOfBasesPerShard);
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

