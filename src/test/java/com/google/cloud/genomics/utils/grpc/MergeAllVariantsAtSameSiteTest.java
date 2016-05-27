/*
 * Copyright (C) 2016 Google Inc.
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

import com.google.genomics.v1.Variant;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@RunWith(JUnit4.class)
public class MergeAllVariantsAtSameSiteTest {

  @Test
  public void testCombineSnpsSameSite() throws Exception {
    Variant snp1BiAllelic = TestHelper.makeVariant("chr2", 100, "C", Arrays.asList("A"), "het-RA").build();
    Variant snp2BiAllelic = TestHelper.makeVariant("chr2", 100, "C", Arrays.asList("G"), "hom-AA").build();
    Variant snp3BiAllelic = TestHelper.makeVariant("chr2", 100, "C", Arrays.asList("T"), "het-RA").build();
    Variant snp1MultiAllelic = TestHelper.makeVariant("chr2", 100, "C", Arrays.asList("A","G"), "het-AA").build();
    Variant snp2MultiAllelic = TestHelper.makeVariant("chr2", 100, "C", Arrays.asList("A","T"), "het-AA").build();
    Variant snp3MultiAllelic = TestHelper.makeVariant("chr2", 100, "C", Arrays.asList("G","T"), "het-AA").build();

    List<Variant> input = Arrays.asList(snp1BiAllelic, snp2BiAllelic, snp3BiAllelic,
        snp1MultiAllelic, snp2MultiAllelic, snp3MultiAllelic);

    Variant expectedOutput = TestHelper.makeVariant("chr2", 100, 101, "C", Arrays.asList("A", "G", "T"))
        .addCalls(TestHelper.makeCall("het-RA-[A]", 0, 1))
        .addCalls(TestHelper.makeCall("het-AA-[A, G]", 1, 2))
        .addCalls(TestHelper.makeCall("het-AA-[A, T]", 1, 3))
        .addCalls(TestHelper.makeCall("hom-AA-[G]", 2, 2))
        .addCalls(TestHelper.makeCall("het-AA-[G, T]", 2, 3))
        .addCalls(TestHelper.makeCall("het-RA-[T]", 0, 3))
        .build();

    VariantMergeStrategyTest.mergeTest(input, Arrays.asList(expectedOutput),
        MergeAllVariantsAtSameSite.class);

    // Ensure that the result is stable regardless of the input order of these Variants.
    for (int i = 0; i < 5; i++) {
      Collections.shuffle(input);
      VariantMergeStrategyTest.mergeTest(input, Arrays.asList(expectedOutput),
          MergeAllVariantsAtSameSite.class);
    }
  }

  @Test
  public void testCombineIndelsAtSameStart() throws Exception {
    Variant insert1BiAllelic = TestHelper.makeVariant("chr2", 100, "C", Arrays.asList("CA"), "het-RA").build();
    Variant insert2BiAllelic = TestHelper.makeVariant("chr2", 100, "C", Arrays.asList("CAAA"), "het-RA").build();
    Variant snpInsertMultiAllelic = TestHelper.makeVariant("chr2", 100, "C", Arrays.asList("G","CA"), "het-AA").build();

    Variant delete1BiAllelic = TestHelper.makeVariant("chr2", 100, "CCT", Arrays.asList("C"), "hom-AA").build();
    Variant deleteMultiAllelic = TestHelper.makeVariant("chr2", 100, "CCT", Arrays.asList("C","CC"), "het-AA").build();

    Variant delete2BiAllelic = TestHelper.makeVariant("chr2", 100, "CC", Arrays.asList("C"), "het-RA").build();
    Variant indelMultiAllelic = TestHelper.makeVariant("chr2", 100, "CC", Arrays.asList("C","CCA"), "het-AA").build();

    Variant expectedOutput1 = TestHelper.makeVariant("chr2", 100, 101, "C", Arrays.asList("CA", "CAAA", "G"))
        .addCalls(TestHelper.makeCall("het-RA-[CA]", 0, 1))
        .addCalls(TestHelper.makeCall("het-RA-[CAAA]", 0, 2))
        .addCalls(TestHelper.makeCall("het-AA-[G, CA]", 3, 1))
        .build();
    Variant expectedOutput2 = TestHelper.makeVariant("chr2", 100, 102, "CC", Arrays.asList("C", "CCA"))
        .addCalls(TestHelper.makeCall("het-RA-[C]", 0, 1))
        .addCalls(TestHelper.makeCall("het-AA-[C, CCA]", 1, 2))
        .build();
    Variant expectedOutput3 = TestHelper.makeVariant("chr2", 100, 103, "CCT", Arrays.asList("C", "CC"))
        .addCalls(TestHelper.makeCall("hom-AA-[C]", 1, 1))
        .addCalls(TestHelper.makeCall("het-AA-[C, CC]", 1, 2))
        .build();

    VariantMergeStrategyTest.mergeTest(Arrays.asList(insert1BiAllelic, insert2BiAllelic, snpInsertMultiAllelic),
        Arrays.asList(expectedOutput1),
        MergeAllVariantsAtSameSite.class);
    VariantMergeStrategyTest.mergeTest(Arrays.asList(delete2BiAllelic, indelMultiAllelic),
        Arrays.asList(expectedOutput2),
        MergeAllVariantsAtSameSite.class);
    VariantMergeStrategyTest.mergeTest(Arrays.asList(delete1BiAllelic, deleteMultiAllelic),
        Arrays.asList(expectedOutput3),
        MergeAllVariantsAtSameSite.class);

    // Ensure that the result is stable regardless of the input order of these Variants.
    List<Variant> input = Arrays.asList(delete1BiAllelic, deleteMultiAllelic, delete2BiAllelic, indelMultiAllelic,
        insert1BiAllelic, insert2BiAllelic, snpInsertMultiAllelic);
    for (int i = 0; i < 5; i++) {
      Collections.shuffle(input);
      VariantMergeStrategyTest.mergeTest(input,
          Arrays.asList(expectedOutput1, expectedOutput2, expectedOutput3),
          MergeAllVariantsAtSameSite.class);
    }
  }
}
