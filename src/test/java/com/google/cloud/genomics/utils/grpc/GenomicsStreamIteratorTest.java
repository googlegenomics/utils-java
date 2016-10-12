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
import com.google.cloud.genomics.utils.ShardUtils;
import com.google.common.collect.ImmutableList;
import com.google.genomics.v1.StreamReadsRequest;
import com.google.genomics.v1.StreamReadsResponse;
import com.google.genomics.v1.StreamVariantsRequest;
import com.google.genomics.v1.StreamVariantsResponse;
import com.google.genomics.v1.StreamingReadServiceGrpc;
import com.google.genomics.v1.StreamingVariantServiceGrpc;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import io.grpc.ManagedChannel;
import io.grpc.Server;
import io.grpc.inprocess.InProcessChannelBuilder;
import io.grpc.inprocess.InProcessServerBuilder;
import io.grpc.stub.StreamObserver;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Collections;

@RunWith(JUnit4.class)
public class GenomicsStreamIteratorTest {
  public static final String SERVER_NAME = "unitTest";
  public static final StreamReadsRequest PROTOTYPE_READ_REQUEST = StreamReadsRequest.newBuilder()
      .setReadGroupSetId("theReadGroupSetId")
      .setProjectId("theProjectId")
      .build();
  public static final StreamVariantsRequest PROTOTYPE_VARIANT_REQUEST = StreamVariantsRequest.newBuilder()
      .setVariantSetId("theVariantSetId")
      .setProjectId("theProjectId")
      .build();

  protected static Server server;

  /**
   * Starts the in-process server.
   */
  @BeforeClass
  public static void startServer() {
    try {
      server = InProcessServerBuilder.forName(SERVER_NAME)
        .addService(new ReadsUnitServerImpl())
        .addService(new VariantsUnitServerImpl())
        .build().start();
    } catch (IOException ex) {
      throw new RuntimeException(ex);
    }
  }

  @AfterClass
  public static void stopServer() {
    server.shutdownNow();
  }

  protected static class ReadsUnitServerImpl extends StreamingReadServiceGrpc.StreamingReadServiceImplBase {
    @Override
    public void streamReads(StreamReadsRequest request,
        StreamObserver<StreamReadsResponse> responseObserver) {
      StreamReadsResponse response = StreamReadsResponse.newBuilder()
          .addAlignments(TestHelper.makeRead(400, 510))
          .addAlignments(TestHelper.makeRead(450, 505))
          .addAlignments(TestHelper.makeRead(499, 600))
          .build();
      responseObserver.onNext(response);
      responseObserver.onCompleted();
    }
  }

  protected static class VariantsUnitServerImpl extends StreamingVariantServiceGrpc.StreamingVariantServiceImplBase {
    @Override
    public void streamVariants(StreamVariantsRequest request,
        StreamObserver<StreamVariantsResponse> responseObserver) {
      StreamVariantsResponse response = StreamVariantsResponse.newBuilder()
          .addVariants(TestHelper.makeVariant(400, 510))
          .addVariants(TestHelper.makeVariant(450, 505))
          .addVariants(TestHelper.makeVariant(499, 600))
          .build();
      responseObserver.onNext(response);
      responseObserver.onCompleted();
    }
  }

  public ManagedChannel createChannel() {
    return InProcessChannelBuilder.forName(SERVER_NAME).build();
  }

  @Test
  public void testAllReadsOverlapsStart() throws IOException, GeneralSecurityException {
    ImmutableList<StreamReadsRequest> requests =
        ShardUtils.getReadRequests(Collections.singletonList(PROTOTYPE_READ_REQUEST), 1000000L, "chr7:500:600");

    ReadStreamIterator iter = ReadStreamIterator.enforceShardBoundary(createChannel(), requests.get(0),
        ShardBoundary.Requirement.STRICT, null);
    TestHelper.consumeStreamTest(iter, 0);

    iter = ReadStreamIterator.enforceShardBoundary(createChannel(), requests.get(0),
        ShardBoundary.Requirement.OVERLAPS, null);
    TestHelper.consumeStreamTest(iter, 3);
  }

