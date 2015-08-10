/*
 * Copyright (C) 2015 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package com.google.cloud.genomics.utils;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.MatcherAssert.assertThat;

import java.util.Arrays;
import java.util.List;

import org.hamcrest.CoreMatchers;
import org.junit.Test;

import com.google.api.services.genomics.model.SearchReadsRequest;
import com.google.api.services.genomics.model.SearchVariantsRequest;
import com.google.genomics.v1.StreamReadsRequest;
import com.google.genomics.v1.StreamVariantsRequest;

public class ShardUtilsTest {

  @Test
  public void testGetVariantRequestsStringStringLong() {
    final StreamVariantsRequest[] EXPECTED_RESULT = {
        new Contig("chr17", 41196311, 41246311)
          .getStreamVariantsRequest("variantset1"),
        new Contig("chr17", 41246311, 41277499)
          .getStreamVariantsRequest("variantset1")
    };
    assertThat(ShardUtils.getVariantRequests("variantset1", "chr17:41196311:41277499", 50000L),
        CoreMatchers.allOf(CoreMatchers.hasItems(EXPECTED_RESULT)));
  }

  @Test
  public void testGetPaginatedVariantRequestsStringStringLong() {
    final SearchVariantsRequest[] EXPECTED_RESULT = {
        new Contig("chr17", 41196311, 41246311)
          .getSearchVariantsRequest("variantset1"),
        new Contig("chr17", 41246311, 41277499)
          .getSearchVariantsRequest("variantset1")
    };
    assertThat(ShardUtils.getPaginatedVariantRequests("variantset1", "chr17:41196311:41277499", 50000L),
        CoreMatchers.allOf(CoreMatchers.hasItems(EXPECTED_RESULT)));
  }

  @Test
  public void testGetReadRequestsListOfStringStringLong() {
    final StreamReadsRequest[] EXPECTED_RESULT = {
        new Contig("chr17", 41196311, 41246311)
          .getStreamReadsRequest("readset1"),
        new Contig("chr17", 41246311, 41277499)
          .getStreamReadsRequest("readset1"),
          new Contig("chr17", 41196311, 41246311)
        .getStreamReadsRequest("readset2"),
      new Contig("chr17", 41246311, 41277499)
        .getStreamReadsRequest("readset2")
    };
    assertThat(ShardUtils.getReadRequests(Arrays.asList("readset1", "readset2"), "chr17:41196311:41277499", 50000L),
        CoreMatchers.allOf(CoreMatchers.hasItems(EXPECTED_RESULT)));
  }

  @Test
  public void testGetPaginatedReadRequests() {
    final SearchReadsRequest[] EXPECTED_RESULT = {
        new Contig("chr17", 41196311, 41246311)
          .getSearchReadsRequest("readset1"),
        new Contig("chr17", 41246311, 41277499)
          .getSearchReadsRequest("readset1"),
          new Contig("chr17", 41196311, 41246311)
        .getSearchReadsRequest("readset2"),
      new Contig("chr17", 41246311, 41277499)
        .getSearchReadsRequest("readset2")
    };
    assertThat(ShardUtils.getPaginatedReadRequests(Arrays.asList("readset1", "readset2"), "chr17:41196311:41277499", 50000L),
        CoreMatchers.allOf(CoreMatchers.hasItems(EXPECTED_RESULT)));
  }

  @Test
  public void testGetReadRequestsListOfString() {
    final StreamReadsRequest[] EXPECTED_RESULT = {
        StreamReadsRequest.newBuilder().setReadGroupSetId("readset1").build(),
        StreamReadsRequest.newBuilder().setReadGroupSetId("readset2").build(),
    };
    assertThat(ShardUtils.getReadRequests(Arrays.asList("readset1", "readset2")),
        CoreMatchers.allOf(CoreMatchers.hasItems(EXPECTED_RESULT)));
  }

  @Test
  public void testVariantShardsAreShuffled() throws Exception {
    final StreamVariantsRequest[] EXPECTED_RESULT = {
      new Contig("chr1", 0, 50)
      .getStreamVariantsRequest("variantset1"),
      new Contig("chr1", 50, 100)
      .getStreamVariantsRequest("variantset1"),
      new Contig("chr1", 100, 150)
      .getStreamVariantsRequest("variantset1"),
      new Contig("chr2", 25, 75)
      .getStreamVariantsRequest("variantset1"),
      new Contig("chr2", 75, 125)
      .getStreamVariantsRequest("variantset1"),
      new Contig("chr2", 125, 175)
      .getStreamVariantsRequest("variantset1"),
      new Contig("chr2", 175, 225)
      .getStreamVariantsRequest("variantset1"),
      new Contig("chr2", 225, 250)
      .getStreamVariantsRequest("variantset1"),
    };

    List<StreamVariantsRequest> requests = ShardUtils.getVariantRequests("variantset1", "chr1:0:150,chr2:25:250", 50);
    assertThat(requests, CoreMatchers.allOf(CoreMatchers.hasItems(EXPECTED_RESULT)));
    
    // Call it a second time, expect the same set of shards but in a different order.
    List<StreamVariantsRequest> requests2 = ShardUtils.getVariantRequests("variantset1", "chr1:0:150,chr2:25:250", 50);
    assertThat(requests2, CoreMatchers.allOf(CoreMatchers.hasItems(EXPECTED_RESULT)));

    // Lists have different orders for their elements.
    assertThat(requests, is(not(requests2)));
  }

  @Test
  public void testReadShardsAreShuffled() throws Exception {
    final StreamReadsRequest[] EXPECTED_RESULT = {
      new Contig("chr1", 0, 50)
      .getStreamReadsRequest("readset1"),
      new Contig("chr1", 50, 100)
      .getStreamReadsRequest("readset1"),
      new Contig("chr1", 100, 150)
      .getStreamReadsRequest("readset1"),
      new Contig("chr2", 25, 75)
      .getStreamReadsRequest("readset1"),
      new Contig("chr2", 75, 125)
      .getStreamReadsRequest("readset1"),
      new Contig("chr2", 125, 175)
      .getStreamReadsRequest("readset1"),
      new Contig("chr2", 175, 225)
      .getStreamReadsRequest("readset1"),
      new Contig("chr2", 225, 250)
      .getStreamReadsRequest("readset1"),
      new Contig("chr1", 0, 50)
      .getStreamReadsRequest("readset2"),
      new Contig("chr1", 50, 100)
      .getStreamReadsRequest("readset2"),
      new Contig("chr1", 100, 150)
      .getStreamReadsRequest("readset2"),
      new Contig("chr2", 25, 75)
      .getStreamReadsRequest("readset2"),
      new Contig("chr2", 75, 125)
      .getStreamReadsRequest("readset2"),
      new Contig("chr2", 125, 175)
      .getStreamReadsRequest("readset2"),
      new Contig("chr2", 175, 225)
      .getStreamReadsRequest("readset2"),
      new Contig("chr2", 225, 250)
      .getStreamReadsRequest("readset2"),
    };

    List<StreamReadsRequest> requests = ShardUtils.getReadRequests(Arrays.asList("readset1", "readset2"),
        "chr1:0:150,chr2:25:250", 50);
    assertThat(requests, CoreMatchers.allOf(CoreMatchers.hasItems(EXPECTED_RESULT)));
    
    // Call it a second time, expect the same set of shards but in a different order.
    List<StreamReadsRequest> requests2 = ShardUtils.getReadRequests(Arrays.asList("readset1", "readset2"),
        "chr1:0:150,chr2:25:250", 50);
    assertThat(requests2, CoreMatchers.allOf(CoreMatchers.hasItems(EXPECTED_RESULT)));

    // Lists have different orders for their elements.
    assertThat(requests, is(not(requests2)));
  }
}
