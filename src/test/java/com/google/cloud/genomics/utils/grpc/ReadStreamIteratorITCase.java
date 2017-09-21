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
package com.google.cloud.genomics.utils.grpc;

import static org.hamcrest.Matchers.containsString;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import com.google.cloud.genomics.utils.IntegrationTestHelper;
import com.google.cloud.genomics.utils.ShardBoundary;
import com.google.cloud.genomics.utils.ShardUtils;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;
import com.google.genomics.v1.Read;
import com.google.genomics.v1.StreamReadsRequest;
import com.google.genomics.v1.StreamReadsResponse;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

@RunWith(JUnit4.class)
public class ReadStreamIteratorITCase {
  // This small interval overlaps the Klotho SNP.
  static final String REFERENCES = "chr13:33628134:33628138";
  static final StreamReadsRequest PROTOTYPE = StreamReadsRequest.newBuilder()
      .setReadGroupSetId(IntegrationTestHelper.PLATINUM_GENOMES_READGROUPSETS[0])
      .setProjectId(IntegrationTestHelper.getTEST_PROJECT())
      .build();

  @Rule
  public ExpectedException thrown = ExpectedException.none();

  @Test
  public void testBasic() throws IOException, GeneralSecurityException {
    ImmutableList<StreamReadsRequest> requests =
        ShardUtils.getReadRequests(Collections.singletonList(PROTOTYPE),
        100L, REFERENCES);
    assertEquals(1, requests.size());

    Iterator<StreamReadsResponse> iter =
        ReadStreamIterator.enforceShardBoundary(IntegrationTestHelper.getAuthFromApplicationDefaultCredential(),
            requests.get(0),
            ShardBoundary.Requirement.OVERLAPS, null);

    assertTrue(iter.hasNext());
    StreamReadsResponse readResponse = iter.next();
    assertEquals(63, readResponse.getAlignmentsList().size());
    assertFalse(iter.hasNext());

    iter = ReadStreamIterator.enforceShardBoundary(IntegrationTestHelper.getAuthFromApplicationDefaultCredential(),
        requests.get(0),
        ShardBoundary.Requirement.STRICT, null);

    assertTrue(iter.hasNext());
    readResponse = iter.next();
    assertEquals(2, readResponse.getAlignmentsList().size());
    assertFalse(iter.hasNext());
  }

  @Test
  public void testPartialResponses() throws IOException, GeneralSecurityException {
    ImmutableList<StreamReadsRequest> requests =
        ShardUtils.getReadRequests(Collections.singletonList(PROTOTYPE),
        100L, REFERENCES);
    assertEquals(1, requests.size());

    Iterator<StreamReadsResponse> iter =
        ReadStreamIterator.enforceShardBoundary(IntegrationTestHelper.getAuthFromApplicationDefaultCredential(),
            requests.get(0),
            ShardBoundary.Requirement.STRICT, "alignments(alignment)");

    assertTrue(iter.hasNext());
    StreamReadsResponse readResponse = iter.next();
    List<Read> reads = readResponse.getAlignmentsList();
    assertEquals(2, reads.size());
    assertFalse(iter.hasNext());

    assertEquals("chr13", reads.get(0).getAlignment().getPosition().getReferenceName());
    assertEquals(33628135, reads.get(0).getAlignment().getPosition().getPosition());
    assertTrue(Strings.isNullOrEmpty(reads.get(0).getAlignedSequence()));
  }

  @Test
  public void testPartialResponsesInsufficientFields() throws IOException, GeneralSecurityException {
    thrown.expect(IllegalArgumentException.class);
    thrown.expectMessage(containsString("Insufficient fields requested in partial response. "
        + "At a minimum include 'alignments(alignment)' to enforce a strict shard boundary."));

    ImmutableList<StreamReadsRequest> requests =
        ShardUtils.getReadRequests(Collections.singletonList(PROTOTYPE),
        100L, REFERENCES);
    assertEquals(1, requests.size());

    Iterator<StreamReadsResponse> iter =
        ReadStreamIterator.enforceShardBoundary(IntegrationTestHelper.getAuthFromApplicationDefaultCredential(),
            requests.get(0),
            ShardBoundary.Requirement.STRICT, "alignments(alignedSequence)");
  }
}

