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
package com.google.cloud.genomics.utils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.List;

import org.hamcrest.CoreMatchers;
import org.junit.Test;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.genomics.v1.LinearAlignment;
import com.google.genomics.v1.Position;
import com.google.genomics.v1.Read;
import com.google.genomics.v1.Variant;

import com.google.cloud.genomics.utils.grpc.VariantUtils;


public class ShardBoundaryTest {

  @Test
  public void testGetStrictVariantPredicate() {
    long start = 1000L;
    long end = 2000L;  // This is not used, but its the "extent" mentioned below.

    Variant overlapStartWithinExtent = Variant.newBuilder().setStart(900L).setEnd(1005L).build();
    Variant overlapStartExtent = Variant.newBuilder().setStart(999L).setEnd(5000L).build();
    Variant atStartWithinExtent = Variant.newBuilder().setStart(1000L).setEnd(1002L).build();
    Variant atStartOverlapExtent = Variant.newBuilder().setStart(1000L).setEnd(5000L).build();
    Variant beyondStartWithinExtent = Variant.newBuilder().setStart(1500L).setEnd(1502L).build();
    Variant beyondOverlapExtent = Variant.newBuilder().setStart(1500L).setEnd(5000L).build();
    Variant[] variants = new Variant[] { overlapStartWithinExtent, overlapStartExtent, atStartWithinExtent,
        atStartOverlapExtent, beyondStartWithinExtent, beyondOverlapExtent };

    Predicate<Variant> shardPredicate = ShardBoundary.getStrictVariantPredicate(start); 
    List<Variant> filteredVariants = Lists.newArrayList(Iterables.filter(Arrays.asList(variants), shardPredicate));
    assertEquals(4, filteredVariants.size());
    assertThat(filteredVariants, CoreMatchers.allOf(CoreMatchers.hasItems(atStartWithinExtent,
        atStartOverlapExtent, beyondStartWithinExtent, beyondOverlapExtent)));
  }

  static Read readHelper(int start, int end) {
    Position position = Position.newBuilder().setPosition((long) start).build();
    LinearAlignment alignment = LinearAlignment.newBuilder().setPosition(position).build();
    return Read.newBuilder().setAlignment(alignment).setFragmentLength(end-start).build();
  }

  @Test
  public void testGetStrictReadPredicate() {
    long start = 1000L;
    long end = 2000L;  // This is not used, but its the "extent" mentioned below.

      Read overlapStartWithinExtent = readHelper(900,1005);
      Read overlapStartExtent = readHelper(999, 5000);
      Read atStartWithinExtent = readHelper(1000, 1002);
      Read atStartOverlapExtent = readHelper(1000, 5000);
      Read beyondStartWithinExtent = readHelper(1500, 1502);
      Read beyondOverlapExtent = readHelper(1500, 5000);
      Read[] reads = new Read[] { overlapStartWithinExtent, overlapStartExtent, atStartWithinExtent,
              atStartOverlapExtent, beyondStartWithinExtent, beyondOverlapExtent };

      Predicate<Read> shardPredicate = ShardBoundary.getStrictReadPredicate(start); 
      List<Read> filteredReads = Lists.newArrayList(Iterables.filter(Arrays.asList(reads), shardPredicate));
      assertEquals(4, filteredReads.size());
      assertThat(filteredReads, CoreMatchers.allOf(CoreMatchers.hasItems(atStartWithinExtent,
          atStartOverlapExtent, beyondStartWithinExtent, beyondOverlapExtent)));
  }

  @Test
  public void testGetNonVariantOverlapsPredicate() {
    long start = 1000L;

    Variant overlapStart = Variant.newBuilder().setReferenceBases("T").addAlternateBases("A").setStart(900L).build();
    Variant overlapStartNonVariant = Variant.newBuilder().setReferenceBases("T").setStart(900L).setEnd(1005L).build();
    
    Predicate<Variant> shardPredicate = ShardBoundary.getNonVariantOverlapsPredicate(start);
    
    assertFalse(shardPredicate.apply(overlapStart));
    assertTrue(shardPredicate.apply(overlapStartNonVariant));
  }

}
