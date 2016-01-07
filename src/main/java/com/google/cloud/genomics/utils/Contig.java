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

import static com.google.common.base.Objects.equal;
import static com.google.common.collect.Lists.newArrayList;
import static java.util.Objects.hash;
import static java.util.Objects.requireNonNull;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.google.api.client.util.Preconditions;
import com.google.api.services.genomics.model.SearchReadsRequest;
import com.google.api.services.genomics.model.SearchVariantsRequest;
import com.google.common.base.Function;
import com.google.common.base.Splitter;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.genomics.v1.StreamReadsRequest;
import com.google.genomics.v1.StreamVariantsRequest;

/**
 * A Contig is a contiguous region of the genome.
 */
public class Contig implements Serializable {

  private static final long serialVersionUID = -1730387112193404207L;

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


  /**
   * Parse the list of Contigs expressed in the string argument.
   * 
   * The common use case is to parse the value of a command line parameter.
   * 
   * @param contigsArgument - a string expressing the specified contiguous region(s) of the genome.
   *                          The format is chromosome:start:end[,chromosome:start:end]
   * @return a list of Contig objects
   */
  public static Iterable<Contig> parseContigsFromCommandLine(String contigsArgument) {
    return Iterables.transform(Splitter.on(",").split(contigsArgument),
        new Function<String, Contig>() {
          @Override
          public Contig apply(String contigString) {
            ArrayList<String> contigInfo = newArrayList(Splitter.on(":").split(contigString));
            Long start = Long.valueOf(contigInfo.get(1));
            Long end = Long.valueOf(contigInfo.get(2));
            Preconditions.checkArgument(start <= end,
                "Contig coordinates are incorrectly specified: start " + start + " is greater than end " + end);
            return new Contig(contigInfo.get(0), start, end);
          }
        });
  }
  
  // The following methods have package scope and are helpers for ShardUtils. For sharded Contigs,
  // the ShardUtils methods should be used to ensure that shards are shuffled all together before
  // being returned to clients.
  List<Contig> getShards(long numberOfBasesPerShard) {
    double shardCount = Math.ceil(end - start) / (double) numberOfBasesPerShard;
    List<Contig> shards = Lists.newArrayList();
    for (int i = 0; i < shardCount; i++) {
      long shardStart = start + (i * numberOfBasesPerShard);
      long shardEnd = Math.min(end, shardStart + numberOfBasesPerShard);

      shards.add(new Contig(referenceName, shardStart, shardEnd));
    }
    return shards;
  }
  
  @Deprecated // Remove this when fully migrated to gRPC.
  SearchVariantsRequest getSearchVariantsRequest(String variantSetId) {
    return new SearchVariantsRequest()
        .setVariantSetIds(Collections.singletonList(variantSetId))
        .setReferenceName(referenceName)
        .setStart(start)
        .setEnd(end);
  }

  StreamVariantsRequest getStreamVariantsRequest(String variantSetId) {
    return StreamVariantsRequest.newBuilder()
        .setVariantSetId(variantSetId)
        .setReferenceName(referenceName)
        .setStart(start)
        .setEnd(end)
        .build();
  }

  @Deprecated // Remove this when fully migrated to gRPC.
  SearchReadsRequest getSearchReadsRequest(String readGroupSetId) {
    return new SearchReadsRequest()
        .setReadGroupSetIds(Collections.singletonList(readGroupSetId))
        .setReferenceName(referenceName)
        .setStart(start)
        .setEnd(end);
  }

  StreamReadsRequest getStreamReadsRequest(String readGroupSetId) {
    return StreamReadsRequest.newBuilder()
        .setReadGroupSetId(readGroupSetId)
        .setReferenceName(referenceName)
        .setStart(start)
        .setEnd(end)
        .build();
  }

}

