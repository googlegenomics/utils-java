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
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertEquals;

import java.util.Collections;
import java.util.List;

import org.hamcrest.CoreMatchers;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import com.google.api.services.genomics.model.SearchReadsRequest;
import com.google.api.services.genomics.model.SearchVariantsRequest;
import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.collect.Ordering;

@RunWith(JUnit4.class)
public class ContigTest {
  
  final Ordering<Contig> BY_REFERENCE_NAME = Ordering.natural().onResultOf(
      new Function<Contig, String>() {
        @Override
        public String apply(Contig contig) {
          return contig.referenceName;
        }
      });
  final Ordering<Contig> BY_START = Ordering.natural().onResultOf(
      new Function<Contig, Long>() {
        @Override
        public Long apply(Contig contig) {
          return contig.start;
        }
      });    
  final Ordering<Contig> BY_END = Ordering.natural().onResultOf(
      new Function<Contig, Long>() {
        @Override
        public Long apply(Contig contig) {
          return contig.end;
        }
      });
  final Ordering<Contig> CONTIG_ORDERING = BY_REFERENCE_NAME.compound(BY_START.compound(BY_END));


  @Test
  public void testGetShards() throws Exception {
    final Contig[] EXPECTED_RESULT = {
      new Contig("chr1", 0, 50),
      new Contig("chr1", 50, 100),
      new Contig("chr1", 100, 150),
      new Contig("chr2", 25, 75),
      new Contig("chr2", 75, 125),
      new Contig("chr2", 125, 175),
      new Contig("chr2", 175, 225),
      new Contig("chr2", 225, 250),
    };
    
    List<Contig> shards = Contig.getSpecifiedShards("chr1:0:150,chr2:25:250", 50);
    assertThat(shards, CoreMatchers.allOf(CoreMatchers.hasItems(EXPECTED_RESULT)));
    
    // Call it a second time, expect the same set of shards but in a different order.
    List<Contig> shards2 = Contig.getSpecifiedShards("chr1:0:150,chr2:25:250", 50);
    
    assertThat(shards, is(not(shards2)));
    Collections.sort(shards, CONTIG_ORDERING);
    Collections.sort(shards2, CONTIG_ORDERING);
    assertThat(shards, is(shards2));
  }

  @Test
  public void testGetVariantsRequest() throws Exception {
    SearchVariantsRequest request = new Contig("1", 0, 9).getVariantsRequest("vs");
    assertEquals("vs", request.getVariantSetIds().get(0));
    assertEquals("1", request.getReferenceName());
    assertEquals(0, request.getStart().longValue());
    assertEquals(9, request.getEnd().longValue());
  }

  @Test
  public void testGetReadsRequest() throws Exception {
    SearchReadsRequest request = new Contig("1", 0, 9).getReadsRequest("rs");
    assertEquals("rs", request.getReadGroupSetIds().get(0));
    assertEquals("1", request.getReferenceName());
    assertEquals(0, request.getStart().longValue());
    assertEquals(9, request.getEnd().longValue());
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

}
