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

import com.google.cloud.genomics.utils.ShardBoundary;
import com.google.genomics.v1.StreamReadsRequest;
import com.google.genomics.v1.StreamReadsResponse;
import com.google.genomics.v1.StreamVariantsRequest;
import com.google.genomics.v1.StreamVariantsResponse;
import com.google.genomics.v1.StreamingReadServiceGrpc;
import com.google.genomics.v1.StreamingVariantServiceGrpc;
import com.google.protobuf.Message;

import org.junit.After;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mockito;

import io.grpc.ManagedChannel;
import io.grpc.Server;
import io.grpc.Status;
import io.grpc.inprocess.InProcessChannelBuilder;
import io.grpc.inprocess.InProcessServerBuilder;
import io.grpc.stub.StreamObserver;

import java.io.IOException;

/**
 * Retry tests for reads and variants.
 *
 * Test retries that occur at:
 *
 * (1) the beginning of the stream
 * (2) within records that overlap the start position
 * (3) occur at the start position
 * (4) beyond the start position
 *
 * Test should confirm that all records are returned only once upon successful completion of the
 * retried stream.
 */
@RunWith(JUnit4.class)
public class GenomicsStreamIteratorRetryTest {
  public static final StreamReadsResponse[] READ_RESPONSES = {
      StreamReadsResponse.newBuilder().addAlignments(TestHelper.makeRead(400, 505))
          .addAlignments(TestHelper.makeRead(400, 510))
          .addAlignments(TestHelper.makeRead(450, 600)).build(),
      StreamReadsResponse.newBuilder().addAlignments(TestHelper.makeRead(450, 610))
          .addAlignments(TestHelper.makeRead(500, 505))
          .addAlignments(TestHelper.makeRead(505, 511)).build(),
      StreamReadsResponse.newBuilder().addAlignments(TestHelper.makeRead(505, 700))
          .addAlignments(TestHelper.makeRead(511, 555))
          .addAlignments(TestHelper.makeRead(511, 556)).build()};
  public static final StreamVariantsResponse[] VARIANT_RESPONSES = {
      StreamVariantsResponse.newBuilder().addVariants(TestHelper.makeVariant(400, 505))
          .addVariants(TestHelper.makeVariant(400, 510))
          .addVariants(TestHelper.makeVariant(450, 600)).build(),
      StreamVariantsResponse.newBuilder().addVariants(TestHelper.makeVariant(450, 610))
          .addVariants(TestHelper.makeVariant(500, 505))
          .addVariants(TestHelper.makeVariant(505, 511)).build(),
      StreamVariantsResponse.newBuilder().addVariants(TestHelper.makeVariant(505, 700))
          .addVariants(TestHelper.makeVariant(511, 555))
          .addVariants(TestHelper.makeVariant(511, 556)).build()};
  public static final long REQUEST_START_POSITION = 450;
  public static final StreamReadsRequest READS_REQUEST = StreamReadsRequest.newBuilder()
      .setStart(REQUEST_START_POSITION).build();
  public static final StreamVariantsRequest VARIANTS_REQUEST = StreamVariantsRequest.newBuilder()
      .setStart(REQUEST_START_POSITION).build();

  enum InjectionSite {
    AT_BEGINNING, AFTER_FIRST_RESPONSE, AFTER_SECOND_RESPONSE, AT_END
  };

  @Rule
  public TestName testName = new TestName();

  protected Server server;

  protected static class FaultInjector {

    protected final InjectionSite injectionSite;
    protected volatile boolean failNow;  // Accessed by the InProcess Server executor threads.

    protected FaultInjector(InjectionSite targetSite) {
      injectionSite = targetSite;
      failNow = true;
    }

    protected synchronized boolean shouldInjectNow(InjectionSite currentSite) {
        if (failNow && injectionSite.equals(currentSite)) {
          failNow = false;
          return true;
        }
        return false;
    }

