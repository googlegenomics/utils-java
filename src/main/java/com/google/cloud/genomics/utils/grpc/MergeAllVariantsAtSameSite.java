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
import com.google.common.collect.Lists;
import com.google.genomics.v1.Variant;
import com.google.genomics.v1.Variant.Builder;
import com.google.genomics.v1.VariantCall;
import com.google.protobuf.ListValue;
import com.google.protobuf.Value;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * EXPERIMENTAL
 *
 * This strategy converts data with non-variant segments (such as data that was in
 * source format Genome VCF (gVCF) or Complete Genomics) to variant-only data with calls from
 * non-variant-segments merged into the variants with which they overlap.
 *
 * It additionally:
 *  - Merges variants at the same site, renumbering genotypes as needed.
 *  - Makes note of any callsets in other variants that overlap the current variant.  This facilitates,
 *    for example, a more accurate denominator for allelic frequency.
 *
 * Dealing with ambiguous data:
 *  All ambiguous data is retained.  The consumer of this merged data can reconcile ambiguity, if desired.
 */
public class MergeAllVariantsAtSameSite implements VariantMergeStrategy {

  public static final String OVERLAPPING_CALLSETS_FIELD = "overlappingCallsets";

  @Override
  public void merge(Long windowStart, Iterable<Variant> variants, VariantEmitterStrategy emitter) {
    // The sort order is critical here so that candidate overlapping reference matching blocks
    // occur prior to any variants they may overlap.  Additionally, this ensures that merged variants
    // always wind up with the same ordering on alternate bases and re-written genotypes regardless of
    // the order of the input data.
    List<Variant> records = Lists.newArrayList(variants);  // Get a modifiable list.
    Collections.sort(records, VariantUtils.NON_VARIANT_SEGMENT_COMPARATOR);

    List<Variant> blockRecords = new LinkedList<>();
    List<Variant> priorStartVariantRecords = new LinkedList<>();
    List<Variant.Builder> currentStartVariantRecords = new LinkedList<>();

    Variant.Builder updatedRecord = null;
    for (Variant record : records) {
      if (VariantUtils.IS_NON_VARIANT_SEGMENT.apply(record)) {
        blockRecords.add(record);
        continue;
      }
      if (record.getStart() < windowStart) {
        // This is a variant that begins before our window. We'll only consider it for overlaps.
        priorStartVariantRecords.add(record);
        continue;
      }
      if (null != updatedRecord && VariantUtils.isSameVariantSite(updatedRecord, record)) {
        // This is another variant at the same position; merge it with the current variant.
        mergeVariants(updatedRecord, record);
      } else {
        // This is a variant at a new site.
        if (null != updatedRecord) {
          if (updatedRecord.getStart() == record.getStart()) {
            // We're still at the same position, queue updatedRecord for subsequent emission.
            currentStartVariantRecords.add(updatedRecord);
          } else {
            // Since we're at a new position, we've accumulated enough information to wrap up
            // and emit the variants for the previous position.
            currentStartVariantRecords.add(updatedRecord);
            emitRecords(emitter, currentStartVariantRecords, priorStartVariantRecords, blockRecords);
          }
        }
        updatedRecord = Variant.newBuilder(record);
      }
    }
    if (null != updatedRecord) {
      // Emit our final variant.
      currentStartVariantRecords.add(updatedRecord);
      emitRecords(emitter, currentStartVariantRecords, priorStartVariantRecords, blockRecords);
    }
  }

  static void emitRecords(VariantEmitterStrategy emitter, List<Variant.Builder> currentStartVariantRecords,
      List<Variant> priorStartVariantRecords, List<Variant> blockRecords) {

    // Make a deep copy of these prior to adding call from block records.  We do
    // this because later on we'll count the calls in these that overlap other
    // variants and we don't want to overcount block record calls.
    List<Variant> copyOfCurrentStartVariantRecords = new ArrayList();
    for (Variant.Builder variantBuilder : currentStartVariantRecords) {
      copyOfCurrentStartVariantRecords.add(variantBuilder.build());
    }

    // Update the records to reflect blockRecords and other variants that overlap them.
    noteOverlappingVariantCalls(currentStartVariantRecords, priorStartVariantRecords);
    mergeOverlappingBlockRecordCalls(currentStartVariantRecords, blockRecords);

    // Emit them to the underlying processing engine.
    for (Variant.Builder variantBuilder : currentStartVariantRecords) {
      Variant variant = variantBuilder.build();
      emitter.emit(variant);
    }

    // Reset our state.
    currentStartVariantRecords.clear();
    priorStartVariantRecords.addAll(copyOfCurrentStartVariantRecords);
  }

