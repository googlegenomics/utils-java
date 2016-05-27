/*
 * Copyright (C) 2014 Google Inc.
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

import static org.junit.Assert.assertEquals;

import com.google.api.client.util.ExponentialBackOff;
import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.genomics.v1.LinearAlignment;
import com.google.genomics.v1.Position;
import com.google.genomics.v1.Read;
import com.google.genomics.v1.Variant;
import com.google.genomics.v1.VariantCall;
import com.google.protobuf.Message;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class TestHelper {

  public static final List<String> EMPTY_ALT_LIST = ImmutableList.of();

  public static final Map<String, List<Integer>> CALL_MAP = ImmutableMap.<String, List<Integer>>builder()
      .put("hom-NN", Arrays.asList(-1, -1))
      .put("het-NR", Arrays.asList(-1, 0))
      .put("hom-RR", Arrays.asList(0, 0))
      .put("het-RA", Arrays.asList(0, 1))
      .put("hom-AA", Arrays.asList(1, 1))
      .put("het-AA", Arrays.asList(1, 2))
      .build();

  public static VariantCall.Builder makeCall(String name, String callType) {
    return VariantCall.newBuilder()
        .setCallSetName(name)
        .addAllGenotype(CALL_MAP.get(callType));
  }

  public static VariantCall.Builder makeCall(String name, Integer... gt) {
    return VariantCall.newBuilder()
        .setCallSetName(name)
        .addAllGenotype(Arrays.asList(gt));
  }

  public static Variant.Builder makeVariant(String chr, long start, long end, String ref, List<String> alts) {
    return Variant.newBuilder()
        .setReferenceName(chr)
        .setStart(start)
        .setEnd(end)
        .setReferenceBases(ref)
        .addAllAlternateBases(alts);
  }

  public static Variant.Builder makeVariant(String chr, long start, String ref, List<String> alts, String... callTypes) {
    List<VariantCall> calls = Lists.newArrayList();
    for (String callType : callTypes) {
      calls.add(TestHelper.makeCall(Joiner.on('-').join(callType, alts), callType).build());
    }
    return makeVariant(chr, start, start + ref.length(), ref, alts)
        .addAllCalls(calls);
  }

  public static Variant.Builder makeBlockRecord(String chr, long start, long end, String ref, List<String> alts) {
    return makeVariant(chr, start, end, ref, alts)
        .addCalls(TestHelper.makeCall(Joiner.on('-').join("hom-RR", alts), "hom-RR"));
  }

  public static Variant makeVariant(long start, long end) {
    return Variant.newBuilder()
        .setId(UUID.randomUUID().toString())
        .setStart(start)
        .setEnd(end)
        .build();
  }

  public static Read makeRead(long start, long end) {
    Position position = Position.newBuilder().setPosition(start).build();
    LinearAlignment alignment = LinearAlignment.newBuilder().setPosition(position).build();
    return Read.newBuilder()
        .setId(UUID.randomUUID().toString())
        .setAlignment(alignment)
        .setFragmentLength((int) (end - start))
        .build();
  }

  public static void consumeStreamTest(final GenomicsStreamIterator iter, int expectedNumItems) {
    // Tweak the backoff to be static instead of exponential since we are possibly injecting
    // fake faults.  Also note that this is used by both unit and integration tests.
    iter.backoff =
        new ExponentialBackOff.Builder().setInitialIntervalMillis(50).setMultiplier(1).build();

    Function<Message, String> getId = new Function<Message, String>() {
      @Override
      public String apply(Message m) {
        return iter.getDataItemId(m);
      }
    };

    Set<String> uniqueReceivedIds = new HashSet<String>(expectedNumItems);
    int numItemsReceived = 0;
    while (iter.hasNext()) {
      List<Message> items = iter.getDataList(iter.next());
      numItemsReceived += items.size();
      System.out.println("Received so far: " + numItemsReceived);
      uniqueReceivedIds.addAll(Lists.transform(items, getId));
    }
    assertEquals("confirm that we received all the data we expected", expectedNumItems,
        uniqueReceivedIds.size());
    assertEquals("confirm that all data received is unique", uniqueReceivedIds.size(),
        numItemsReceived);
  }
}
