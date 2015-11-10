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

import static org.junit.Assert.assertEquals;

import java.io.IOException;

import io.grpc.ManagedChannel;
import io.grpc.Server;
import io.grpc.Status;
import io.grpc.inprocess.InProcessChannelBuilder;
import io.grpc.inprocess.InProcessServerBuilder;
import io.grpc.stub.StreamObserver;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import com.google.cloud.genomics.utils.ShardBoundary;
import com.google.genomics.v1.StreamReadsRequest;
import com.google.genomics.v1.StreamReadsResponse;
import com.google.genomics.v1.StreamVariantsRequest;
import com.google.genomics.v1.StreamVariantsResponse;
import com.google.genomics.v1.StreamingReadServiceGrpc;
import com.google.genomics.v1.StreamingVariantServiceGrpc;
import com.google.protobuf.Message;

/**
 * Retry tests for reads and variants.
 * 
 * Test retries that occur at: 
 * 
 *  (1) the beginning of the stream
 *  (2) within records that overlap the start position 
 *  (3) occur at the start position 
 *  (4) beyond the start position
 *
 * Test should confirm that all records are returned only once upon successful completion of the
 * retried stream.
 */
@RunWith(JUnit4.class)
public class GenomicsStreamIteratorRetryTest {
  public static final String SERVER_NAME = "unitTest";
  public static final StreamReadsResponse[] READ_RESPONSES = {
    StreamReadsResponse.newBuilder()
    .addAlignments(TestHelper.makeRead(400, 505))
    .addAlignments(TestHelper.makeRead(400, 510))
    .addAlignments(TestHelper.makeRead(450, 600)).build(),
    StreamReadsResponse.newBuilder()
    .addAlignments(TestHelper.makeRead(450, 610))
    .addAlignments(TestHelper.makeRead(500, 505))
    .addAlignments(TestHelper.makeRead(505, 511)).build(),
    StreamReadsResponse.newBuilder()
    .addAlignments(TestHelper.makeRead(505, 700))
    .addAlignments(TestHelper.makeRead(511, 555))
    .addAlignments(TestHelper.makeRead(511, 556)).build()
  };
  public static final StreamVariantsResponse[] VARIANT_RESPONSES = {
    StreamVariantsResponse.newBuilder()
    .addVariants(TestHelper.makeVariant(400, 505))
    .addVariants(TestHelper.makeVariant(400, 510))
    .addVariants(TestHelper.makeVariant(450, 600)).build(),
    StreamVariantsResponse.newBuilder()
    .addVariants(TestHelper.makeVariant(450, 610))
    .addVariants(TestHelper.makeVariant(500, 505))
    .addVariants(TestHelper.makeVariant(505, 511)).build(),
    StreamVariantsResponse.newBuilder()
    .addVariants(TestHelper.makeVariant(505, 700))
    .addVariants(TestHelper.makeVariant(511, 555))
    .addVariants(TestHelper.makeVariant(511, 556)).build()
  };
  public static final long REQUEST_START_POSITION = 450;
  public static final StreamReadsRequest READS_REQUEST = StreamReadsRequest.newBuilder()
      .setStart(REQUEST_START_POSITION).build();
  public static final StreamVariantsRequest VARIANTS_REQUEST = StreamVariantsRequest.newBuilder()
      .setStart(REQUEST_START_POSITION).build();
  
  enum InjectionSite {
    AT_BEGINNING, AFTER_FIRST_RESPONSE, AFTER_SECOND_RESPONSE, AT_END
  };

  protected static InjectionSite injectionSite;
  protected static boolean failNow;
  protected static long lastObservedRequestStartPosition;
  protected static Server server;
  
  /**
   * Starts the in-process server. 
   */
  @BeforeClass
  public static void startServer() {
    try {
      server = InProcessServerBuilder.forName(SERVER_NAME)
          .addService(StreamingReadServiceGrpc.bindService(new UnitServerImpl()))
          .addService(StreamingVariantServiceGrpc.bindService(new UnitServerImpl()))
          .build().start();
    } catch (IOException ex) {
      throw new RuntimeException(ex);
    }
  }

  @AfterClass
  public static void stopServer() {
    server.shutdownNow();
  }
  
  protected static class UnitServerImpl implements
  StreamingReadServiceGrpc.StreamingReadService,
      StreamingVariantServiceGrpc.StreamingVariantService {
    
    @Override
    public void streamReads(StreamReadsRequest request,
        StreamObserver<StreamReadsResponse> responseObserver) {
      // Set this to later confirm that the request start position is updated
      // for retrying data beyond the start position.
      lastObservedRequestStartPosition = request.getStart();
      respondWithFaults(responseObserver, READ_RESPONSES);
    }

    @Override
    public void streamVariants(StreamVariantsRequest request,
        StreamObserver<StreamVariantsResponse> responseObserver) {
      // Set this to later confirm that the request start position is updated
      // for retrying data beyond the start position.
      lastObservedRequestStartPosition = request.getStart();
      respondWithFaults(responseObserver, VARIANT_RESPONSES);
    }

    protected void respondWithFaults(StreamObserver responseObserver, Message[] responses) {
      if (failNow && InjectionSite.AT_BEGINNING.equals(injectionSite)) {
        failNow = !failNow;
        responseObserver.onError(Status.UNAVAILABLE.withDescription("injected fault")
            .asRuntimeException());
        return;
      } else {
        responseObserver.onNext(responses[0]);
      }

      if (failNow && InjectionSite.AFTER_FIRST_RESPONSE.equals(injectionSite)) {
        failNow = !failNow;
        responseObserver.onError(Status.UNAVAILABLE.withDescription("injected fault")
            .asRuntimeException());
        return;
      } else {
        responseObserver.onNext(responses[1]);
      }

      if (failNow && InjectionSite.AFTER_SECOND_RESPONSE.equals(injectionSite)) {
        failNow = !failNow;
        responseObserver.onError(Status.UNAVAILABLE.withDescription("injected fault")
            .asRuntimeException());
        return;
      } else {
        responseObserver.onNext(responses[2]);
      }

      if (failNow && InjectionSite.AT_END.equals(injectionSite)) {
        failNow = !failNow;
        responseObserver.onError(Status.UNAVAILABLE.withDescription("injected fault")
            .asRuntimeException());
        return;
      } else {
        responseObserver.onCompleted();
      }
    }
  }

