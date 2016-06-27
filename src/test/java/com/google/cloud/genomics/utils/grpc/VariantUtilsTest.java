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
import com.google.genomics.v1.VariantCall;
import com.google.protobuf.ListValue;
import com.google.protobuf.Value;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

@RunWith(JUnit4.class)
public class VariantUtilsTest {

  // For test readability, create alias for this constant.
  public static final String GATK_ALT = VariantUtils.GATK_NON_VARIANT_SEGMENT_ALT;

  @Test
  public void SnpTitvStatus() {
    Variant variant =
        Variant.newBuilder()
            .setId("Var1")
            .setReferenceBases("A")
            .addAlternateBases("G")
            .addAlternateBases("C")
            .addAlternateBases("T")
            .build();
    assertEquals(VariantUtils.snpTitvStatus(0, variant), VariantUtils.SnpTitvStatus.REFERENCE);
    assertEquals(VariantUtils.snpTitvStatus(1, variant), VariantUtils.SnpTitvStatus.TRANSITION);
    assertEquals(VariantUtils.snpTitvStatus(2, variant), VariantUtils.SnpTitvStatus.TRANSVERSION);
    assertEquals(VariantUtils.snpTitvStatus(3, variant), VariantUtils.SnpTitvStatus.TRANSVERSION);

    variant =
        Variant.newBuilder()
            .setId("Var2")
            .setReferenceBases("G")
            .addAlternateBases("C")
            .addAlternateBases("T")
            .addAlternateBases("A")
            .build();
    assertEquals(VariantUtils.snpTitvStatus(0, variant), VariantUtils.SnpTitvStatus.REFERENCE);
    assertEquals(VariantUtils.snpTitvStatus(1, variant), VariantUtils.SnpTitvStatus.TRANSVERSION);
    assertEquals(VariantUtils.snpTitvStatus(2, variant), VariantUtils.SnpTitvStatus.TRANSVERSION);
    assertEquals(VariantUtils.snpTitvStatus(3, variant), VariantUtils.SnpTitvStatus.TRANSITION);

    variant =
        Variant.newBuilder()
            .setId("Var3")
            .setReferenceBases("C")
            .addAlternateBases("G")
            .addAlternateBases("A")
            .addAlternateBases("T")
            .build();
    assertEquals(VariantUtils.snpTitvStatus(0, variant), VariantUtils.SnpTitvStatus.REFERENCE);
    assertEquals(VariantUtils.snpTitvStatus(1, variant), VariantUtils.SnpTitvStatus.TRANSVERSION);
    assertEquals(VariantUtils.snpTitvStatus(2, variant), VariantUtils.SnpTitvStatus.TRANSVERSION);
    assertEquals(VariantUtils.snpTitvStatus(3, variant), VariantUtils.SnpTitvStatus.TRANSITION);

    variant =
        Variant.newBuilder()
            .setId("Var4")
            .setReferenceBases("T")
            .addAlternateBases("A")
            .addAlternateBases("C")
            .addAlternateBases("G")
            .build();
    assertEquals(VariantUtils.snpTitvStatus(0, variant), VariantUtils.SnpTitvStatus.REFERENCE);
    assertEquals(VariantUtils.snpTitvStatus(1, variant), VariantUtils.SnpTitvStatus.TRANSVERSION);
    assertEquals(VariantUtils.snpTitvStatus(2, variant), VariantUtils.SnpTitvStatus.TRANSITION);
    assertEquals(VariantUtils.snpTitvStatus(3, variant), VariantUtils.SnpTitvStatus.TRANSVERSION);
  }

  @Test
  public void testGetVariantQualityMeasure() {
    final Double firstVariantQuality = VariantUtils.getVariantQualityMeasure(
        TestHelper.makeVariant("chr1", 100, "A", "C", 0.0).build());
    assertTrue(firstVariantQuality >= -1E-6 && firstVariantQuality <= 1E-6);

    final Double vqslod = -0.001;
    Map<String, ListValue> infoField = new TreeMap<>();
    infoField.put(
        "OTHER_AWESOME_INFO",
        ListValue.newBuilder()
            .addValues(Value.newBuilder().setNumberValue(99.99).build())
            .build());
    infoField.put(
        "VQSLOD",
        ListValue.newBuilder()
            .addValues(Value.newBuilder().setStringValue(vqslod.toString()).build())
            .build());
    Variant testVar =
        Variant.newBuilder()
            .putAllInfo(infoField)
            .setReferenceName("20")
            .setStart(10_000_000L)
            .setReferenceBases("ACGCGCG")
            .addAlternateBases("A")
            .addAlternateBases("ACGCG")
            .build();
    final Double secondVariantQuality = VariantUtils.getVariantQualityMeasure(testVar);
    assertTrue(secondVariantQuality >= -1E-6 + vqslod && secondVariantQuality <= 1E-6 + vqslod);
  }

