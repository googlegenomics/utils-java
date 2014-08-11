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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import com.google.api.services.genomics.model.Read;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public class ReadFlagUtilsTest {
  
  private static final Read ALL_READ = new Read();
  private static final Read NONE_READ = new Read();
  private static final Read NULL_FLAG_READ = new Read();
  
  @Before
  public void setReadFlags() {
    ALL_READ.setFlags(0xFFF);
    NONE_READ.setFlags(0x000);
  }
  
  // Test Getters
  
  @Test
  public void testHasReadPairedFlag() {
    assertTrue(ReadFlagUtils.hasReadPairedFlag(ALL_READ));
    assertFalse(ReadFlagUtils.hasReadPairedFlag(NONE_READ));
    assertFalse(ReadFlagUtils.hasReadPairedFlag(NULL_FLAG_READ));
  }
  
  @Test
  public void testHasProperPairFlag() {
    assertTrue(ReadFlagUtils.hasProperPairFlag(ALL_READ));
    assertFalse(ReadFlagUtils.hasProperPairFlag(NONE_READ));
    assertFalse(ReadFlagUtils.hasProperPairFlag(NULL_FLAG_READ));
  }
  
  @Test
  public void testHasReadUnmappedFlag() {
    assertTrue(ReadFlagUtils.hasReadUnmappedFlag(ALL_READ));
    assertFalse(ReadFlagUtils.hasReadUnmappedFlag(NONE_READ));
    assertFalse(ReadFlagUtils.hasReadUnmappedFlag(NULL_FLAG_READ));
  }
  
  @Test
  public void testHasMateUnmappedFlag() {
    assertTrue(ReadFlagUtils.hasMateUnmappedFlag(ALL_READ));
    assertFalse(ReadFlagUtils.hasMateUnmappedFlag(NONE_READ));
    assertFalse(ReadFlagUtils.hasMateUnmappedFlag(NULL_FLAG_READ));
  }
  
  @Test
  public void testHasReadStrandFlag() {
    assertTrue(ReadFlagUtils.hasReadStrandFlag(ALL_READ));
    assertFalse(ReadFlagUtils.hasReadStrandFlag(NONE_READ));
    assertFalse(ReadFlagUtils.hasReadStrandFlag(NULL_FLAG_READ));
  }
  
  @Test
  public void testHasMateStrandFlag() {
    assertTrue(ReadFlagUtils.hasMateStrandFlag(ALL_READ));
    assertFalse(ReadFlagUtils.hasMateStrandFlag(NONE_READ));
    assertFalse(ReadFlagUtils.hasMateStrandFlag(NULL_FLAG_READ));
  }
  
  @Test
  public void testHasFirstOfPairFlag() {
    assertTrue(ReadFlagUtils.hasFirstOfPairFlag(ALL_READ));
    assertFalse(ReadFlagUtils.hasFirstOfPairFlag(NONE_READ));
    assertFalse(ReadFlagUtils.hasFirstOfPairFlag(NULL_FLAG_READ));
  }
  
  @Test
  public void testHasSecondOfPairFlag() {
    assertTrue(ReadFlagUtils.hasSecondOfPairFlag(ALL_READ));
    assertFalse(ReadFlagUtils.hasSecondOfPairFlag(NONE_READ));
    assertFalse(ReadFlagUtils.hasSecondOfPairFlag(NULL_FLAG_READ));
  }
  
  @Test
  public void testHasNotPrimaryAlignmentFlag() {
    assertTrue(ReadFlagUtils.hasNotPrimaryAlignmentFlag(ALL_READ));
    assertFalse(ReadFlagUtils.hasNotPrimaryAlignmentFlag(NONE_READ));
    assertFalse(ReadFlagUtils.hasNotPrimaryAlignmentFlag(NULL_FLAG_READ));
  }
  
  @Test
  public void testHasReadFailsVendorQualityCheckFlag() {
    assertTrue(ReadFlagUtils.hasReadFailsVendorQualityCheckFlag(ALL_READ));
    assertFalse(ReadFlagUtils.hasReadFailsVendorQualityCheckFlag(NONE_READ));
    assertFalse(ReadFlagUtils.hasReadFailsVendorQualityCheckFlag(NULL_FLAG_READ));
  }
  
  @Test
  public void testHasDuplicateReadFlag() {
    assertTrue(ReadFlagUtils.hasDuplicateReadFlag(ALL_READ));
    assertFalse(ReadFlagUtils.hasDuplicateReadFlag(NONE_READ));
    assertFalse(ReadFlagUtils.hasDuplicateReadFlag(NULL_FLAG_READ));
  }
  
  @Test
  public void testHasSupplementaryAlignmentFlag() {
    assertTrue(ReadFlagUtils.hasSupplementaryAlignmentFlag(ALL_READ));
    assertFalse(ReadFlagUtils.hasSupplementaryAlignmentFlag(NONE_READ));
    assertFalse(ReadFlagUtils.hasSupplementaryAlignmentFlag(NULL_FLAG_READ));
  }
  
  // Test Setters
  
  @Test
  public void testSetReadPairedFlag() {
    assertFalse(ReadFlagUtils.hasReadPairedFlag(NONE_READ));
    ReadFlagUtils.setReadPairedFlag(NONE_READ, true);
    assertTrue(ReadFlagUtils.hasReadPairedFlag(NONE_READ));
    ReadFlagUtils.setReadPairedFlag(NONE_READ, false);
    assertFalse(ReadFlagUtils.hasReadPairedFlag(NONE_READ));
  }
  
  @Test
  public void testSetProperPairFlag() {
    assertFalse(ReadFlagUtils.hasProperPairFlag(NONE_READ));
    ReadFlagUtils.setProperPairFlag(NONE_READ, true);
    assertTrue(ReadFlagUtils.hasProperPairFlag(NONE_READ));
    ReadFlagUtils.setProperPairFlag(NONE_READ, false);
    assertFalse(ReadFlagUtils.hasProperPairFlag(NONE_READ));
  }
  
  @Test
  public void testSetReadUnmappedFlag() {
    assertFalse(ReadFlagUtils.hasReadUnmappedFlag(NONE_READ));
    ReadFlagUtils.setReadUnmappedFlag(NONE_READ, true);
    assertTrue(ReadFlagUtils.hasReadUnmappedFlag(NONE_READ));
    ReadFlagUtils.setReadUnmappedFlag(NONE_READ, false);
    assertFalse(ReadFlagUtils.hasReadUnmappedFlag(NONE_READ));
  }
  
  @Test
  public void testSetMateUnmappedFlag() {
    assertFalse(ReadFlagUtils.hasMateUnmappedFlag(NONE_READ));
    ReadFlagUtils.setMateUnmappedFlag(NONE_READ, true);
    assertTrue(ReadFlagUtils.hasMateUnmappedFlag(NONE_READ));
    ReadFlagUtils.setMateUnmappedFlag(NONE_READ, false);
    assertFalse(ReadFlagUtils.hasMateUnmappedFlag(NONE_READ));
  }
  
  @Test
  public void testSetReadStrandFlag() {
    assertFalse(ReadFlagUtils.hasReadStrandFlag(NONE_READ));
    ReadFlagUtils.setReadStrandFlag(NONE_READ, true);
    assertTrue(ReadFlagUtils.hasReadStrandFlag(NONE_READ));
    ReadFlagUtils.setReadStrandFlag(NONE_READ, false);
    assertFalse(ReadFlagUtils.hasReadStrandFlag(NONE_READ));
  }
  
  @Test
  public void testSetMateStrandFlag() {
    assertFalse(ReadFlagUtils.hasMateStrandFlag(NONE_READ));
    ReadFlagUtils.setMateStrandFlag(NONE_READ, true);
    assertTrue(ReadFlagUtils.hasMateStrandFlag(NONE_READ));
    ReadFlagUtils.setMateStrandFlag(NONE_READ, false);
    assertFalse(ReadFlagUtils.hasMateStrandFlag(NONE_READ));
  }
  
  @Test
  public void testSetFirstOfPairFlag() {
    assertFalse(ReadFlagUtils.hasFirstOfPairFlag(NONE_READ));
    ReadFlagUtils.setFirstOfPairFlag(NONE_READ, true);
    assertTrue(ReadFlagUtils.hasFirstOfPairFlag(NONE_READ));
    ReadFlagUtils.setFirstOfPairFlag(NONE_READ, false);
    assertFalse(ReadFlagUtils.hasFirstOfPairFlag(NONE_READ));
  }
  
  @Test
  public void testSetSecondOfPairFlag() {
    assertFalse(ReadFlagUtils.hasSecondOfPairFlag(NONE_READ));
    ReadFlagUtils.setSecondOfPairFlag(NONE_READ, true);
    assertTrue(ReadFlagUtils.hasSecondOfPairFlag(NONE_READ));
    ReadFlagUtils.setSecondOfPairFlag(NONE_READ, false);
    assertFalse(ReadFlagUtils.hasSecondOfPairFlag(NONE_READ));
  }
  
  @Test
  public void testSetNotPrimaryAlignmentFlag() {
    assertFalse(ReadFlagUtils.hasNotPrimaryAlignmentFlag(NONE_READ));
    ReadFlagUtils.setNotPrimaryAlignmentFlag(NONE_READ, true);
    assertTrue(ReadFlagUtils.hasNotPrimaryAlignmentFlag(NONE_READ));
    ReadFlagUtils.setNotPrimaryAlignmentFlag(NONE_READ, false);
    assertFalse(ReadFlagUtils.hasNotPrimaryAlignmentFlag(NONE_READ));
  }
  
  @Test
  public void testSetReadFailsVendorQualityCheckFlag() {
    assertFalse(ReadFlagUtils.hasReadFailsVendorQualityCheckFlag(NONE_READ));
    ReadFlagUtils.setReadFailsVendorQualityCheckFlag(NONE_READ, true);
    assertTrue(ReadFlagUtils.hasReadFailsVendorQualityCheckFlag(NONE_READ));
    ReadFlagUtils.setReadFailsVendorQualityCheckFlag(NONE_READ, false);
    assertFalse(ReadFlagUtils.hasReadFailsVendorQualityCheckFlag(NONE_READ));
  }
  
  @Test
  public void testSetDuplicateReadFlag() {
    assertFalse(ReadFlagUtils.hasDuplicateReadFlag(NONE_READ));
    ReadFlagUtils.setDuplicateReadFlag(NONE_READ, true);
    assertTrue(ReadFlagUtils.hasDuplicateReadFlag(NONE_READ));
    ReadFlagUtils.setDuplicateReadFlag(NONE_READ, false);
    assertFalse(ReadFlagUtils.hasDuplicateReadFlag(NONE_READ));
  }
  
  @Test
  public void testSetSupplementaryAlignmentFlag() {
    assertFalse(ReadFlagUtils.hasSupplementaryAlignmentFlag(NONE_READ));
    ReadFlagUtils.setSupplementaryAlignmentFlag(NONE_READ, true);
    assertTrue(ReadFlagUtils.hasSupplementaryAlignmentFlag(NONE_READ));
    ReadFlagUtils.setSupplementaryAlignmentFlag(NONE_READ, false);
    assertFalse(ReadFlagUtils.hasSupplementaryAlignmentFlag(NONE_READ));
  }
}
