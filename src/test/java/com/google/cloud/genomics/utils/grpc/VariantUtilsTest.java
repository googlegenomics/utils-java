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

import com.google.genomics.v1.Variant;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

@RunWith(JUnit4.class)
public class VariantUtilsTest {

  // For test readability, create alias for this constant.
  public static final String GATK_ALT = VariantUtils.GATK_NON_VARIANT_SEGMENT_ALT;

  @Test
  public void testIsMultiNucleotide() {
    // missing reference
    assertFalse(VariantUtils.IS_MULTI_NUCLEOTIDE.apply(Variant.newBuilder().addAlternateBases("G").build()));

    // Standard SNP
    assertFalse(VariantUtils.IS_MULTI_NUCLEOTIDE.apply(
        Variant.newBuilder().setReferenceBases("A").addAlternateBases("G").build()));

    // Triallelic SNV
    assertFalse(VariantUtils.IS_MULTI_NUCLEOTIDE.apply(
        Variant.newBuilder()
            .setReferenceBases("A")
            .addAlternateBases("G")
            .addAlternateBases("C")
            .build()));

    // Quad-allelic SNV
    assertFalse(VariantUtils.IS_MULTI_NUCLEOTIDE.apply(
        Variant.newBuilder()
            .setReferenceBases("A")
            .addAlternateBases("G")
            .addAlternateBases("C")
            .addAlternateBases("T")
            .build()));

    // Unknown multi-nucleotide variant
    assertTrue(VariantUtils.IS_MULTI_NUCLEOTIDE.apply(Variant.newBuilder().setReferenceBases("AT").build()));

    // Standard deletion
    assertTrue(VariantUtils.IS_MULTI_NUCLEOTIDE.apply(
        Variant.newBuilder().setReferenceBases("AT").addAlternateBases("A").build()));

    // Standard insertion
    assertTrue(VariantUtils.IS_MULTI_NUCLEOTIDE.apply(
        Variant.newBuilder().setReferenceBases("T").addAlternateBases("TAA").build()));

    // Weird MNP
    assertTrue(VariantUtils.IS_MULTI_NUCLEOTIDE.apply(
        Variant.newBuilder().setReferenceBases("TCC").addAlternateBases("TAA").build()));
  }

  @Test
  public void testIsSNP() {
    assertTrue(VariantUtils.IS_SNP.apply(
        TestHelper.makeVariant("chr17", 100, "C", Arrays.asList("G"), "het-RA").build()));

    // Deletion
    assertFalse(VariantUtils.IS_SNP.apply(
        TestHelper.makeVariant("chr17", 100, "CA", Arrays.asList("C"), "het-RA").build()));

    // Insertion
    assertFalse(VariantUtils.IS_SNP.apply(
        TestHelper.makeVariant("chr17", 100, "C", Arrays.asList("CA"), "het-RA").build()));

    // SNP and Insertion
    assertFalse(VariantUtils.IS_SNP.apply(
        TestHelper.makeVariant("chr17", 100, "C", Arrays.asList("G", "CA"), "het-AA").build()));

    // Block Records
    assertFalse(VariantUtils.IS_SNP.apply(
        TestHelper.makeBlockRecord("chr17", 100, 200, "C", TestHelper.EMPTY_ALT_LIST).build()));
    assertFalse(VariantUtils.IS_SNP.apply(
        TestHelper.makeBlockRecord("chr17", 100, 200, "C", Arrays.asList(GATK_ALT)).build()));
    assertFalse(VariantUtils.IS_SNP.apply(
        TestHelper.makeBlockRecord("chr17", 100, 200, "C", Arrays.asList("G", GATK_ALT)).build()));
  }

  @Test
  public void testIsNonVariantSegment() {
    // SNPs
    assertFalse(VariantUtils.IS_NON_VARIANT_SEGMENT.apply(
        TestHelper.makeVariant("chr17", 100, "C", Arrays.asList("G"), "het-RA").build()));

    // Insertions
    assertFalse(VariantUtils.IS_NON_VARIANT_SEGMENT.apply(
        TestHelper.makeVariant("chr17", 100, "C", Arrays.asList("CA"), "het-RA").build()));

    // Deletions NOTE: These two are the same mutation, just encoded in different ways.
    assertFalse(VariantUtils.IS_NON_VARIANT_SEGMENT.apply(
        TestHelper.makeVariant("chr17", 100, "CAG", Arrays.asList("C"), "het-RA").build()));
    assertFalse(VariantUtils.IS_NON_VARIANT_SEGMENT.apply(
        TestHelper.makeVariant("chr17", 100, "", Arrays.asList("AG"), "het-RA").build()));

    // Multi-allelic sites
    assertFalse(VariantUtils.IS_NON_VARIANT_SEGMENT.apply(
        TestHelper.makeVariant("chr17", 100, "C", Arrays.asList("G", "CA"), "het-AA").build()));
    assertFalse(VariantUtils.IS_NON_VARIANT_SEGMENT.apply(
        TestHelper.makeVariant("chr17", 100, "C", Arrays.asList("G", "T"), "het-AA").build()));

    // Non-Variant Block Records
    assertTrue(VariantUtils.IS_NON_VARIANT_SEGMENT.apply(
        TestHelper.makeVariant("chr17", 100, "C", TestHelper.EMPTY_ALT_LIST, "hom-RR").build()));
    assertTrue(VariantUtils.IS_NON_VARIANT_SEGMENT.apply(
        TestHelper.makeVariant("chr17", 100, "C", Arrays.asList(GATK_ALT), "hom-RR").build()));

    // A variant with a <NON_REF> alternate.
    assertFalse(VariantUtils.IS_NON_VARIANT_SEGMENT.apply(
        TestHelper.makeVariant("chr17", 100, "C", Arrays.asList("G", GATK_ALT), "hom-RR").build()));
  }