  @Test
  public void testAllVariantsOverlapsStart() throws IOException, GeneralSecurityException {
    ImmutableList<StreamVariantsRequest> requests =
        ShardUtils.getVariantRequests(PROTOTYPE_VARIANT_REQUEST, 1000000L, "chr7:500:600");

    VariantStreamIterator iter = VariantStreamIterator.enforceShardBoundary(createChannel(), requests.get(0),
        ShardBoundary.Requirement.STRICT, null);
    TestHelper.consumeStreamTest(iter, 0);

    iter = VariantStreamIterator.enforceShardBoundary(createChannel(), requests.get(0),
        ShardBoundary.Requirement.OVERLAPS, null);
    TestHelper.consumeStreamTest(iter, 3);
  }

  @Test
  public void testSomeReadsOverlapsStart() throws IOException, GeneralSecurityException {
    ImmutableList<StreamReadsRequest> requests =
        ShardUtils.getReadRequests(Collections.singletonList(PROTOTYPE_READ_REQUEST), 1000000L, "chr7:499:600");

    ReadStreamIterator iter = ReadStreamIterator.enforceShardBoundary(createChannel(), requests.get(0),
        ShardBoundary.Requirement.STRICT, null);
    TestHelper.consumeStreamTest(iter, 1);

    iter = ReadStreamIterator.enforceShardBoundary(createChannel(), requests.get(0),
        ShardBoundary.Requirement.OVERLAPS, null);
    TestHelper.consumeStreamTest(iter, 3);
  }

  @Test
  public void testSomeVariantsOverlapsStart() throws IOException, GeneralSecurityException {
    ImmutableList<StreamVariantsRequest> requests =
        ShardUtils.getVariantRequests(PROTOTYPE_VARIANT_REQUEST, 1000000L, "chr7:499:600");

    VariantStreamIterator iter = VariantStreamIterator.enforceShardBoundary(createChannel(), requests.get(0),
        ShardBoundary.Requirement.STRICT, null);
    TestHelper.consumeStreamTest(iter, 1);

    iter = VariantStreamIterator.enforceShardBoundary(createChannel(), requests.get(0),
        ShardBoundary.Requirement.OVERLAPS, null);
    TestHelper.consumeStreamTest(iter, 3);
  }

  @Test
  public void testNoReadsOverlapsStart() throws IOException, GeneralSecurityException {
    ImmutableList<StreamReadsRequest> requests =
        ShardUtils.getReadRequests(Collections.singletonList(PROTOTYPE_READ_REQUEST), 1000000L, "chr7:300:600");

    ReadStreamIterator iter = ReadStreamIterator.enforceShardBoundary(createChannel(), requests.get(0),
        ShardBoundary.Requirement.STRICT, null);
    TestHelper.consumeStreamTest(iter, 3);

    iter = ReadStreamIterator.enforceShardBoundary(createChannel(), requests.get(0),
        ShardBoundary.Requirement.OVERLAPS, null);
    TestHelper.consumeStreamTest(iter, 3);
  }

  @Test
  public void testNoVariantsOverlapsStart() throws IOException, GeneralSecurityException {
    ImmutableList<StreamVariantsRequest> requests =
        ShardUtils.getVariantRequests(PROTOTYPE_VARIANT_REQUEST, 1000000L, "chr7:300:600");

    VariantStreamIterator iter = VariantStreamIterator.enforceShardBoundary(createChannel(), requests.get(0),
        ShardBoundary.Requirement.STRICT, null);
    TestHelper.consumeStreamTest(iter, 3);

    iter = VariantStreamIterator.enforceShardBoundary(createChannel(), requests.get(0),
        ShardBoundary.Requirement.OVERLAPS, null);
    TestHelper.consumeStreamTest(iter, 3);
  }

}
