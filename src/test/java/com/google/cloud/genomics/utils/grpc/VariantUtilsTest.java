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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.google.genomics.v1.Variant;
import com.google.genomics.v1.VariantCall;

public class VariantUtilsTest {

  @Test
  public void testIsSNP() {
    assertTrue(VariantUtils.IS_SNP.apply(Variant.newBuilder().setReferenceName("chr7")
        .setStart(200000).setEnd(200001).setReferenceBases("A").addAlternateBases("C").build()));

    // Deletion
    assertFalse(VariantUtils.IS_SNP.apply(Variant.newBuilder().setReferenceName("chr7")
        .setStart(200000).setEnd(200001).setReferenceBases("CA").addAlternateBases("C").build()));

    // Insertion
    assertFalse(VariantUtils.IS_SNP.apply(Variant.newBuilder().setReferenceName("chr7")
        .setStart(200000).setEnd(200001).setReferenceBases("C").addAlternateBases("CA").build()));

    // SNP and Insertion
    assertFalse(VariantUtils.IS_SNP.apply(Variant.newBuilder().setReferenceName("chr7")
        .setStart(200000).setEnd(200001).setReferenceBases("C").addAlternateBases("A")
        .addAlternateBases("CA").build()));

    // Block Records
    assertFalse(VariantUtils.IS_SNP.apply(Variant.newBuilder().setReferenceName("chr7")
        .setStart(200000).setEnd(200001).setReferenceBases("A").build()));
    assertFalse(VariantUtils.IS_SNP.apply(Variant.newBuilder().setReferenceName("chr7")
        .setStart(200000).setEnd(200001).setReferenceBases("A")
        .addAlternateBases(VariantUtils.GATK_NON_VARIANT_SEGMENT_ALT).build()));
  }

  @Test
  public void testIsVariant() {
    // SNPs
    assertFalse(VariantUtils.IS_NON_VARIANT_SEGMENT.apply(Variant.newBuilder()
        .setReferenceName("chr7").setStart(200000).setEnd(200001).setReferenceBases("A")
        .addAlternateBases("C").build()));

    // Insertions
    assertFalse(VariantUtils.IS_NON_VARIANT_SEGMENT.apply(Variant.newBuilder()
        .setReferenceName("chr7").setStart(200000).setEnd(200001).setReferenceBases("A")
        .addAlternateBases("AC").build()));

    // Deletions NOTE: These are all the same mutation, just encoded in different ways.
    assertFalse(VariantUtils.IS_NON_VARIANT_SEGMENT.apply(Variant.newBuilder()
        .setReferenceName("chr7").setStart(200000).setEnd(200001).setReferenceBases("CAG")
        .addAlternateBases("C").build()));
    assertFalse(VariantUtils.IS_NON_VARIANT_SEGMENT.apply(Variant.newBuilder()
        .setReferenceName("chr7").setStart(200000).setEnd(200001).setReferenceBases("AG").build()));

    // Multi-allelic sites
    assertFalse(VariantUtils.IS_NON_VARIANT_SEGMENT.apply(Variant.newBuilder()
        .setReferenceName("chr7").setStart(200000).setEnd(200001).setReferenceBases("A")
        .addAlternateBases("C").addAlternateBases("AC").build()));
    assertFalse(VariantUtils.IS_NON_VARIANT_SEGMENT.apply(Variant.newBuilder()
        .setReferenceName("chr7").setStart(200000).setEnd(200001).setReferenceBases("A")
        .addAlternateBases("C").addAlternateBases("G").build()));

    // Non-Variant Block Records
    assertTrue(VariantUtils.IS_NON_VARIANT_SEGMENT.apply(Variant.newBuilder()
        .setReferenceName("chr7").setStart(200000).setEnd(200001).setReferenceBases("A").build()));
    assertTrue(VariantUtils.IS_NON_VARIANT_SEGMENT.apply(Variant.newBuilder()
        .setReferenceName("chr7").setStart(200000).setEnd(200001).setReferenceBases("A")
        .addAlternateBases(VariantUtils.GATK_NON_VARIANT_SEGMENT_ALT).build()));
  }

  @Test
  public void testCallComparator() {
    assertTrue(0 == VariantUtils.CALL_COMPARATOR.compare(
        VariantCall.newBuilder().setCallSetName("NA12883").build(),
        VariantCall.newBuilder().setCallSetName("NA12883").build()));

    assertTrue(0 > VariantUtils.CALL_COMPARATOR.compare(
        VariantCall.newBuilder().setCallSetName("NA12883").build(),
        VariantCall.newBuilder().setCallSetName("NA12884").build()));

    assertTrue(0 < VariantUtils.CALL_COMPARATOR.compare(
        VariantCall.newBuilder().setCallSetName("NA12884").build(),
        VariantCall.newBuilder().setCallSetName("NA12883").build()));
  }
}
