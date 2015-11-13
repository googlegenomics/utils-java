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

import io.grpc.ManagedChannel;
import io.grpc.Server;
import io.grpc.Status;
import io.grpc.inprocess.InProcessChannelBuilder;
import io.grpc.inprocess.InProcessServerBuilder;
import io.grpc.stub.StreamObserver;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Random;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import com.google.cloud.genomics.utils.IntegrationTestHelper;
import com.google.cloud.genomics.utils.ShardBoundary;
import com.google.cloud.genomics.utils.ShardUtils;
import com.google.common.collect.ImmutableList;
import com.google.genomics.v1.StreamVariantsRequest;
import com.google.genomics.v1.StreamVariantsResponse;
import com.google.genomics.v1.StreamingVariantServiceGrpc;

@RunWith(JUnit4.class)
public class FaultyGenomicsServerITCase {
  public static final String SERVER_NAME = "integrationTest";
  
  protected static Server server;
  protected static IntegrationTestHelper helper;
  protected static ManagedChannel genomicsChannel;
  
  // Variable accessed by both the InProcess Server executor threads and the test thread.
  protected static volatile double faultPercentage = 0.0;

  /**
   * Starts the in-process server that calls the real service.
   * 
   * @throws GeneralSecurityException
   * @throws IOException
   */
  @BeforeClass
  public static void startServer() throws IOException, GeneralSecurityException {
    try {
      server = InProcessServerBuilder.forName(SERVER_NAME)
              .addService(StreamingVariantServiceGrpc.bindService(new VariantsIntegrationServerImpl()))
              .build().start();
    } catch (IOException ex) {
      throw new RuntimeException(ex);
    }
    helper = new IntegrationTestHelper();
    genomicsChannel = GenomicsChannel.fromOfflineAuth(helper.getAuth());
  }

  @AfterClass
  public static void stopServer() {
    server.shutdownNow();
  }

  protected static class VariantsIntegrationServerImpl implements
      StreamingVariantServiceGrpc.StreamingVariantService {
    final Random random = new Random();

    @Override
    public void streamVariants(StreamVariantsRequest request,
        final StreamObserver<StreamVariantsResponse> responseObserver) {

      StreamingVariantServiceGrpc.newStub(genomicsChannel).streamVariants(request,
          new StreamObserver<StreamVariantsResponse>() {
            private boolean injectedError;
            
            @Override
            public void onNext(StreamVariantsResponse response) {
              if (injectedError) {
                return;
              }
              double rand = random.nextDouble();
              if (faultPercentage > rand) {
                responseObserver.onError(Status.UNAVAILABLE.withDescription("injected fault")
                    .asRuntimeException());
                injectedError = true;
                // TODO: this works to cancel the call, but investigate other options
                throw new RuntimeException("cancel the call");
              }
              responseObserver.onNext(response);
            }

            @Override
            public void onError(Throwable t) {
              if (injectedError) {
                return;
              }
              responseObserver.onError(t);
            }

            @Override
            public void onCompleted() {
              if (injectedError) {
                return;
              }
              responseObserver.onCompleted();
            }
          });
    }
  }

  public ManagedChannel createChannel() {
    return InProcessChannelBuilder.forName(SERVER_NAME).build();
  }

  public void runRetryTest(final GenomicsStreamIterator iter, double faultPercentage, int expectedNumItems) {
    FaultyGenomicsServerITCase.faultPercentage = faultPercentage;
    TestHelper.consumeStreamTest(iter, expectedNumItems);
  }
  
  @Test
  public void testVariantRetries() {
    ImmutableList<StreamVariantsRequest> requests =
        ShardUtils.getVariantRequests(helper.PLATINUM_GENOMES_VARIANTSET,
            helper.PLATINUM_GENOMES_BRCA1_REFERENCES, 1000000000L);
    VariantStreamIterator iter = VariantStreamIterator.enforceShardBoundary(createChannel(), requests.get(0), 
        ShardBoundary.Requirement.STRICT, null);
    // Dev Note: this data currently comes back as 20 separate lists but this is controlled server-side.
    // We're using a pretty high fault rate here (25%) to ensure we see a few faults during each test run.
    runRetryTest(iter, 0.25, helper.PLATINUM_GENOMES_BRCA1_EXPECTED_NUM_VARIANTS);
  }
  
}
