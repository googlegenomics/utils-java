/*
 * Copyright (C) 2015 Google Inc.
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

import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertEquals;

import com.google.common.collect.Iterables;

import org.hamcrest.CoreMatchers;
import org.hamcrest.collection.IsIterableContainingInOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public class GenomicsUtilsITCase {

  @Test
  public void testGetReadGroupSetIds() throws Exception {
    assertThat(GenomicsUtils.getReadGroupSetIds(IntegrationTestHelper.PLATINUM_GENOMES_DATASET,
        IntegrationTestHelper.getAuthFromApiKey()),
        CoreMatchers.allOf(CoreMatchers.hasItems(IntegrationTestHelper.PLATINUM_GENOMES_READGROUPSETS)));
  }

  @Test
  public void testGetReferenceSetIdForReadGroupSet() throws Exception {
    assertEquals(IntegrationTestHelper.PLATINUM_GENOMES_REFERENCE_SET_ID,
        GenomicsUtils.getReferenceSetId(IntegrationTestHelper.PLATINUM_GENOMES_READGROUPSETS[0],
            IntegrationTestHelper.getAuthFromApiKey()));
  }

  @Test
  public void testGetVariantSetIds() throws Exception {
    assertThat(GenomicsUtils.getVariantSetIds(IntegrationTestHelper.PLATINUM_GENOMES_DATASET,
        IntegrationTestHelper.getAuthFromApiKey()),
        CoreMatchers.allOf(CoreMatchers.hasItems(IntegrationTestHelper.PLATINUM_GENOMES_VARIANTSET)));
  }

  @Test
  public void testGetCallSets() throws Exception {
    assertThat(Iterables.transform(GenomicsUtils.getCallSets(IntegrationTestHelper.PLATINUM_GENOMES_VARIANTSET,
        IntegrationTestHelper.getAuthFromApiKey()), CallSetUtils.GET_NAMES),
        IsIterableContainingInOrder.contains(IntegrationTestHelper.PLATINUM_GENOMES_CALLSET_NAMES));
  }

  @Test
  public void testGetCallSetsNames() throws Exception {
    assertThat(GenomicsUtils.getCallSetsNames(IntegrationTestHelper.PLATINUM_GENOMES_VARIANTSET,
        IntegrationTestHelper.getAuthFromApiKey()),
        CoreMatchers.allOf(CoreMatchers.hasItems(IntegrationTestHelper.PLATINUM_GENOMES_CALLSET_NAMES)));
  }

  @Test
  public void testGetReferenceBounds() throws Exception {
    assertThat(GenomicsUtils.getReferenceBounds(IntegrationTestHelper.PLATINUM_GENOMES_VARIANTSET,
        IntegrationTestHelper.getAuthFromApiKey()),
        CoreMatchers.allOf(CoreMatchers.hasItems(IntegrationTestHelper.PLATINUM_GENOMES_VARIANTSET_BOUNDS)));
  }

  @Test
  public void testGetReferenceBoundsApplicationDefaultCredential() throws Exception {
    assertThat(GenomicsUtils.getReferenceBounds(IntegrationTestHelper.PLATINUM_GENOMES_VARIANTSET,
        IntegrationTestHelper.getAuthFromApplicationDefaultCredential()),
        CoreMatchers.allOf(CoreMatchers.hasItems(IntegrationTestHelper.PLATINUM_GENOMES_VARIANTSET_BOUNDS)));
  }

}
