package com.google.cloud.genomics.utils.grpc;

import static org.junit.Assert.*;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Iterator;

import org.junit.BeforeClass;
import org.junit.Test;

import com.google.cloud.genomics.utils.IntegrationTestHelper;
import com.google.cloud.genomics.utils.ShardUtils;
import com.google.common.collect.ImmutableList;
import com.google.genomics.v1.StreamVariantsRequest;
import com.google.genomics.v1.StreamVariantsResponse;

public class VariantStreamIteratorITCase {

  static IntegrationTestHelper helper;
  
  @BeforeClass
  public static void setUpBeforeClass() throws Exception {
    helper = new IntegrationTestHelper();
  }
  
  @Test
  public void testBasic() throws IOException, GeneralSecurityException {
    ImmutableList<StreamVariantsRequest> requests =
        ShardUtils.getVariantRequests(helper.PLATINUM_GENOMES_VARIANTSET,
        helper.PLATINUM_GENOMES_KLOTHO_REFERENCES, 100L);
    assertEquals(1, requests.size());

    // TODO: switch this to helper.getAuth() to use api key once gRPC is no longer behind a whitelist.
    // At that time, application default credentials would also work.  Right now application default credentials
    // will not work locally since they come from a gcloud project not in the whitelist.  Application default
    // credentials do work fine on Google Compute Engine if running in a project in the whitelist.
    // https://github.com/googlegenomics/utils-java/issues/51
    Iterator<StreamVariantsResponse> iter = new VariantStreamIterator(requests.get(0),
        helper.getAuthWithUserCredentials());

    assertTrue(iter.hasNext());
    StreamVariantsResponse variantResponse = iter.next();
    assertEquals(4, variantResponse.getVariantsList().size());
    assertFalse(iter.hasNext());
  }

}
