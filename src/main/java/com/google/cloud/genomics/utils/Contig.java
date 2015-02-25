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

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.google.common.base.Objects.equal;
import static com.google.common.collect.Lists.newArrayList;
import static java.util.Objects.hash;
import static java.util.Objects.requireNonNull;

public class Contig implements Serializable {

  private static final long serialVersionUID = -1730387112193404207L;

  public static final long DEFAULT_NUMBER_OF_BASES_PER_SHARD = 100000;

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

  public List<Contig> getShards(long numberOfBasesPerShard) {
    double shardCount = Math.ceil(end - start) / (double) numberOfBasesPerShard;
    List<Contig> shards = Lists.newArrayList();
    for (int i = 0; i < shardCount; i++) {
      long shardStart = start + (i * numberOfBasesPerShard);
      long shardEnd = Math.min(end, shardStart + numberOfBasesPerShard);

      shards.add(new Contig(referenceName, shardStart, shardEnd));
    }
    Collections.shuffle(shards); // Shuffle shards for better backend performance
    return shards;
  }

  public List<Contig> getShards() {
    return getShards(DEFAULT_NUMBER_OF_BASES_PER_SHARD);
  }

  public SearchVariantsRequest getVariantsRequest(String variantSetId) {
    return new SearchVariantsRequest()
        .setVariantSetIds(Collections.singletonList(variantSetId))
        .setReferenceName(referenceName)
        .setStart(start)
        .setEnd(end);
  }

  public SearchReadsRequest getReadsRequest(String readGroupSetId) {
    return new SearchReadsRequest()
        .setReadGroupSetIds(Collections.singletonList(readGroupSetId))
        .setReferenceName(referenceName)
        .setStart(start)
        .setEnd(end);
  }

  public static Iterable<Contig> parseContigsFromCommandLine(String contigsArgument) {
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
}

