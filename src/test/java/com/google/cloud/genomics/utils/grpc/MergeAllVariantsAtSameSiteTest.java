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
import com.google.protobuf.ListValue;
import com.google.protobuf.Value;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RunWith(JUnit4.class)
public class MergeAllVariantsAtSameSiteTest {

  @Test
  public void testJustOneSnp() throws Exception {
    Variant snp1BiAllelic = TestHelper.makeVariant("chr2", 100, "C", Arrays.asList("A"), "het-RA").build();
    Variant snp2BiAllelic = TestHelper.makeVariant("chr2", 200, "G", Arrays.asList("T"), "hom-AA").build();

    Map<String, ListValue> info = new HashMap<String, ListValue>();
    ListValue.Builder callSetNames = ListValue.newBuilder();
    info.put(MergeAllVariantsAtSameSite.OVERLAPPING_CALLSETS_FIELD, callSetNames.build());

    Variant expectedOutput1 = Variant.newBuilder(snp1BiAllelic)
        .putAllInfo(info)
        .build();

    Variant expectedOutput2 = Variant.newBuilder(snp2BiAllelic)
        .putAllInfo(info)
        .build();

    VariantMergeStrategyTestHelper.mergeTest(100L,
        Arrays.asList(snp1BiAllelic, snp2BiAllelic),
        Arrays.asList(expectedOutput1, expectedOutput2),
        MergeAllVariantsAtSameSite.class);
  }

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

    Map<String, ListValue> info = new HashMap<String, ListValue>();
    ListValue.Builder callSetNames = ListValue.newBuilder();
    info.put(MergeAllVariantsAtSameSite.OVERLAPPING_CALLSETS_FIELD, callSetNames.build());
    Variant expectedOutput = TestHelper.makeVariant("chr2", 100, 101, "C", Arrays.asList("A", "G", "T"))
        .addCalls(TestHelper.makeCall("het-RA-[A]", 0, 1))
        .addCalls(TestHelper.makeCall("het-AA-[A, G]", 1, 2))
        .addCalls(TestHelper.makeCall("het-AA-[A, T]", 1, 3))
        .addCalls(TestHelper.makeCall("hom-AA-[G]", 2, 2))
        .addCalls(TestHelper.makeCall("het-AA-[G, T]", 2, 3))
        .addCalls(TestHelper.makeCall("het-RA-[T]", 0, 3))
        .putAllInfo(info)
        .build();

    VariantMergeStrategyTestHelper.mergeTest(100L, input, Arrays.asList(expectedOutput),
        MergeAllVariantsAtSameSite.class);

