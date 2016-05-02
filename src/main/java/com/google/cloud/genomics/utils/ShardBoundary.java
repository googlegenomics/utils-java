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

import com.google.api.client.util.Strings;
import com.google.cloud.genomics.utils.grpc.VariantUtils;
import com.google.common.base.Preconditions;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.genomics.v1.Read;
import com.google.genomics.v1.Variant;

import java.util.regex.Pattern;

/**
 * By default cluster compute jobs working with sharded data from the Genomics API will
 * see any records that span a shard boundary in both shards. In some cases this is
 * desired; in others it is not.  It just depends upon the particular analysis.
 */
public class ShardBoundary {

  /**
   * Enum for shard boundary requirement.
   */
  public enum Requirement {
   /**
   * Use OVERLAPS if data overlapping the start of the shard should be returned.
   */
  OVERLAPS,
  /**
   * Use STRICT if data overlapping the start of the shard should be excluded.
   */
  STRICT,
  /**
   * Use NON_VARIANT_OVERLAPS when non-variant segments overlapping the start of the shard should be
   * retained but variants overlapping the start of the shard should be excluded.
   */
  NON_VARIANT_OVERLAPS
  }

  private static final Pattern READ_FIELD_PATTERN = Pattern.compile(".*\\p{Punct}alignment\\p{Punct}.*");
  private static final Pattern VARIANT_FIELD_PATTERN = Pattern.compile(".*\\p{Punct}start\\p{Punct}.*");

  /**
   * Predicate expressing the logic for which variants should and should not be included in the shard.
   *
   * @param start The start position of the shard.
   * @return Whether the variant would be included in a strict shard boundary.
   */
  public static Predicate<Variant> getStrictVariantPredicate(final long start, String fields) {
    Preconditions
    .checkArgument(Strings.isNullOrEmpty(fields)
        || VARIANT_FIELD_PATTERN.matcher(fields).matches(),
        "Insufficient fields requested in partial response. At a minimum "
            + "include 'variants(start)' to enforce a strict shard boundary.");
    return new Predicate<Variant>() {
      @Override
      public boolean apply(Variant variant) {
        return variant.getStart() >= start;
      }
    };
  }

  /**
   * Predicate expressing the logic for which reads should and should not be included in the shard.
   *
   * @param start The start position of the shard.
   * @return Whether the read would be included in a strict shard boundary.
   */
  public static Predicate<Read> getStrictReadPredicate(final long start, final String fields) {
    Preconditions
    .checkArgument(Strings.isNullOrEmpty(fields)
        || READ_FIELD_PATTERN.matcher(fields).matches(),
        "Insufficient fields requested in partial response. At a minimum "
            + "include 'alignments(alignment)' to enforce a strict shard boundary.");
    return new Predicate<Read>() {
      @Override
      public boolean apply(Read read) {
        return read.getAlignment().getPosition().getPosition() >= start;
      }
    };
  }

  /**
   * Predicate expressing the logic for which variants and non-variant segments should and should
   * not be included in the shard.
   *
   * @param start The start position of the shard.
   * @return Whether the variant would be included in a non-variant overlaps shard boundary.
   */
  public static Predicate<Variant> getNonVariantOverlapsPredicate(final long start, final String fields) {
    return Predicates.or(VariantUtils.IS_NON_VARIANT_SEGMENT, getStrictVariantPredicate(start, fields));
  }
}