  public ManagedChannel createChannel() {
    return InProcessChannelBuilder.forName(SERVER_NAME).build();
  }

  public void runTest(final GenomicsStreamIterator iter, InjectionSite site, int expectedNumItems) {
    injectionSite = site;
    failNow = true;
    
    TestHelper.consumeStreamTest(iter, expectedNumItems);
    
    if (InjectionSite.AFTER_SECOND_RESPONSE.equals(injectionSite)) {
      assertEquals(505L, lastObservedRequestStartPosition);
    } else if (InjectionSite.AT_END.equals(injectionSite)) {
      assertEquals(511L, lastObservedRequestStartPosition);
    } else {
      assertEquals(REQUEST_START_POSITION, lastObservedRequestStartPosition);
    }
  }
  
  // The following tests could be collapsed into a for loop upon the injection site enumeration,
  // but breaking them out separately makes it easier to understand failures if they happen.

  @Test
  public void testRetriesAfterFirstResponse() {
    InjectionSite site = InjectionSite.AFTER_FIRST_RESPONSE;
    GenomicsStreamIterator iter =
        VariantStreamIterator.enforceShardBoundary(createChannel(), VARIANTS_REQUEST,
            ShardBoundary.Requirement.STRICT, null);
    runTest(iter, site, 7);

    iter =
        VariantStreamIterator.enforceShardBoundary(createChannel(), VARIANTS_REQUEST,
            ShardBoundary.Requirement.OVERLAPS, null);
    runTest(iter, site, 9);

    iter =
        ReadStreamIterator.enforceShardBoundary(createChannel(), READS_REQUEST,
            ShardBoundary.Requirement.STRICT, null);
    runTest(iter, site, 7);

    iter =
        ReadStreamIterator.enforceShardBoundary(createChannel(), READS_REQUEST,
            ShardBoundary.Requirement.OVERLAPS, null);
    runTest(iter, site, 9);
  }

  @Test
  public void testRetriesAfterSecondResponse() {
    InjectionSite site = InjectionSite.AFTER_SECOND_RESPONSE;
    GenomicsStreamIterator iter =
        VariantStreamIterator.enforceShardBoundary(createChannel(), VARIANTS_REQUEST,
            ShardBoundary.Requirement.STRICT, null);
    runTest(iter, site, 7);

    iter =
        VariantStreamIterator.enforceShardBoundary(createChannel(), VARIANTS_REQUEST,
            ShardBoundary.Requirement.OVERLAPS, null);
    runTest(iter, site, 9);

    iter =
        ReadStreamIterator.enforceShardBoundary(createChannel(), READS_REQUEST,
            ShardBoundary.Requirement.STRICT, null);
    runTest(iter, site, 7);

    iter =
        ReadStreamIterator.enforceShardBoundary(createChannel(), READS_REQUEST,
            ShardBoundary.Requirement.OVERLAPS, null);
    runTest(iter, site, 9);
  }

  @Test
  public void testRetriesAtBeginning() {
    InjectionSite site = InjectionSite.AT_BEGINNING;
    GenomicsStreamIterator iter =
        VariantStreamIterator.enforceShardBoundary(createChannel(), VARIANTS_REQUEST,
            ShardBoundary.Requirement.STRICT, null);
    runTest(iter, site, 7);

    iter =
        VariantStreamIterator.enforceShardBoundary(createChannel(), VARIANTS_REQUEST,
            ShardBoundary.Requirement.OVERLAPS, null);
    runTest(iter, site, 9);

    iter =
        ReadStreamIterator.enforceShardBoundary(createChannel(), READS_REQUEST,
            ShardBoundary.Requirement.STRICT, null);
    runTest(iter, site, 7);

    iter =
        ReadStreamIterator.enforceShardBoundary(createChannel(), READS_REQUEST,
            ShardBoundary.Requirement.OVERLAPS, null);
    runTest(iter, site, 9);
  }

  @Test
  public void testRetriesAtEnd() {
    InjectionSite site = InjectionSite.AT_END;
    GenomicsStreamIterator iter =
        VariantStreamIterator.enforceShardBoundary(createChannel(), VARIANTS_REQUEST,
            ShardBoundary.Requirement.STRICT, null);
    runTest(iter, site, 7);

    iter =
        VariantStreamIterator.enforceShardBoundary(createChannel(), VARIANTS_REQUEST,
            ShardBoundary.Requirement.OVERLAPS, null);
    runTest(iter, site, 9);
    
    iter =
        ReadStreamIterator.enforceShardBoundary(createChannel(), READS_REQUEST,
            ShardBoundary.Requirement.STRICT, null);
    runTest(iter, site, 7);

    iter =
        ReadStreamIterator.enforceShardBoundary(createChannel(), READS_REQUEST,
            ShardBoundary.Requirement.OVERLAPS, null);
    runTest(iter, site, 9);
  }
}
