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
    ImmutableList<StreamVariantsRequest> requests = ShardUtils.getVariantRequests(helper.PLATINUM_GENOMES_VARIANTSET,
        helper.PLATINUM_GENOMES_KLOTHO_REFERENCES, 100L);
    assertEquals(1, requests.size());
    Iterator<StreamVariantsResponse> iter = new VariantStreamIterator(requests.get(0), helper.getAuthWithUserCredentials());
    assertTrue(iter.hasNext());
    StreamVariantsResponse variantResponse = iter.next();
    assertEquals(4, variantResponse.getVariantsList().size());
    assertFalse(iter.hasNext());
  }

}
