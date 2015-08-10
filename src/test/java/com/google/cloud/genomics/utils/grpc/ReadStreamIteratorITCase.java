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
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.Iterator;

import org.junit.BeforeClass;
import org.junit.Test;

import com.google.cloud.genomics.utils.IntegrationTestHelper;
import com.google.cloud.genomics.utils.ShardBoundary;
import com.google.cloud.genomics.utils.ShardUtils;
import com.google.common.collect.ImmutableList;
import com.google.genomics.v1.StreamReadsRequest;
import com.google.genomics.v1.StreamReadsResponse;


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
    
    // TODO: switch this to helper.getAuth() to use api key once gRPC is no longer behind a whitelist.
    // At that time, application default credentials would also work.  Right now application default credentials
    // will not work locally since they come from a gcloud project not in the whitelist.  Application default
    // credentials do work fine on Google Compute Engine if running in a project in the whitelist.
    // https://github.com/googlegenomics/utils-java/issues/51
    Iterator<StreamReadsResponse> iter = new ReadStreamIterator(requests.get(0),
        helper.getAuthWithUserCredentials(), ShardBoundary.Requirement.OVERLAPS, null);
    
    assertTrue(iter.hasNext());
    StreamReadsResponse readResponse = iter.next();
    assertEquals(57, readResponse.getAlignmentsList().size());
    assertFalse(iter.hasNext());

    iter = new ReadStreamIterator(requests.get(0),
        helper.getAuthWithUserCredentials(), ShardBoundary.Requirement.STRICT, null);
    
    assertTrue(iter.hasNext());
    readResponse = iter.next();
    assertEquals(2, readResponse.getAlignmentsList().size());
    assertFalse(iter.hasNext());
  }

}