  @Test
  public void testGetPosition() {
    assertEquals(
        VariantUtils.getPosition(TestHelper.makeVariant("chr1", 100, "A", "C", 0.0).build()),
        TestHelper.makePosition("chr1", 100));

    final double vqslod = -0.001;
    Map<String, ListValue> infoField = new TreeMap<>();
    infoField.put(
        "OTHER_AWESOME_INFO",
        ListValue.newBuilder()
            .addValues(Value.newBuilder().setNumberValue(99.99).build())
            .build());
    infoField.put(
        "VQSLOD",
        ListValue.newBuilder()
            .addValues(Value.newBuilder().setNumberValue(vqslod).build())
            .build());
    Variant testVar =
        Variant.newBuilder()
            .putAllInfo(infoField)
            .setReferenceName("20")
            .setStart(10_000_000L)
            .setReferenceBases("ACGCGCG")
            .addAlternateBases("A")
            .addAlternateBases("ACGCG")
            .build();
    assertEquals(VariantUtils.getPosition(testVar), TestHelper.makePosition("20", 10_000_000L));
  }

  @Test
  public void testAllelicChecks() {
    // multiple alternates
    Variant multipleAlts =
        Variant.newBuilder()
            .setReferenceBases("A")
            .addAlternateBases("G")
            .addAlternateBases("T")
            .build();
    assertTrue(VariantUtils.isSnpAllele(1, multipleAlts));
    assertTrue(VariantUtils.isSnpAllele(2, multipleAlts));
    assertFalse(VariantUtils.isDeletionAllele(1, multipleAlts));
    assertFalse(VariantUtils.isDeletionAllele(2, multipleAlts));
    assertFalse(VariantUtils.isInsertionAllele(1, multipleAlts));
    assertFalse(VariantUtils.isInsertionAllele(2, multipleAlts));

    // deletion
    Variant del = Variant.newBuilder().setReferenceBases("AC").addAlternateBases("A").build();
    assertFalse(VariantUtils.isSnpAllele(1, del));
    assertTrue(VariantUtils.isDeletionAllele(1, del));
    assertFalse(VariantUtils.isInsertionAllele(1, del));

    // insertion
    Variant ins = Variant.newBuilder().setReferenceBases("A").addAlternateBases("AT").build();
    assertFalse(VariantUtils.isSnpAllele(1, ins));
    assertFalse(VariantUtils.isDeletionAllele(1, ins));
    assertTrue(VariantUtils.isInsertionAllele(1, ins));
  }

  @Test
  public void testHasVariation() {
    Variant template = TestHelper.makeVariant("chr1", 100, "A", "C").build();
    VariantCall homRef = VariantCall.newBuilder().addGenotype(0).addGenotype(0).build();
    VariantCall het1 = VariantCall.newBuilder().addGenotype(0).addGenotype(1).build();
    VariantCall het2 = VariantCall.newBuilder().addGenotype(1).addGenotype(0).build();
    VariantCall homAlt = VariantCall.newBuilder().addGenotype(1).addGenotype(1).build();
    VariantCall haploidRef = VariantCall.newBuilder().addGenotype(0).build();
    VariantCall haploidAlt = VariantCall.newBuilder().addGenotype(1).build();

    assertFalse(VariantUtils.HAS_VARIATION.apply(template));
    assertFalse(VariantUtils.HAS_VARIATION.apply(Variant.newBuilder(template).addCalls(homRef).build()));
    assertTrue(VariantUtils.HAS_VARIATION.apply(Variant.newBuilder(template).addCalls(het1).build()));
    assertTrue(VariantUtils.HAS_VARIATION.apply(Variant.newBuilder(template).addCalls(het2).build()));
    assertTrue(VariantUtils.HAS_VARIATION.apply(Variant.newBuilder(template).addCalls(homAlt).build()));
    assertFalse(VariantUtils.HAS_VARIATION.apply(Variant.newBuilder(template).addCalls(haploidRef).build()));
    assertTrue(VariantUtils.HAS_VARIATION.apply(Variant.newBuilder(template).addCalls(haploidAlt).build()));
    assertTrue(VariantUtils.HAS_VARIATION.apply(
            Variant.newBuilder(template)
                .addCalls(haploidAlt)
                .addCalls(homRef)
                .addCalls(het2)
                .build()));

    Variant multiTemplate =
        TestHelper.makeVariant("chr2", 200, "G", "GT")
            .addAlternateBases("T")
            .build();
    VariantCall multiAlt = VariantCall.newBuilder().addGenotype(1).addGenotype(2).build();
    assertTrue(VariantUtils.HAS_VARIATION.apply(
            Variant.newBuilder(multiTemplate).addCalls(multiAlt).addCalls(homRef).build()));
    assertFalse(VariantUtils.HAS_VARIATION.apply(
            Variant.newBuilder(multiTemplate).addCalls(haploidRef).addCalls(homRef).build()));
  }

