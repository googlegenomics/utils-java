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

import static org.junit.Assert.assertEquals;

import com.google.cloud.genomics.utils.IntegrationTestHelper;
import com.google.cloud.genomics.utils.ShardBoundary;
import com.google.cloud.genomics.utils.ShardUtils;
import com.google.common.collect.ImmutableList;
import com.google.genomics.v1.StreamVariantsRequest;
import com.google.genomics.v1.StreamVariantsResponse;
import com.google.genomics.v1.Variant;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.Arrays;
import java.util.Iterator;

/**
 * To run the test: mvn -Dit.test=MergeNonVariantSegmentsWithSnpsITCase verify
 *
 * Use the following API explorer link or BigQuery query to see the data used in this test.
 *   https://developers.google.com/apis-explorer/#p/genomics/v1/genomics.variants.stream?fields=variants(alternateBases%252Ccalls(callSetName%252Cgenotype)%252Cend%252CreferenceBases%252CreferenceName%252Cstart)&_h=2&resource=%257B%250A++%2522variantSetId%2522%253A+%25223049512673186936334%2522%252C%250A++%2522referenceName%2522%253A+%2522chr17%2522%252C%250A++%2522start%2522%253A+%252241204796%2522%252C%250A++%2522end%2522%253A+%252241204797%2522%250A%257D&
 *
SELECT
  reference_name,
  start,
  end,
  reference_bases,
  GROUP_CONCAT(alternate_bases) WITHIN RECORD AS alternate_bases,
  call.call_set_name,
  GROUP_CONCAT(STRING(call.genotype)) WITHIN call AS genotype,
FROM
  [genomics-public-data:platinum_genomes.variants]
WHERE
  reference_name CONTAINS 'chr17'
  AND start <= 41204796 and end >= 41204797
ORDER BY
  call.call_set_name,
  start,
  alternate_bases
 *
 */
@RunWith(JUnit4.class)
public class MergeNonVariantSegmentsWithSnpsITCase {

  public static final StreamVariantsRequest PROTOTYPE = StreamVariantsRequest.newBuilder()
      .setVariantSetId(IntegrationTestHelper.PLATINUM_GENOMES_VARIANTSET)
      .setProjectId(IntegrationTestHelper.getTEST_PROJECT())
      .build();

  @Test
  public void testMerge() throws Exception {
    ImmutableList<StreamVariantsRequest> requests =
        ShardUtils.getVariantRequests(PROTOTYPE,
            100, "chr17:41204796:41204797");
    assertEquals(1, requests.size());

    Iterator<StreamVariantsResponse> iter =
        VariantStreamIterator.enforceShardBoundary(IntegrationTestHelper.getAuthFromApplicationDefaultCredential(),
            requests.get(0),
            ShardBoundary.Requirement.OVERLAPS,
            "variants(alternateBases,calls(callSetName,genotype),end,referenceBases,referenceName,start)");

    // Platinum genomes has both a snp and an insertion at this genomic site.
    Variant expectedOutput1 = TestHelper.makeVariant("chr17", 41204796, 41204797, "A", Arrays.asList("AAC"))
        .addCalls(TestHelper.makeCall("NA12882", 1, 1))
        .addCalls(TestHelper.makeCall("NA12879", 1, 1))
        .addCalls(TestHelper.makeCall("NA12891", 0, 1))
        .addCalls(TestHelper.makeCall("NA12878", 0, 1))
        .addCalls(TestHelper.makeCall("NA12884", 1, 1))
        .build();

    Variant expectedOutput2 = TestHelper.makeVariant("chr17", 41204796, 41204797, "A", Arrays.asList("C"))
        .addCalls(TestHelper.makeCall("NA12882", 0, 1))
        .addCalls(TestHelper.makeCall("NA12879", 0, 1))
        .addCalls(TestHelper.makeCall("NA12892", 0, 0))
        .addCalls(TestHelper.makeCall("NA12893", 0, 0))
        .addCalls(TestHelper.makeCall("NA12890", 0, 0))
        .addCalls(TestHelper.makeCall("NA12883", 0, 0))
        .addCalls(TestHelper.makeCall("NA12891", 0, 0))
        .addCalls(TestHelper.makeCall("NA12880", 0, 0))
        .addCalls(TestHelper.makeCall("NA12877", 0, 0))
        .addCalls(TestHelper.makeCall("NA12885", 0, 0))
        .addCalls(TestHelper.makeCall("NA12889", 0, 0))
        .addCalls(TestHelper.makeCall("NA12887", 0, 0))
        .addCalls(TestHelper.makeCall("NA12881", 0, 0))
        .addCalls(TestHelper.makeCall("NA12888", 0, 0))
        .addCalls(TestHelper.makeCall("NA12886", 0, 0))
        .addCalls(TestHelper.makeCall("NA12878", 0, 0))
        .addCalls(TestHelper.makeCall("NA12884", 0, 0))
        .build();

    VariantMergeStrategyTestHelper.mergeTest(requests.get(0).getStart(), iter.next().getVariantsList(),
        Arrays.asList(expectedOutput1, expectedOutput2),
        MergeNonVariantSegmentsWithSnps.class);
  }
}
