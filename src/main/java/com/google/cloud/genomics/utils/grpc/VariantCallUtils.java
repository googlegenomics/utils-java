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
import com.google.common.collect.Ordering;
import com.google.genomics.v1.VariantCall;

import java.util.Comparator;

/**
 * Predicates and comparators for variant calls.
 *
 */
public class VariantCallUtils {

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

}