  static void mergeOverlappingBlockRecordCalls(List<Variant.Builder> currentStartVariantRecords,
      List<Variant> blockRecords) {

    // Identify the block records overlapping this position.
    List<VariantCall> overlappingBlockRecordCalls = new ArrayList();
    for (Iterator<Variant> iterator = blockRecords.iterator(); iterator.hasNext();) {
      Variant blockRecord = iterator.next();
      if (VariantUtils.isOverlapping(blockRecord, currentStartVariantRecords.get(0))) {
        overlappingBlockRecordCalls.addAll(blockRecord.getCallsList());
      } else if (blockRecord.getStart() < currentStartVariantRecords.get(0).getStart()) {
        // Remove the current element from the iterator and the list since it is
        // left of this position.
        iterator.remove();
      } else {
        // Break out of this loop since we are now examining records to the right of this position.
        break;
      }
    }

    // Add the overlapping block record calls to all variants at this position.
    for (Variant.Builder destVariant : currentStartVariantRecords) {
      destVariant.addAllCalls(overlappingBlockRecordCalls);
    }
  }

  static final Function<VariantCall, Value> GET_CALL_SET_NAME = new Function<VariantCall, Value>() {
    @Override
    public Value apply(VariantCall call) {
      return Value.newBuilder().setStringValue(call.getCallSetName()).build();
    }
  };

  static void noteOverlappingVariantCalls(List<Variant.Builder> currentStartVariantRecords,
      List<Variant> priorStartVariantRecords) {

    // Identify the prior variant records overlapping this position.
    ListValue.Builder priorCallSetNamesBuilder = ListValue.newBuilder();
    for (Iterator<Variant> iterator = priorStartVariantRecords.iterator(); iterator.hasNext();) {
      Variant variantRecord = iterator.next();
      if (VariantUtils.isOverlapping(variantRecord, currentStartVariantRecords.get(0))) {
        priorCallSetNamesBuilder.addAllValues(Lists.transform(variantRecord.getCallsList(),
            GET_CALL_SET_NAME));
      } else if (variantRecord.getStart() < currentStartVariantRecords.get(0).getStart()) {
        // Remove the current element from the iterator and the list since it is
        // left of this position.
        iterator.remove();
      } else {
        // Break out of this loop since we are now examining records to the right of this position.
        break;
      }
    }

    ListValue priorCallSetNames = priorCallSetNamesBuilder.build();
    for (Variant.Builder destVariant : currentStartVariantRecords) {
      ListValue.Builder overlappingCallSetNames = ListValue.newBuilder(priorCallSetNames);

      // Also include the call sets for other variants at this same position to each other.
      for (Variant.Builder srcVariant: currentStartVariantRecords) {
        if (destVariant.equals(srcVariant)) {
          continue;
        }
        overlappingCallSetNames.addAllValues(Lists.transform(srcVariant.getCallsList(),
            GET_CALL_SET_NAME));
      }

      Map<String, ListValue> info = new HashMap<String, ListValue>();
      info.put(OVERLAPPING_CALLSETS_FIELD, overlappingCallSetNames.build());
      destVariant.putAllInfo(info);
    }
  }

  static void mergeVariants(Builder destVariant, Variant srcVariant) {
    // Merge alternates preserving the order of pre-existing alts in the destination.
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
          int updatedGenotype =
              destVariant.getAlternateBasesList().indexOf(
                  srcVariant.getAlternateBasesList().get(call.getGenotype(i) - 1)) + 1;
          updatedCall.setGenotype(i, updatedGenotype);
        }
      }
      destVariant.addCalls(updatedCall);
    }
  }
}