    // Ensure that the result is stable regardless of the input order of these Variants.
    for (int i = 0; i < 5; i++) {
      Collections.shuffle(input);
      VariantMergeStrategyTestHelper.mergeTest(100L, input, Arrays.asList(expectedOutput),
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

    Map<String, ListValue> emptyInfo = new HashMap<String, ListValue>();
    emptyInfo.put(MergeAllVariantsAtSameSite.OVERLAPPING_CALLSETS_FIELD, ListValue.newBuilder().build());

    Variant expectedOutput1 = TestHelper.makeVariant("chr2", 100, 101, "C", Arrays.asList("CA", "CAAA", "G"))
        .addCalls(TestHelper.makeCall("het-RA-[CA]", 0, 1))
        .addCalls(TestHelper.makeCall("het-RA-[CAAA]", 0, 2))
        .addCalls(TestHelper.makeCall("het-AA-[G, CA]", 3, 1))
        .putAllInfo(emptyInfo)
        .build();

    Variant expectedOutput2 = TestHelper.makeVariant("chr2", 100, 102, "CC", Arrays.asList("C", "CCA"))
        .addCalls(TestHelper.makeCall("het-RA-[C]", 0, 1))
        .addCalls(TestHelper.makeCall("het-AA-[C, CCA]", 1, 2))
        .putAllInfo(emptyInfo)
        .build();

    Variant expectedOutput3 = TestHelper.makeVariant("chr2", 100, 103, "CCT", Arrays.asList("C", "CC"))
        .addCalls(TestHelper.makeCall("hom-AA-[C]", 1, 1))
        .addCalls(TestHelper.makeCall("het-AA-[C, CC]", 1, 2))
        .putAllInfo(emptyInfo)
        .build();

    //
    // First test just the variants that should be merged together because they occur at the same site.
    //
    VariantMergeStrategyTestHelper.mergeTest(100L, Arrays.asList(insert1BiAllelic, insert2BiAllelic, snpInsertMultiAllelic),
        Arrays.asList(expectedOutput1),
        MergeAllVariantsAtSameSite.class);
    VariantMergeStrategyTestHelper.mergeTest(100L, Arrays.asList(delete2BiAllelic, indelMultiAllelic),
        Arrays.asList(expectedOutput2),
        MergeAllVariantsAtSameSite.class);
    VariantMergeStrategyTestHelper.mergeTest(100L, Arrays.asList(delete1BiAllelic, deleteMultiAllelic),
        Arrays.asList(expectedOutput3),
        MergeAllVariantsAtSameSite.class);

    //
    // Now test all variants that occur at the same position so that some overlap each other even though
    // they should not be merged together.
    //

    Map<String, ListValue> info1 = new HashMap<String, ListValue>();
    ListValue.Builder overlappingCallSetNames1 = ListValue.newBuilder();
    overlappingCallSetNames1.addValues(Value.newBuilder().setStringValue("het-RA-[C]"));
    overlappingCallSetNames1.addValues(Value.newBuilder().setStringValue("het-AA-[C, CCA]"));
    overlappingCallSetNames1.addValues(Value.newBuilder().setStringValue("hom-AA-[C]"));
    overlappingCallSetNames1.addValues(Value.newBuilder().setStringValue("het-AA-[C, CC]"));
    info1.put(MergeAllVariantsAtSameSite.OVERLAPPING_CALLSETS_FIELD, overlappingCallSetNames1.build());
    Variant expectedOutput1b = Variant.newBuilder(expectedOutput1)
        .putAllInfo(info1)
        .build();

    Map<String, ListValue> info2 = new HashMap<String, ListValue>();
    ListValue.Builder overlappingCallSetNames2 = ListValue.newBuilder();
    overlappingCallSetNames2.addValues(Value.newBuilder().setStringValue("het-RA-[CA]"));
    overlappingCallSetNames2.addValues(Value.newBuilder().setStringValue("het-RA-[CAAA]"));
    overlappingCallSetNames2.addValues(Value.newBuilder().setStringValue("het-AA-[G, CA]"));
    overlappingCallSetNames2.addValues(Value.newBuilder().setStringValue("hom-AA-[C]"));
    overlappingCallSetNames2.addValues(Value.newBuilder().setStringValue("het-AA-[C, CC]"));
    info2.put(MergeAllVariantsAtSameSite.OVERLAPPING_CALLSETS_FIELD, overlappingCallSetNames2.build());
    Variant expectedOutput2b =  Variant.newBuilder(expectedOutput2)
        .putAllInfo(info2)
        .build();

    Map<String, ListValue> info3 = new HashMap<String, ListValue>();
    ListValue.Builder overlappingCallSetNames3 = ListValue.newBuilder();
    overlappingCallSetNames3.addValues(Value.newBuilder().setStringValue("het-RA-[CA]"));
    overlappingCallSetNames3.addValues(Value.newBuilder().setStringValue("het-RA-[CAAA]"));
    overlappingCallSetNames3.addValues(Value.newBuilder().setStringValue("het-AA-[G, CA]"));
    overlappingCallSetNames3.addValues(Value.newBuilder().setStringValue("het-RA-[C]"));
    overlappingCallSetNames3.addValues(Value.newBuilder().setStringValue("het-AA-[C, CCA]"));
    info3.put(MergeAllVariantsAtSameSite.OVERLAPPING_CALLSETS_FIELD, overlappingCallSetNames3.build());
    Variant expectedOutput3b =  Variant.newBuilder(expectedOutput3)
        .putAllInfo(info3)
        .build();

    // Ensure that the result is stable regardless of the input order of these Variants.
    List<Variant> input = Arrays.asList(delete1BiAllelic, deleteMultiAllelic, delete2BiAllelic, indelMultiAllelic,
        insert1BiAllelic, insert2BiAllelic, snpInsertMultiAllelic);
    for (int i = 0; i < 5; i++) {
      Collections.shuffle(input);
      VariantMergeStrategyTestHelper.mergeTest(100L, input,
          Arrays.asList(expectedOutput1b, expectedOutput2b, expectedOutput3b),
          MergeAllVariantsAtSameSite.class);
    }
  }
}