  @Test
  public void testIsFiltered() {
    Variant variant =
        Variant.newBuilder()
            .setId("Var1")
            .addFilter("PASS")
            .setReferenceBases("A")
            .addAlternateBases("G")
            .addAlternateBases("C")
            .addAlternateBases("T")
            .build();
    assertFalse(VariantUtils.IS_FILTERED.apply(variant));

    variant =
        Variant.newBuilder()
            .setId("Var2")
            .addFilter(".")
            .setReferenceBases("A")
            .addAlternateBases("G")
            .addAlternateBases("C")
            .addAlternateBases("T")
            .build();
    assertFalse(VariantUtils.IS_FILTERED.apply(variant));

    variant =
        Variant.newBuilder()
            .setId("Var3")
            .addFilter("BAD_SB_SCORE")
            .setReferenceBases("A")
            .addAlternateBases("G")
            .addAlternateBases("C")
            .addAlternateBases("T")
            .build();
    assertTrue(VariantUtils.IS_FILTERED.apply(variant));

    variant =
        Variant.newBuilder()
            .setId("Var4")
            .addFilter("LowQual")
            .addFilter("!ReallyBadHaplotype_score")
            .setReferenceBases("A")
            .addAlternateBases("G")
            .addAlternateBases("C")
            .addAlternateBases("T")
            .build();
    assertTrue(VariantUtils.IS_FILTERED.apply(variant));

    // Test that variants without any filters defined are passing.
    variant = Variant.newBuilder().setReferenceBases("A").addAlternateBases("G").build();
    assertFalse(VariantUtils.IS_FILTERED.apply(variant));
  }

  @Test
  public void testIsBiallelic() {
    // no alternates
    assertFalse(VariantUtils.IS_BIALLELIC.apply(Variant.newBuilder().setReferenceBases("A").build()));
    // multiple alternates
    assertFalse(VariantUtils.IS_BIALLELIC.apply(
            Variant.newBuilder()
                .setReferenceBases("A")
                .addAlternateBases("G")
                .addAlternateBases("T")
                .build()));
    // deletion
    assertTrue(VariantUtils.IS_BIALLELIC.apply(
            Variant.newBuilder().setReferenceBases("AC").addAlternateBases("A").build()));
    // insertion
    assertTrue(VariantUtils.IS_BIALLELIC.apply(
            Variant.newBuilder().setReferenceBases("A").addAlternateBases("AT").build()));
    // success
    assertTrue(VariantUtils.IS_BIALLELIC.apply(
            Variant.newBuilder().setReferenceBases("A").addAlternateBases("G").build()));
  }

  @Test
  public void testIsGVCF() {
    Variant variant = Variant.newBuilder()
        .setReferenceBases("A")
        .addAlternateBases("G")
        .build();
    assertFalse(VariantUtils.IS_GVCF.apply(variant));

    variant = Variant.newBuilder()
        .setReferenceBases("A")
        .addAlternateBases("ACG")
        .build();
    assertFalse(VariantUtils.IS_GVCF.apply(variant));

    variant = Variant.newBuilder()
        .setReferenceBases("A")
        .addAlternateBases("<symbolic>")
        .build();
    assertFalse(VariantUtils.IS_GVCF.apply(variant));

    variant = Variant.newBuilder()
        .setReferenceBases("A")
        .addAlternateBases("<*>")
        .build();
    assertTrue(VariantUtils.IS_GVCF.apply(variant));
  }

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
