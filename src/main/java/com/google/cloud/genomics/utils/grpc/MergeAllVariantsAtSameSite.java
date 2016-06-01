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
import com.google.common.collect.Collections2;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.genomics.v1.Variant;
import com.google.genomics.v1.Variant.Builder;
import com.google.genomics.v1.VariantCall;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * EXPERIMENTAL
 *
 * This strategy converts data with non-variant segments (such as data that was in
 * source format Genome VCF (gVCF) or Complete Genomics) to variant-only data with calls from
 * non-variant-segments merged into the variants with which they overlap.
 *
 * It additionally merges variants at the same site, renumbering genotypes as needed.
 *
 * Dealing with ambiguous data:
 *  If a particular sample has both a variant call and one or more non-variant segments that overlap it,
 *  the non-variant segment call(s) is discarded and the variant call is retained.
 *
 *  All other ambiguous data is retained (e.g., if a sample has multiple variant calls at the same site
 *  or if a sample has no variant calls at the site but has multiple non-variant-segments).
 */
public class MergeAllVariantsAtSameSite implements VariantMergeStrategy {

  @Override
  public void merge(Iterable<Variant> variants, VariantEmitterStrategy emitter) {
    // The sort order is critical here so that candidate overlapping reference matching blocks
    // occur prior to any variants they may overlap.
    // TODO optimization: remove this sort by restructuring the code to depend upon the order in which data
    // is returned by the the genomics API which is sorted by (variantset id, contig, start pos, variant id).
    List<Variant> records = Lists.newArrayList(variants);  // Get a modifiable list.
    Collections.sort(records, VariantUtils.NON_VARIANT_SEGMENT_COMPARATOR);

    // The upper bound on potential overlaps is the sample size plus the number of
    // block records that occur between actual variants.
    List<Variant> blockRecords = new LinkedList<>();

    Variant.Builder updatedRecord = null;
    List<VariantCall> overlappingBlockRecordCalls = null;
    for (Variant record : records) {
      if (VariantUtils.IS_NON_VARIANT_SEGMENT.apply(record)) {
        blockRecords.add(record);
      } else {
        if (null != updatedRecord && VariantUtils.isSameVariantSite(updatedRecord, record)) {
          // This is another variant at the same position; merge it with the current variant.
          mergeVariants(updatedRecord, record);
        } else {
          // This is a variant at a new position; merge it with any overlapping non-variant segments.
          if (null != updatedRecord) {
            // Since we're at a new position, emit the variant for the previous position and move
            // on (no need to hang onto it in memory).
            emitter.emit(mergeBlockRecordCalls(updatedRecord, overlappingBlockRecordCalls));
          }
          updatedRecord = Variant.newBuilder(record);
          overlappingBlockRecordCalls = new ArrayList();
          for (Iterator<Variant> iterator = blockRecords.iterator(); iterator.hasNext();) {
            Variant blockRecord = iterator.next();
            if (VariantUtils.isOverlapping(blockRecord, record)) {
              overlappingBlockRecordCalls.addAll(blockRecord.getCallsList());
            } else {
              // Remove the current element from the iterator and the list since it is
              // left of the genomic region we are currently working on due to our sort.
              iterator.remove();
            }
          }
        }
      }
    }
    if (null != updatedRecord) {
      // Emit our final variant.
      emitter.emit(mergeBlockRecordCalls(updatedRecord, overlappingBlockRecordCalls));
    }
  }

  static Variant mergeBlockRecordCalls(Variant.Builder destVariant, List<VariantCall> overlappingBlockRecordCalls) {
    final ImmutableSet<String> callSetNames = ImmutableSet.<String>builder().addAll(
        Lists.transform(destVariant.getCallsList(),
            new Function<VariantCall, String>() {
          @Override
          public String apply(final VariantCall c) {
            return c.getCallSetName();
          }
        })).build();

    // Only add calls from block records for callSetNames not already present in the variant.
    Iterable<VariantCall> callsToAdd = Collections2.filter(overlappingBlockRecordCalls,
        new Predicate<VariantCall>() {
      @Override
      public boolean apply(VariantCall call) {
        return !callSetNames.contains(call.getCallSetName());
      }});
    return destVariant.addAllCalls(callsToAdd).build();
  }

  static void mergeVariants(Builder destVariant, Variant srcVariant) {
    // Merge alternates preserving the order of pre-existing alts in the destination.
    // The source alts should be length 1 or 2 (or rarely 3) since its coming from single-sample data.
    // The dest alts theoretically could be infinitely long due to indels, but in practice, 1,000
    // genomes phase 3 multi-allelic site alternates were at most 6 in length.
    for (String srcAlt : srcVariant.getAlternateBasesList()) {
      if (!destVariant.getAlternateBasesList().contains(srcAlt)) {
        destVariant.addAlternateBases(srcAlt);
      }
    }

    // Re-number genotypes and merge calls.
    for (VariantCall call : srcVariant.getCallsList()) {
      VariantCall.Builder updatedCall = VariantCall.newBuilder(call);
      for (int i = 0; i < call.getGenotypeCount(); i++) {
        if (0 < call.getGenotype(i)) {
          int updatedGenotype = destVariant.getAlternateBasesList()
              .indexOf(srcVariant.getAlternateBasesList().get(call.getGenotype(i) - 1)) + 1;
          updatedCall.setGenotype(i, updatedGenotype);
        }
      }
      destVariant.addCalls(updatedCall);
    }
  }
}
