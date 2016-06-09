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

import com.google.common.collect.Lists;
import com.google.genomics.v1.Variant;
import com.google.genomics.v1.Variant.Builder;

import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * This strategy converts data with non-variant segments (such as data that was in
 * source format Genome VCF (gVCF) or Complete Genomics) to variant-only data occurring
 * within the genomic window with calls from non-variant-segments merged into the
 * variants with which they overlap.
 *
 * Dealing with ambiguous data:
 *  If a particular sample has both a variant and one or more non-variant segments that overlap it,
 *  all ambiguous calls are returned to be disambiguated or flagged further downstream.
 */
public class MergeNonVariantSegmentsWithVariants implements VariantMergeStrategy {

  @Override
  public void merge(Long windowStart, Iterable<Variant> variants, VariantEmitterStrategy emitter) {
    // The sort order is critical here so that candidate overlapping reference matching blocks
    // occur prior to any variants they may overlap.
    List<Variant> records = Lists.newArrayList(variants);  // Get a modifiable list.
    Collections.sort(records, VariantUtils.NON_VARIANT_SEGMENT_COMPARATOR);

    // The upper bound on potential overlaps is the sample size plus the number of
    // block records that occur between actual variants.
    List<Variant> blockRecords = new LinkedList<>();

    for (Variant record : records) {
      if (!VariantUtils.IS_NON_VARIANT_SEGMENT.apply(record)) {
        if (record.getStart() < windowStart) {
          // This is a variant that begins before our window.  Skip it.
          continue;
        }
        Builder updatedRecord = Variant.newBuilder(record);
        for (Iterator<Variant> iterator = blockRecords.iterator(); iterator.hasNext();) {
          Variant blockRecord = iterator.next();
          if (VariantUtils.isOverlapping(blockRecord, record)) {
            updatedRecord.addAllCalls(blockRecord.getCallsList());
          } else {
            // Remove the current element from the iterator and the list since it is
            // left of the genomic region we are currently working on due to our sort.
            iterator.remove();
          }
        }
        // Emit this variant and move on (no need to hang onto it in memory).
        emitter.emit(updatedRecord.build());
      } else {
        blockRecords.add(record);
      }
    }
  }
}
