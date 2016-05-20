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
import com.google.genomics.v1.StreamVariantsRequest;
import com.google.genomics.v1.StreamVariantsResponse;
import com.google.genomics.v1.Variant;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Iterator;
import java.util.List;

@RunWith(JUnit4.class)
public class VariantStreamIteratorITCase {
  public static final StreamVariantsRequest PROTOTYPE = StreamVariantsRequest.newBuilder()
      .setVariantSetId(IntegrationTestHelper.PLATINUM_GENOMES_VARIANTSET)
      .setProjectId(IntegrationTestHelper.getTEST_PROJECT())
      .build();

  @Rule
  public ExpectedException thrown = ExpectedException.none();

  @Test
  public void testBasic() throws IOException, GeneralSecurityException {
    ImmutableList<StreamVariantsRequest> requests =
        ShardUtils.getVariantRequests(PROTOTYPE,
            100L, IntegrationTestHelper.PLATINUM_GENOMES_KLOTHO_REFERENCES);
    assertEquals(1, requests.size());

    Iterator<StreamVariantsResponse> iter =
        VariantStreamIterator.enforceShardBoundary(IntegrationTestHelper.getAuthFromApplicationDefaultCredential(),
            requests.get(0),
            ShardBoundary.Requirement.OVERLAPS, null);

    assertTrue(iter.hasNext());
    StreamVariantsResponse variantResponse = iter.next();
    List<Variant> variants = variantResponse.getVariantsList();
    // This includes the klotho SNP and three non-variant segments which overlap it.
    assertEquals(4, variants.size());
    assertFalse(iter.hasNext());

    iter =
        VariantStreamIterator.enforceShardBoundary(IntegrationTestHelper.getAuthFromApplicationDefaultCredential(),
            requests.get(0),
            ShardBoundary.Requirement.STRICT, null);

    assertTrue(iter.hasNext());
    variantResponse = iter.next();
    // This includes only the klotho SNP.
    assertEquals(1, variantResponse.getVariantsList().size());
    assertFalse(iter.hasNext());
  }

  @Test
  public void testEmptyRegion() throws IOException, GeneralSecurityException {
    ImmutableList<StreamVariantsRequest> requests =
        ShardUtils.getVariantRequests(PROTOTYPE,
            100L, "chrDoesNotExist:100:200");
    assertEquals(1, requests.size());

    Iterator<StreamVariantsResponse> iter =
        VariantStreamIterator.enforceShardBoundary(IntegrationTestHelper.getAuthFromApplicationDefaultCredential(),
            requests.get(0),
            ShardBoundary.Requirement.OVERLAPS, null);
    assertFalse(iter.hasNext());

    iter =
        VariantStreamIterator.enforceShardBoundary(IntegrationTestHelper.getAuthFromApplicationDefaultCredential(),
            requests.get(0),
            ShardBoundary.Requirement.STRICT, null);
    assertFalse(iter.hasNext());
  }

  @Test
  public void testPartialResponses() throws IOException, GeneralSecurityException {
    ImmutableList<StreamVariantsRequest> requests =
        ShardUtils.getVariantRequests(PROTOTYPE,
            100L, IntegrationTestHelper.PLATINUM_GENOMES_KLOTHO_REFERENCES);
    assertEquals(1, requests.size());

    Iterator<StreamVariantsResponse> iter =
        VariantStreamIterator.enforceShardBoundary(IntegrationTestHelper.getAuthFromApplicationDefaultCredential(),
            requests.get(0),
            ShardBoundary.Requirement.STRICT, "variants(reference_name,start)");

    assertTrue(iter.hasNext());
    StreamVariantsResponse variantResponse = iter.next();
    List<Variant> variants = variantResponse.getVariantsList();
    // This includes only the klotho SNP.
    assertEquals(1, variants.size());
    assertFalse(iter.hasNext());

    assertEquals("chr13", variants.get(0).getReferenceName());
    assertEquals(33628137, variants.get(0).getStart());
    assertTrue(Strings.isNullOrEmpty(variants.get(0).getReferenceBases()));
  }

  @Test
  public void testPartialResponsesInsufficientFields() throws IOException, GeneralSecurityException {
    thrown.expect(IllegalArgumentException.class);
    thrown.expectMessage(containsString("Insufficient fields requested in partial response. "
        + "At a minimum include 'variants(start)' to enforce a strict shard boundary."));

    ImmutableList<StreamVariantsRequest> requests =
        ShardUtils.getVariantRequests(PROTOTYPE,
            100L, IntegrationTestHelper.PLATINUM_GENOMES_KLOTHO_REFERENCES);
    assertEquals(1, requests.size());

    Iterator<StreamVariantsResponse> iter =
        VariantStreamIterator.enforceShardBoundary(IntegrationTestHelper.getAuthFromApplicationDefaultCredential(),
            requests.get(0),
            ShardBoundary.Requirement.STRICT, "variants(reference_bases)");
  }
}