    protected synchronized void respondWithFaults(StreamObserver responseObserver, Message[] responses) {
      if (shouldInjectNow(InjectionSite.AT_BEGINNING)) {
        responseObserver.onError(Status.UNAVAILABLE.withDescription("injected fault")
            .asRuntimeException());
        return;
      } else {
        responseObserver.onNext(responses[0]);
      }

      if (shouldInjectNow(InjectionSite.AFTER_FIRST_RESPONSE)) {
        responseObserver.onError(Status.UNAVAILABLE.withDescription("injected fault")
            .asRuntimeException());
        return;
      } else {
        responseObserver.onNext(responses[1]);
      }

      if (shouldInjectNow(InjectionSite.AFTER_SECOND_RESPONSE)) {
        responseObserver.onError(Status.UNAVAILABLE.withDescription("injected fault")
            .asRuntimeException());
        return;
      } else {
        responseObserver.onNext(responses[2]);
      }

      if (shouldInjectNow(InjectionSite.AT_END)) {
        responseObserver.onError(Status.UNAVAILABLE.withDescription("injected fault")
            .asRuntimeException());
        return;
      } else {
        responseObserver.onCompleted();
      }
    }
  }

  protected static class ReadUnitServerImpl extends StreamingReadServiceGrpc.StreamingReadServiceImplBase {
    final FaultInjector faultInjector;

    public ReadUnitServerImpl(InjectionSite targetSite) {
      faultInjector = new FaultInjector(targetSite);
    }

    @Override
    public void streamReads(StreamReadsRequest request,
        StreamObserver<StreamReadsResponse> responseObserver) {
      faultInjector.respondWithFaults(responseObserver, READ_RESPONSES);
    }
  }

  protected static class VariantUnitServerImpl extends StreamingVariantServiceGrpc.StreamingVariantServiceImplBase {
    final FaultInjector faultInjector;

    public VariantUnitServerImpl(InjectionSite targetSite) {
      faultInjector = new FaultInjector(targetSite);
    }

    @Override
    public void streamVariants(StreamVariantsRequest request,
        StreamObserver<StreamVariantsResponse> responseObserver) {
      faultInjector.respondWithFaults(responseObserver, VARIANT_RESPONSES);
    }
  }

  /**
   * Starts the in-process server configured to inject one fault at the specified target site.
   *
   * @param targetSite
   */
  public void startServer(InjectionSite targetSite) {
    try {
      server =
          InProcessServerBuilder.forName(testName.getMethodName())
              .addService(new ReadUnitServerImpl(targetSite))
              .addService(new VariantUnitServerImpl(targetSite))
              .build()
              .start();
    } catch (IOException ex) {
      throw new RuntimeException(ex);
    }
  }

  @After
  public void stopServer() {
    server.shutdownNow();
  }

  public ManagedChannel createChannel() {
    return InProcessChannelBuilder.forName(testName.getMethodName()).build();
  }

  public void runTest(Message request, ShardBoundary.Requirement requirement,
      InjectionSite targetSite, int expectedNumItems) {
    startServer(targetSite);
    GenomicsStreamIterator rawIterator =
        (request instanceof StreamVariantsRequest)
        ? VariantStreamIterator.enforceShardBoundary(createChannel(), (StreamVariantsRequest) request, requirement, null)
            : ReadStreamIterator.enforceShardBoundary(createChannel(), (StreamReadsRequest) request, requirement, null);
    GenomicsStreamIterator iteratorSpy = Mockito.spy(rawIterator);
    TestHelper.consumeStreamTest(iteratorSpy, expectedNumItems);

    // Confirm that the client retried the failed stream at an updated start position to avoid
    // pulling an excessive amount of duplicate data.
    if (InjectionSite.AFTER_SECOND_RESPONSE.equals(targetSite)) {
      Mockito.verify(iteratorSpy, Mockito.times(1)).getRevisedRequest(505L);
    } else if (InjectionSite.AT_END.equals(targetSite)) {
      Mockito.verify(iteratorSpy, Mockito.times(1)).getRevisedRequest(511L);
    } else {
      Mockito.verify(iteratorSpy, Mockito.times(0)).getRevisedRequest(REQUEST_START_POSITION);
    }
  }

