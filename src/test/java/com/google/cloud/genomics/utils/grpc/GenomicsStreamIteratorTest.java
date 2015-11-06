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
import io.grpc.inprocess.InProcessChannelBuilder;
import io.grpc.inprocess.InProcessServerBuilder;
import io.grpc.stub.StreamObserver;
import io.grpc.testing.integration.AbstractTransportTest;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Collections;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import com.google.cloud.genomics.utils.ShardBoundary;
import com.google.cloud.genomics.utils.ShardUtils;
import com.google.common.collect.ImmutableList;
import com.google.genomics.v1.StreamReadsRequest;
import com.google.genomics.v1.StreamReadsResponse;
import com.google.genomics.v1.StreamVariantsRequest;
import com.google.genomics.v1.StreamVariantsResponse;
import com.google.genomics.v1.StreamingReadServiceGrpc;
import com.google.genomics.v1.StreamingVariantServiceGrpc;

@RunWith(JUnit4.class)
public class GenomicsStreamIteratorTest extends AbstractTransportTest {
  private static String serverName = "unitTest";
    
  /** Starts the in-process server. 
   * @throws GeneralSecurityException 
   * @throws IOException */
  @BeforeClass
  public static void startServer() throws IOException, GeneralSecurityException {
    startStaticServer(InProcessServerBuilder.forName(serverName)
        .addService(StreamingReadServiceGrpc.bindService(new ReadsUnitServerImpl()))
        .addService(StreamingVariantServiceGrpc.bindService(new VariantsUnitServerImpl())));
  }

  @AfterClass
  public static void stopServer() {
    stopStaticServer();
  }

  @Override
  protected ManagedChannel createChannel() {
    return InProcessChannelBuilder.forName(serverName).build();
  }
  
  private static class ReadsUnitServerImpl implements StreamingReadServiceGrpc.StreamingReadService {
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

  private static class VariantsUnitServerImpl implements StreamingVariantServiceGrpc.StreamingVariantService {
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
  
  @Test
  public void testAllReadsOverlapsStart() throws IOException, GeneralSecurityException {
    ImmutableList<StreamReadsRequest> requests =
        ShardUtils.getReadRequests(Collections.singletonList("fake readgroup set"), "chr7:500:600", 1000000L);

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
        ShardUtils.getVariantRequests("fake variant set", "chr7:500:600", 1000000L);

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
        ShardUtils.getReadRequests(Collections.singletonList("fake readgroup set"), "chr7:499:600", 1000000L);

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
        ShardUtils.getVariantRequests("fake variant set", "chr7:499:600", 1000000L);

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
        ShardUtils.getReadRequests(Collections.singletonList("fake readgroup set"), "chr7:300:600", 1000000L);

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
        ShardUtils.getVariantRequests("fake variant set", "chr7:300:600", 1000000L);

    VariantStreamIterator iter = VariantStreamIterator.enforceShardBoundary(createChannel(), requests.get(0), 
        ShardBoundary.Requirement.STRICT, null);
    TestHelper.consumeStreamTest(iter, 3);

    iter = VariantStreamIterator.enforceShardBoundary(createChannel(), requests.get(0), 
        ShardBoundary.Requirement.OVERLAPS, null);
    TestHelper.consumeStreamTest(iter, 3);
  }

}