  @Test
  public void testIsOverlapping() {
    Variant blockRecord = TestHelper.makeBlockRecord("chr17", 100, 200, "C", TestHelper.EMPTY_ALT_LIST).build();

    // SNPs
    assertFalse(VariantUtils.isOverlapping(blockRecord,
        TestHelper.makeVariant("chr17", 99, "C", Arrays.asList("G"), "hom-AA").build()));
    assertTrue(VariantUtils.isOverlapping(blockRecord,
        TestHelper.makeVariant("chr17", 100, "C", Arrays.asList("G"), "hom-AA").build()));
    assertTrue(VariantUtils.isOverlapping(blockRecord,
        TestHelper.makeVariant("chr17", 150, "C", Arrays.asList("G"), "hom-AA").build()));
    assertTrue(VariantUtils.isOverlapping(blockRecord,
        TestHelper.makeVariant("chr17", 199, "C", Arrays.asList("G"), "hom-AA").build()));
    assertFalse(VariantUtils.isOverlapping(blockRecord,
        TestHelper.makeVariant("chr17", 200, "C", Arrays.asList("G"), "hom-AA").build()));

    // Insertions
    assertFalse(VariantUtils.isOverlapping(blockRecord,
        TestHelper.makeVariant("chr17", 99, "C", Arrays.asList("CGG"), "hom-AA").build()));
    assertTrue(VariantUtils.isOverlapping(blockRecord,
        TestHelper.makeVariant("chr17", 100, "C", Arrays.asList("CGG"), "hom-AA").build()));
    assertTrue(VariantUtils.isOverlapping(blockRecord,
        TestHelper.makeVariant("chr17", 199, "C", Arrays.asList("CGG"), "hom-AA").build()));
    assertFalse(VariantUtils.isOverlapping(blockRecord,
        TestHelper.makeVariant("chr17", 200, "C", Arrays.asList("CGG"), "hom-AA").build()));

    // Deletions
    assertFalse(VariantUtils.isOverlapping(blockRecord,
        TestHelper.makeVariant("chr17", 99, "CAA", Arrays.asList("C"), "hom-AA").build()));
    assertTrue(VariantUtils.isOverlapping(blockRecord,
        TestHelper.makeVariant("chr17", 100, "CAA", Arrays.asList("C"), "hom-AA").build()));
    assertTrue(VariantUtils.isOverlapping(blockRecord,
        TestHelper.makeVariant("chr17", 199, "CAA", Arrays.asList("C"), "hom-AA").build()));
    assertFalse(VariantUtils.isOverlapping(blockRecord,
        TestHelper.makeVariant("chr17", 200, "CAA", Arrays.asList("C"), "hom-AA").build()));
  }

  @Test
  public void testIsOverlappingBuilderParam() {
    Variant blockRecord = TestHelper.makeBlockRecord("chr17", 100, 200, "C", TestHelper.EMPTY_ALT_LIST).build();

    // SNPs
    assertFalse(VariantUtils.isOverlapping(blockRecord,
        TestHelper.makeVariant("chr17", 99, "C", Arrays.asList("G"), "hom-AA")));
    assertTrue(VariantUtils.isOverlapping(blockRecord,
        TestHelper.makeVariant("chr17", 100, "C", Arrays.asList("G"), "hom-AA")));
  };

  @Test
  public void testIsSameVariantSite() {
    // SNP and insertion
    assertTrue(VariantUtils.isSameVariantSite(
        TestHelper.makeVariant("chr17", 100, "C", Arrays.asList("G"), "hom-AA"),
        TestHelper.makeVariant("chr17", 100, "C", Arrays.asList("CA"), "het-RA").build()));

    // SNP and deletion
    assertFalse(VariantUtils.isSameVariantSite(
        TestHelper.makeVariant("chr17", 100, "C", Arrays.asList("G"), "hom-AA"),
        TestHelper.makeVariant("chr17", 100, "CCT", Arrays.asList("C"), "hom-AA").build()));

    // Insertion and deletion
    assertFalse(VariantUtils.isSameVariantSite(
        TestHelper.makeVariant("chr17", 100, "C", Arrays.asList("CA"), "het-RA"),
        TestHelper.makeVariant("chr17", 100, "CCT", Arrays.asList("C"), "hom-AA").build()));
  }

