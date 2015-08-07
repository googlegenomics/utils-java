package com.google.cloud.genomics.utils.grpc;

import static org.junit.Assert.*;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.Iterator;

import org.junit.BeforeClass;
import org.junit.Test;

import com.google.cloud.genomics.utils.IntegrationTestHelper;
import com.google.cloud.genomics.utils.ShardUtils;
import com.google.common.collect.ImmutableList;
import com.google.genomics.v1.StreamReadsRequest;
import com.google.genomics.v1.StreamReadsResponse;

public class ReadStreamIteratorITCase {

  static IntegrationTestHelper helper;
  
  @BeforeClass
  public static void setUpBeforeClass() throws Exception {
    helper = new IntegrationTestHelper();
  }
  
  @Test
  public void testBasic() throws IOException, GeneralSecurityException {
    ImmutableList<StreamReadsRequest> requests =
        ShardUtils.getReadRequests(Collections.singletonList(helper.PLATINUM_GENOMES_READGROUPSETS[0]),
        helper.PLATINUM_GENOMES_KLOTHO_REFERENCES, 100L);
    assertEquals(1, requests.size());
    
    // TODO: switch this to helper.getAuth() to use api key once gRPC is no longer behind a whitelist.
    // At that time, application default credentials would also work.  Right now application default credentials
    // will not work locally since they come from a gcloud project not in the whitelist.  Application default
    // credentials do work fine on Google Compute Engine if running in a project in the whitelist.
    // https://github.com/googlegenomics/utils-java/issues/51
    Iterator<StreamReadsResponse> iter = new ReadStreamIterator(requests.get(0),
        helper.getAuthWithUserCredentials());
    
    assertTrue(iter.hasNext());
    StreamReadsResponse readResponse = iter.next();
    assertEquals(55, readResponse.getAlignmentsList().size());
    assertFalse(iter.hasNext());
  }

}
