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

@RunWith(JUnit4.class)
public class MergeNonVariantSegmentsWithVariantsTest {

  @Test
  public void mergeVariants() throws Exception {
    Variant snp1 = TestHelper.makeVariant("chr7", 200010, "A", Arrays.asList("C"), "het-RA", "hom-AA").build();
    Variant snp2 = TestHelper.makeVariant("chr7", 200019, "T", Arrays.asList("G"), "het-RA", "hom-AA").build();
    Variant insert = TestHelper.makeVariant("chr7", 200010, "A", Arrays.asList("AC"), "het-RA", "hom-AA").build();
    Variant blockRecord1 = TestHelper.makeBlockRecord("chr7", 199005, 202050, "A", TestHelper.EMPTY_ALT_LIST).build();
    Variant blockRecord2 = TestHelper.makeBlockRecord("chr7", 200011, 202020, "C", Arrays.asList(VariantUtils.GATK_NON_VARIANT_SEGMENT_ALT)).build();

    Variant expectedSnp1 = TestHelper.makeVariant("chr7", 200010, "A", Arrays.asList("C"))
        .addCalls(TestHelper.makeCall("het-RA-[C]", 0,1))
        .addCalls(TestHelper.makeCall("hom-AA-[C]", 1,1))
        .addCalls(TestHelper.makeCall("hom-RR-[]", 0,0))
        .build();

    Variant expectedSnp2 = TestHelper.makeVariant("chr7", 200019, "T", Arrays.asList("G"))
        .addCalls(TestHelper.makeCall("het-RA-[G]", 0,1))
        .addCalls(TestHelper.makeCall("hom-AA-[G]", 1,1))
        .addCalls(TestHelper.makeCall("hom-RR-[]", 0,0))
        .addCalls(TestHelper.makeCall("hom-RR-[<NON_REF>]", 0,0))
        .build();

    Variant expectedInsert = TestHelper.makeVariant("chr7", 200010, "A", Arrays.asList("AC"))
        .addCalls(TestHelper.makeCall("het-RA-[AC]", 0,1))
        .addCalls(TestHelper.makeCall("hom-AA-[AC]", 1,1))
        .addCalls(TestHelper.makeCall("hom-RR-[]", 0,0))
        .build();

    VariantMergeStrategyTestHelper.mergeTest(Arrays.asList(snp1, snp2, insert, blockRecord1, blockRecord2),
        Arrays.asList(expectedInsert, expectedSnp1, expectedSnp2),
        MergeNonVariantSegmentsWithVariants.class);
  }
}
