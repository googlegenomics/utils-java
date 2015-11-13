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
package com.google.cloud.genomics.utils.grpc;

import java.io.IOException;
import java.security.GeneralSecurityException;

import org.junit.Test;

import com.google.cloud.genomics.utils.ShardBoundary;
import com.google.cloud.genomics.utils.ShardUtils;
import com.google.common.collect.ImmutableList;
import com.google.genomics.v1.StreamVariantsRequest;

/**
 * This is a long-running test (~20 minutes) and will not be run by either surefire or failsafe by
 * default.
 * 
 * To run it: mvn -Dit.test=FaultyGenomicsServerITLongCase verify
 *
 */
public class FaultyGenomicsServerITLongCase extends FaultyGenomicsServerITCase {

  // Create one long stream.
  private static final ImmutableList<StreamVariantsRequest> requests = ShardUtils.getVariantRequests(
      helper.PLATINUM_GENOMES_VARIANTSET, "chrY:0:60032946", 1000000000L);
  private static final int EXPECTED_CHRY_NUM_VARIANTS = 5971309;

  @Test
  public void testOnePercentVariantFaults() throws IOException, GeneralSecurityException {
    VariantStreamIterator iter =
        VariantStreamIterator.enforceShardBoundary(createChannel(), requests.get(0),
            ShardBoundary.Requirement.STRICT, null);
    runRetryTest(iter, 0.01, EXPECTED_CHRY_NUM_VARIANTS);
  }

  @Test
  public void testFivePercentVariantFaults() throws IOException, GeneralSecurityException {
    VariantStreamIterator iter =
        VariantStreamIterator.enforceShardBoundary(createChannel(), requests.get(0),
            ShardBoundary.Requirement.STRICT, null);
    runRetryTest(iter, 0.05, EXPECTED_CHRY_NUM_VARIANTS);
  }
}
