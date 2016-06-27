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

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;

import com.google.common.collect.FluentIterable;
import com.google.common.collect.ImmutableList;
import com.google.genomics.v1.Variant;
import com.google.genomics.v1.VariantCall;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public class VariantCallUtilsTest {

  @Test
  public void testCallComparator() {
    assertTrue(0 == VariantCallUtils.CALL_COMPARATOR.compare(
        VariantCall.newBuilder().setCallSetName("NA12883").build(),
        VariantCall.newBuilder().setCallSetName("NA12883").build()));

    assertTrue(0 > VariantCallUtils.CALL_COMPARATOR.compare(
        VariantCall.newBuilder().setCallSetName("NA12883").build(),
        VariantCall.newBuilder().setCallSetName("NA12884").build()));

    assertTrue(0 < VariantCallUtils.CALL_COMPARATOR.compare(
        VariantCall.newBuilder().setCallSetName("NA12884").build(),
        VariantCall.newBuilder().setCallSetName("NA12883").build()));
  }

  @Test
  public void testNonRefCalls() {
    Variant bothRefHomozygous =
        TestHelper.makeVariant(
            "Chr1",
            "Variant1",
            "C",
            "G",
            ImmutableList.of(
                TestHelper.makeCall("Sample1", 0, 0).build(),
                TestHelper.makeCall("Sample2", 0, 0).build())).build();

    Variant bothHeterozygous =
        TestHelper.makeVariant(
            "Chr1",
            "Variant2",
            "A",
            "G",
            ImmutableList.of(
                TestHelper.makeCall("Sample1", 0, 1).build(),
                TestHelper.makeCall("Sample2", 0, 1).build())).build();

    Variant oneOfEach =
        TestHelper.makeVariant(
            "Chr1",
            "Variant3",
            "T",
            "C",
            ImmutableList.of(
                TestHelper.makeCall("Sample1", 0, 0).build(),
                TestHelper.makeCall("Sample2", 0, 1).build())).build();

    assertTrue(
        FluentIterable.from(bothRefHomozygous.getCallsList())
            .filter(new VariantCallUtils.NonRefCallsPredicate())
            .toList().size() == 0);

    assertThat(
        FluentIterable.from(bothHeterozygous.getCallsList())
            .filter(new VariantCallUtils.NonRefCallsPredicate())
            .toList(),
        containsInAnyOrder(
            TestHelper.makeCall("Sample1", 0, 1).build(),
            TestHelper.makeCall("Sample2", 0, 1).build()));

    assertThat(
        FluentIterable.from(oneOfEach.getCallsList())
            .filter(new VariantCallUtils.NonRefCallsPredicate())
            .toList(),
        containsInAnyOrder(
            TestHelper.makeCall("Sample2", 0, 1).build()));
  }

}
