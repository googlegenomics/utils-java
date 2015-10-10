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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import com.google.cloud.genomics.utils.IntegrationTestHelper;
import com.google.cloud.genomics.utils.ShardBoundary;
import com.google.cloud.genomics.utils.ShardUtils;
import com.google.common.collect.ImmutableList;
import com.google.genomics.v1.Read;
import com.google.genomics.v1.StreamReadsRequest;
import com.google.genomics.v1.StreamReadsResponse;
import com.google.genomics.v1.Variant;


public class ReadStreamIteratorITCase {
  // This small interval overlaps the Klotho SNP.
  static final String REFERENCES = "chr13:33628134:33628138";
  static IntegrationTestHelper helper;
  
  @BeforeClass
  public static void setUpBeforeClass() throws Exception {
    helper = new IntegrationTestHelper();
  }
  
  @Test
  public void testBasic() throws IOException, GeneralSecurityException {
    ImmutableList<StreamReadsRequest> requests =
        ShardUtils.getReadRequests(Collections.singletonList(helper.PLATINUM_GENOMES_READGROUPSETS[0]),
        REFERENCES, 100L);
    assertEquals(1, requests.size());
    
    Iterator<StreamReadsResponse> iter = new ReadStreamIterator(requests.get(0),
        helper.getAuth(), ShardBoundary.Requirement.OVERLAPS, null);
    
    assertTrue(iter.hasNext());
    StreamReadsResponse readResponse = iter.next();
    assertEquals(57, readResponse.getAlignmentsList().size());
    assertFalse(iter.hasNext());

    iter = new ReadStreamIterator(requests.get(0),
        helper.getAuth(), ShardBoundary.Requirement.STRICT, null);
    
    assertTrue(iter.hasNext());
    readResponse = iter.next();
    assertEquals(2, readResponse.getAlignmentsList().size());
    assertFalse(iter.hasNext());
  }

  @Test
  @Ignore
  // TODO https://github.com/googlegenomics/utils-java/issues/48
  public void testPartialResponses() throws IOException, GeneralSecurityException {
    ImmutableList<StreamReadsRequest> requests =
        ShardUtils.getReadRequests(Collections.singletonList(helper.PLATINUM_GENOMES_READGROUPSETS[0]),
        REFERENCES, 100L);
    assertEquals(1, requests.size());
    
    Iterator<StreamReadsResponse> iter = new ReadStreamIterator(requests.get(0),
        helper.getAuth(), ShardBoundary.Requirement.STRICT, "reads(alignments)");
    
    assertTrue(iter.hasNext());
    StreamReadsResponse readResponse = iter.next();
    List<Read> reads = readResponse.getAlignmentsList();
    assertEquals(2, reads.size());
    assertFalse(iter.hasNext());
    

    assertEquals("chr13", reads.get(0).getAlignment().getPosition().getReferenceName());
    assertEquals(33628134, reads.get(0).getAlignment().getPosition().getPosition());
    assertNull(reads.get(0).getAlignedSequence());
  }

}
