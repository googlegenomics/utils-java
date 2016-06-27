/*
 * Copyright (C) 2016 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License")); you may not use this file except
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

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;

import com.google.common.collect.FluentIterable;
import com.google.common.collect.ImmutableList;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameter;
import org.junit.runners.Parameterized.Parameters;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@RunWith(JUnit4.class)
public class SequenceUtilsTest {

  @Test
  public void testClassifyGenotype() {
    assertTrue(SequenceUtils.classifyGenotype(ImmutableList.of(0)).equals(
        SequenceUtils.GenotypeCategory.REF));
    assertTrue(SequenceUtils.classifyGenotype(ImmutableList.of(1)).equals(
        SequenceUtils.GenotypeCategory.NON_REF));
    assertTrue(SequenceUtils.classifyGenotype(ImmutableList.of(3)).equals(
        SequenceUtils.GenotypeCategory.NON_REF));
    assertTrue(SequenceUtils.classifyGenotype(ImmutableList.of(0, 0)).equals(
        SequenceUtils.GenotypeCategory.REF_HOMOZYGOUS));
    assertTrue(SequenceUtils.classifyGenotype(ImmutableList.of(1, 0)).equals(
        SequenceUtils.GenotypeCategory.HETEROZYGOUS));
    assertTrue(SequenceUtils.classifyGenotype(ImmutableList.of(0, 1)).equals(
        SequenceUtils.GenotypeCategory.HETEROZYGOUS));
    assertTrue(SequenceUtils.classifyGenotype(ImmutableList.of(0, 3)).equals(
        SequenceUtils.GenotypeCategory.HETEROZYGOUS));
    assertTrue(SequenceUtils.classifyGenotype(ImmutableList.of(3, 0)).equals(
        SequenceUtils.GenotypeCategory.HETEROZYGOUS));
    assertTrue(SequenceUtils.classifyGenotype(ImmutableList.of(1, 1)).equals(
        SequenceUtils.GenotypeCategory.NON_REF_HOMOZYGOUS));
    assertTrue(SequenceUtils.classifyGenotype(ImmutableList.of(1, 3)).equals(
        SequenceUtils.GenotypeCategory.NON_REF_HOMOZYGOUS));
    assertTrue(SequenceUtils.classifyGenotype(ImmutableList.of(3, 1)).equals(
        SequenceUtils.GenotypeCategory.NON_REF_HOMOZYGOUS));
  }

  @RunWith(Parameterized.class)
  public static final class SubstituteAlleleTests {
    @Parameters(name = "{index}: substituteAllele({0},{1},{2})={3}")
    public static Iterable<Object[]> testData() {
      return Arrays.asList(
          new Object[][] {
              {"AG", "AG", "A", "A"},
              {"AG", "A", "C", "CG"},
              {"AGCT", "AG", "A", "ACT"},
              {"AGCT", "A", "AGG", "AGGGCT"},
              {"AGCT", "AGC", "CTA", "CTAT"},
          });
    }

    @Parameter(0)
    public String ref;

    @Parameter(1)
    public String refAllele;

    @Parameter(2)
    public String altAllele;

    @Parameter(3)
    public String expected;

    @Test
    public void testSubstituteAllele() throws Exception {
      assertTrue(SequenceUtils.substituteAllele(ref, refAllele, altAllele).equals(expected));
    }
  }

  @RunWith(Parameterized.class)
  public static final class InvalidSubstituteAlleleTests {
    @Parameters(name = "{index}: substituteAllele({0},{1},{2})={3}")
    public static Iterable<Object[]> testData() {
      return Arrays.asList(
          new Object[][] {
              {"AG", "C", "A"}, // ref doesn't start with refAllele
              {"AG", "AGT", "A"}, // refAllele is too long
              {"", "C", "A"}, // ref is empty
              {"A", "", "A"}, // refAllele is empty
              {"A", "A", ""}, // altAllele is empty
          });
    }

    @Rule public final ExpectedException thrown = ExpectedException.none();

    @Parameter(0)
    public String ref;

    @Parameter(1)
    public String refAllele;

    @Parameter(2)
    public String altAllele;

    @Test
    public void testSubstituteAllele() {
      thrown.expect(IllegalArgumentException.class);
      SequenceUtils.substituteAllele(ref, refAllele, altAllele);
    }
  }

  @Test
  public void testNonRefAlleles() {
    List<Integer> fakeGenotypes = new ArrayList<>(Arrays.asList(-1, -3, 5, 0, 0, 0, 1));
    List<Integer> nonRefAlleles =
        FluentIterable.from(fakeGenotypes)
            .filter(new SequenceUtils.NonRefAllelesPredicate())
            .toList();
    assertThat(nonRefAlleles, containsInAnyOrder(5, 1));

    List<Integer> refGenotypes = new ArrayList<>(Arrays.asList(0));
    nonRefAlleles =
        FluentIterable.from(refGenotypes)
            .filter(new SequenceUtils.NonRefAllelesPredicate())
            .toList();
    assertTrue(nonRefAlleles.size() == 0);
  }
}
