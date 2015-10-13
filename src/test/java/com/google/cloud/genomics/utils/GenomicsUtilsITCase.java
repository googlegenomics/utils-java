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

import java.io.IOException;
import java.security.GeneralSecurityException;

import org.hamcrest.CoreMatchers;
import org.junit.BeforeClass;
import org.junit.Test;

public class GenomicsUtilsITCase {
  
  static IntegrationTestHelper helper;

  @BeforeClass
  public static void setUpBeforeClass() throws Exception {
    helper = new IntegrationTestHelper();
  }
  
  @Test
  public void testGetReadGroupSetIds() throws IOException, GeneralSecurityException {
    assertThat(GenomicsUtils.getReadGroupSetIds(helper.PLATINUM_GENOMES_DATASET, helper.getAuth()),
        CoreMatchers.allOf(CoreMatchers.hasItems(helper.PLATINUM_GENOMES_READGROUPSETS))); 
  }

  @Test
  public void testGetReferenceSetIdForReadGroupSet() throws IOException, GeneralSecurityException {
    assertEquals(helper.PLATINUM_GENOMES_REFERENCE_SET_ID,
        GenomicsUtils.getReferenceSetId(helper.PLATINUM_GENOMES_READGROUPSETS[0], helper.getAuth()));
  }
  
  @Test
  public void testGetVariantSetIds() throws IOException, GeneralSecurityException {
    assertThat(GenomicsUtils.getVariantSetIds(helper.PLATINUM_GENOMES_DATASET, helper.getAuth()),
        CoreMatchers.allOf(CoreMatchers.hasItems(helper.PLATINUM_GENOMES_VARIANTSET))); 
  }

  @Test
  public void testGetCallSetsNames() throws IOException, GeneralSecurityException {
    assertThat(GenomicsUtils.getCallSetsNames(helper.PLATINUM_GENOMES_VARIANTSET, helper.getAuth()),
        CoreMatchers.allOf(CoreMatchers.hasItems(helper.PLATINUM_GENOMES_CALLSET_NAMES))); 
  }

  @Test
  public void testGetReferenceBounds() throws IOException, GeneralSecurityException {
    assertThat(GenomicsUtils.getReferenceBounds(helper.PLATINUM_GENOMES_VARIANTSET, helper.getAuth()),
        CoreMatchers.allOf(CoreMatchers.hasItems(helper.PLATINUM_GENOMES_VARIANTSET_BOUNDS))); 
  }

}