  @Test
  public void testVariantComparator() {
    Comparator<Variant> comparator = VariantUtils.NON_VARIANT_SEGMENT_COMPARATOR;

    assertTrue(0 > comparator.compare(
        TestHelper.makeVariant("2", 10, 11, "A", Arrays.asList("C")).build(),
        TestHelper.makeVariant("2", 10, 11, "A", Arrays.asList("G")).build()));

    assertTrue(0 > comparator.compare(
        TestHelper.makeVariant("2", 10, 11, "A", Arrays.asList("C")).build(),
        TestHelper.makeVariant("2", 10, 11, "A", Arrays.asList("C", "G")).build()));

    assertTrue(0 > comparator.compare(
        TestHelper.makeVariant("2", 10, 11, "A", Arrays.asList("C", "G")).build(),
        TestHelper.makeVariant("2", 10, 11, "A", Arrays.asList("G")).build()));

    assertTrue(0 > comparator.compare(
        TestHelper.makeVariant("2", 10, 11, "A", TestHelper.EMPTY_ALT_LIST).build(),
        TestHelper.makeVariant("2", 10, 11, "A", Arrays.asList("C")).build()));

    assertTrue(0 > comparator.compare(
        TestHelper.makeVariant("2", 10, 11, "A", Arrays.asList(GATK_ALT)).build(),
        TestHelper.makeVariant("2", 10, 11, "A", Arrays.asList("C")).build()));

    assertTrue(0 < comparator.compare(
        TestHelper.makeVariant("2", 10, 11, "A", Arrays.asList("G", GATK_ALT)).build(),
        TestHelper.makeVariant("2", 10, 11, "A", Arrays.asList("C")).build()));
  }

  @Test
  public void testVariantComparator_Collection() {
    Variant insert1BiAllelic = TestHelper.makeVariant("chr2", 100, "C", Arrays.asList("CA"), "het-RA").build();
    Variant insert2BiAllelic = TestHelper.makeVariant("chr2", 100, "C", Arrays.asList("CAAA"), "het-RA").build();
    Variant snpInsertMultiAllelic = TestHelper.makeVariant("chr2", 100, "C", Arrays.asList("G","CA"), "het-AA").build();

    Variant delete1BiAllelic = TestHelper.makeVariant("chr2", 100, "CCT", Arrays.asList("C"), "hom-AA").build();
    Variant deleteMultiAllelic = TestHelper.makeVariant("chr2", 100, "CCT", Arrays.asList("C","CC"), "het-AA").build();

    Variant delete2BiAllelic = TestHelper.makeVariant("chr2", 100, "CC", Arrays.asList("C"), "het-RA").build();
    Variant indelMultiAllelic = TestHelper.makeVariant("chr2", 100, "CC", Arrays.asList("C","CCA"), "het-AA").build();

    List<Variant> input = Arrays.asList(delete1BiAllelic, deleteMultiAllelic, delete2BiAllelic, indelMultiAllelic,
        insert1BiAllelic, insert2BiAllelic, snpInsertMultiAllelic);

    Comparator<Variant> comparator = VariantUtils.NON_VARIANT_SEGMENT_COMPARATOR;

    Collections.shuffle(input);  // This will check a different permutation each time this test runs.
    Collections.sort(input, comparator);
    assertEquals(input, Arrays.asList(insert1BiAllelic, insert2BiAllelic, snpInsertMultiAllelic,
        delete2BiAllelic, indelMultiAllelic, delete1BiAllelic, deleteMultiAllelic));
  }

  @Test
  public void testChromosomalOrdering() {
    Variant first = TestHelper.makeVariant("chr1", 100, "A", "C").build();
    Variant second = TestHelper.makeVariant("chr1", 100, "A", "T").build();
    Variant third = TestHelper.makeVariant("chr1", 100, "A", "C", "T").build();
    Variant fourth = TestHelper.makeVariant("chr1", 100, "C", "A").build();
    Variant fifth = TestHelper.makeVariant("chr1", 100, "AA", "A").build();
    Variant sixth = TestHelper.makeVariant("chr1", 102, "A", "C").build();
    Variant seventh = TestHelper.makeVariant("chr2", 10, "A", "C").build();
    Variant eighth = TestHelper.makeVariant("chr2", 20, "A", "C", "T", "G").build();
    Variant ninth = TestHelper.makeVariant("chr2", 20, "A", "T", "C", "G").build();

    List<Variant> actual =
        Arrays.asList(ninth, seventh, eighth, first, sixth, fifth, fourth, second, third);
    List<Variant> expected =
        Arrays.asList(first, second, third, fourth, fifth, sixth, seventh, eighth, ninth);

    assertTrue(!actual.equals(expected));
    Collections.sort(actual, VariantUtils.CHROMOSOMAL_ORDER);
    assertTrue(actual.equals(expected));
  }
}
