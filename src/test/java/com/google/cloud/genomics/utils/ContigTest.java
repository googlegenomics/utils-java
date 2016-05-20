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

import static com.google.common.collect.Lists.newArrayList;

import static org.hamcrest.Matchers.containsString;
import static org.junit.Assert.assertEquals;

import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableSet;
import com.google.genomics.v1.StreamReadsRequest;
import com.google.genomics.v1.StreamVariantsRequest;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.List;

@RunWith(JUnit4.class)
public class ContigTest {

  @Rule
  public ExpectedException thrown = ExpectedException.none();

  @Test
  public void testGetShards() throws Exception {
    Contig contig = new Contig("1", 0, 9);
    List<Contig> shards = contig.getShards(5);

    assertEquals(2, shards.size());
    Contig shard1 = shards.get(0);
    Contig shard2 = shards.get(1);

    // The code shuffles the shard, so lets make sure that we test the right shards
    if (shard1.start > shard2.start) {
      shard1 = shards.get(1);
      shard2 = shards.get(0);
    }

    assertEquals("1", shard1.referenceName);
    assertEquals(0, shard1.start);
    assertEquals(5, shard1.end);

    assertEquals("1", shard2.referenceName);
    assertEquals(5, shard2.start);
    assertEquals(9, shard2.end);
  }

  @Test
  public void testGetVariantsRequest() throws Exception {
    StreamVariantsRequest prototype = StreamVariantsRequest.newBuilder()
        .setProjectId("theProjectId")
        .setVariantSetId("theVariantSetId")
        .build();
    StreamVariantsRequest request = new Contig("1", 0, 9)
      .getStreamVariantsRequest(prototype);
    assertEquals("theProjectId", request.getProjectId());
    assertEquals("theVariantSetId", request.getVariantSetId());
    assertEquals("1", request.getReferenceName());
    assertEquals(0, request.getStart());
    assertEquals(9, request.getEnd());
  }

  @Test
  public void testGetVariantsRequestWithCallSetIds() throws Exception {
    ImmutableSet<String> callSetIds = ImmutableSet.<String>builder()
        .add("callSetId-0")
        .add("callSetId-1")
        .build();
    StreamVariantsRequest prototype = StreamVariantsRequest.newBuilder()
        .setProjectId("theProjectId")
        .setVariantSetId("theVariantSetId")
        .addAllCallSetIds(callSetIds)
        .build();
    StreamVariantsRequest request = new Contig("1", 0, 9)
      .getStreamVariantsRequest(prototype);
    assertEquals("theProjectId", request.getProjectId());
    assertEquals("theVariantSetId", request.getVariantSetId());
    assertEquals(2, request.getCallSetIdsList().size());
    assertEquals("callSetId-0", request.getCallSetIds(0));
    assertEquals("callSetId-1", request.getCallSetIds(1));
    assertEquals("1", request.getReferenceName());
    assertEquals(0, request.getStart());
    assertEquals(9, request.getEnd());
  }

  @Test
  public void testGetVariantsRequestWithEmptyCallSetIds() throws Exception {
    ImmutableSet<String> callSetIds = ImmutableSet.<String>builder()
        .build();
    StreamVariantsRequest prototype = StreamVariantsRequest.newBuilder()
        .setProjectId("theProjectId")
        .setVariantSetId("theVariantSetId")
        .addAllCallSetIds(callSetIds)
        .build();
    StreamVariantsRequest request = new Contig("1", 0, 9)
      .getStreamVariantsRequest(prototype);
    assertEquals("theProjectId", request.getProjectId());
    assertEquals("theVariantSetId", request.getVariantSetId());
    assertEquals(0, request.getCallSetIdsList().size());
    assertEquals("1", request.getReferenceName());
    assertEquals(0, request.getStart());
    assertEquals(9, request.getEnd());
  }

  @Test
  public void testGetReadsRequest() throws Exception {
    StreamReadsRequest prototype = StreamReadsRequest.newBuilder()
        .setProjectId("theProjectId")
        .setReadGroupSetId("theReadGroupSetId")
        .build();
    StreamReadsRequest request = new Contig("1", 0, 9)
      .getStreamReadsRequest(prototype);
    assertEquals("theProjectId", request.getProjectId());
    assertEquals("theReadGroupSetId", request.getReadGroupSetId());
    assertEquals("1", request.getReferenceName());
    assertEquals(0, request.getStart());
    assertEquals(9, request.getEnd());
  }

  @Test
  public void testParseContigs() {
    Contig brca1Contig = new Contig("17", 41196311, 41277499);
    String brca1ContigString = "17:41196311:41277499";

    Contig klothoContig = new Contig("13", 33628137, 33628138);
    String klothoContigString = "13:33628137:33628138";

    assertEquals(newArrayList(brca1Contig),
        newArrayList(Contig.parseContigsFromCommandLine(brca1ContigString)));

    assertEquals(newArrayList(brca1Contig, klothoContig),
        newArrayList(Contig.parseContigsFromCommandLine((Joiner.on(",").join(
            brca1ContigString, klothoContigString)))));
  }

  @Test
  public void testParseContigsValidation() {
    String contigEndBeforeStart = "17:41277499:41196311";
    thrown.expect(IllegalArgumentException.class);
    thrown.expectMessage(containsString("Contig coordinates are incorrectly specified"));
    Iterable<Contig> contigs = Contig.parseContigsFromCommandLine(contigEndBeforeStart);
    contigs.toString();  // The operation is lazy, force it here.
  }

}
