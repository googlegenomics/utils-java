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

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.Ordering;
import com.google.genomics.v1.VariantCall;

import java.util.Comparator;
import java.util.List;

/**
 * Predicates and comparators for variant calls.
 */
public class VariantCallUtils {

  public static final Predicate<VariantCall> ALLOW_ALL_CALLS = Predicates.alwaysTrue();

  /**
   * Comparator for sorting calls by call set name.
   */
  public static final Comparator<VariantCall> CALL_COMPARATOR = Ordering.natural().onResultOf(
      new Function<VariantCall, String>() {
        @Override
        public String apply(VariantCall call) {
          return call.getCallSetName();
        }
      });

  /**
   * Predicate for the common pattern of using FluentIterable over a list of Calls and operating
   * on only the non-reference calls.
   */
  public static class NonRefCallsPredicate implements Predicate<VariantCall> {
    @Override
    public boolean apply(VariantCall call) {
      return !FluentIterable.from(call.getGenotypeList()).allMatch(Predicates.equalTo(0));
    }
  }

  /**
   * Determines if the the given call contains a reference genotype
   */
  public static final Predicate<VariantCall> HAS_REFERENCE_GENOTYPE = new Predicate<VariantCall>() {
    @Override
    public boolean apply(VariantCall call) {
      List<Integer> genotypeList = call.getGenotypeList();
      SequenceUtils.GenotypeCategory category = SequenceUtils.classifyGenotype(genotypeList);
      if (category == SequenceUtils.GenotypeCategory.REF_HOMOZYGOUS || category == SequenceUtils.GenotypeCategory.REF) {
        return true;
      }
      return false;
    }
  };

}