  // The following tests could be collapsed into a for loop upon the injection site enumeration,
  // but breaking them out separately makes it easier to understand failures if they happen.

  @Test
  public void testVariantStrictRetriesAfterFirstResponse() {
    runTest(VARIANTS_REQUEST, ShardBoundary.Requirement.STRICT,
        InjectionSite.AFTER_FIRST_RESPONSE, 7);
  }

  @Test
  public void testVariantOverlappingRetriesAfterFirstResponse() {
    runTest(VARIANTS_REQUEST, ShardBoundary.Requirement.OVERLAPS,
        InjectionSite.AFTER_FIRST_RESPONSE, 9);
  }

  @Test
  public void testReadStrictRetriesAfterFirstResponse() {
    runTest(READS_REQUEST, ShardBoundary.Requirement.STRICT,
        InjectionSite.AFTER_FIRST_RESPONSE, 7);
  }

  @Test
  public void testReadOverlappingRetriesAfterFirstResponse() {
    runTest(READS_REQUEST, ShardBoundary.Requirement.OVERLAPS,
        InjectionSite.AFTER_FIRST_RESPONSE, 9);
  }

  @Test
  public void testVariantStrictRetriesAfterSecondResponse() {
    runTest(VARIANTS_REQUEST, ShardBoundary.Requirement.STRICT,
        InjectionSite.AFTER_SECOND_RESPONSE, 7);
  }

  @Test
  public void testVariantOverlappingRetriesAfterSecondResponse() {
    runTest(VARIANTS_REQUEST, ShardBoundary.Requirement.OVERLAPS,
        InjectionSite.AFTER_SECOND_RESPONSE, 9);
  }

  @Test
  public void testReadStrictRetriesAfterSecondResponse() {
    runTest(READS_REQUEST, ShardBoundary.Requirement.STRICT,
        InjectionSite.AFTER_SECOND_RESPONSE, 7);
  }

  @Test
  public void testReadOverlappingRetriesAfterSecondResponse() {
    runTest(READS_REQUEST, ShardBoundary.Requirement.OVERLAPS,
        InjectionSite.AFTER_SECOND_RESPONSE, 9);
  }

  @Test
  public void testVariantStrictRetriesAtBeginning() {
    runTest(VARIANTS_REQUEST, ShardBoundary.Requirement.STRICT,
        InjectionSite.AT_BEGINNING, 7);
  }

  @Test
  public void testVariantOverlappingRetriesAtBeginning() {
    runTest(VARIANTS_REQUEST, ShardBoundary.Requirement.OVERLAPS,
        InjectionSite.AT_BEGINNING, 9);
  }

  @Test
  public void testReadStrictRetriesAtBeginning() {
    runTest(READS_REQUEST, ShardBoundary.Requirement.STRICT,
        InjectionSite.AT_BEGINNING, 7);
  }

  @Test
  public void testReadOverlappingRetriesAtBeginning() {
    runTest(READS_REQUEST, ShardBoundary.Requirement.OVERLAPS,
        InjectionSite.AT_BEGINNING, 9);
  }

  @Test
  public void testVariantStrictRetriesAtEnd() {
    runTest(VARIANTS_REQUEST, ShardBoundary.Requirement.STRICT,
        InjectionSite.AT_END, 7);
  }

  @Test
  public void testVariantOverlappingRetriesAtEnd() {
    runTest(VARIANTS_REQUEST, ShardBoundary.Requirement.OVERLAPS,
        InjectionSite.AT_END, 9);
  }

  @Test
  public void testReadStrictRetriesAtEnd() {
    runTest(READS_REQUEST, ShardBoundary.Requirement.STRICT,
        InjectionSite.AT_END, 7);
  }

  @Test
  public void testReadOverlappingRetriesAtEnd() {
    runTest(READS_REQUEST, ShardBoundary.Requirement.OVERLAPS,
        InjectionSite.AT_END, 9);
  }
}
