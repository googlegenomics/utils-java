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

import com.google.cloud.genomics.utils.OfflineAuth;
import com.google.cloud.genomics.utils.ShardBoundary;
import com.google.cloud.genomics.utils.ShardBoundary.Requirement;
import com.google.common.base.Predicate;
import com.google.genomics.v1.Read;
import com.google.genomics.v1.StreamReadsRequest;
import com.google.genomics.v1.StreamReadsResponse;
import com.google.genomics.v1.StreamingReadServiceGrpc;
import com.google.genomics.v1.StreamingReadServiceGrpc.StreamingReadServiceBlockingStub;

import io.grpc.ManagedChannel;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Iterator;
import java.util.List;

/**
 * An iterator for streaming genomic reads via gRPC with shard boundary semantics.
 *
 * This function integrates complex retry logic, which upon encountering a failure
 * will resume the stream at the last known valid start position, skipping over any
 * data that was already returned to the client prior to the failure.
 */
public class ReadStreamIterator
    extends
    GenomicsStreamIterator<StreamReadsRequest, StreamReadsResponse, Read, StreamingReadServiceGrpc.StreamingReadServiceBlockingStub> {

  /**
   * Create a stream iterator that can enforce shard boundary semantics.
   *
   * @param auth The OfflineAuth to use for the request.
   * @param request The request for the shard of data.
   * @param shardBoundary The shard boundary semantics to enforce.
   * @param fields Which fields to include in a partial response or null for all.
   * @throws IOException
   * @throws GeneralSecurityException
   */
  public static ReadStreamIterator enforceShardBoundary(OfflineAuth auth,
      StreamReadsRequest request, Requirement shardBoundary, String fields) throws IOException,
      GeneralSecurityException {
    return ReadStreamIterator.enforceShardBoundary(GenomicsChannel.fromOfflineAuth(auth, fields), request,
        shardBoundary, fields);
  }

  /**
   * Create a stream iterator that can enforce shard boundary semantics.
   *
   * @param channel The ManagedChannel.
   * @param request The request for the shard of data.
   * @param shardBoundary The shard boundary semantics to enforce.
   * @param fields Used to check whether the specified fields would meet the minimum required
   *    fields for the shard boundary predicate, if applicable.
   */
  public static ReadStreamIterator enforceShardBoundary(ManagedChannel channel,
      StreamReadsRequest request, Requirement shardBoundary, String fields) {
    Predicate<Read> shardPredicate =
        (ShardBoundary.Requirement.STRICT == shardBoundary) ? ShardBoundary
            .getStrictReadPredicate(request.getStart(), fields) : null;
    return new ReadStreamIterator(channel, request, shardPredicate);
  }

  /**
   * Create a stream iterator.
   *
   * @param channel The ManagedChannel.
   * @param request The request for the shard of data.
   * @param shardPredicate A predicate used to client-side filter results returned (e.g., enforce a
   *        shard boundary and/or limit to SNPs only) or null for no filtering.
   */
  public ReadStreamIterator(ManagedChannel channel, StreamReadsRequest request,
      Predicate<Read> shardPredicate) {
    super(channel, request, shardPredicate);
  }

  @Override
  StreamingReadServiceBlockingStub createStub(ManagedChannel channel) {
    return StreamingReadServiceGrpc.newBlockingStub(channel);
  }

  @Override
  Iterator<StreamReadsResponse> createIteratorFromStub(StreamReadsRequest request) {
    return stub.streamReads(request);
  }

  @Override
  long getRequestStart(StreamReadsRequest request) {
    return request.getStart();
  }

  @Override
  long getDataItemStart(Read dataItem) {
    return dataItem.getAlignment().getPosition().getPosition();
  }

  @Override
  String getDataItemId(Read dataItem) {
    return dataItem.getId();
  }

  @Override
  StreamReadsRequest getRevisedRequest(long updatedStart) {
    return StreamReadsRequest.newBuilder(originalRequest).setStart(updatedStart).build();
  }

  @Override
  List<Read> getDataList(StreamReadsResponse response) {
    return response.getAlignmentsList();
  }

  @Override
  StreamReadsResponse buildResponse(StreamReadsResponse response, Iterable<Read> dataList) {
    return StreamReadsResponse.newBuilder(response).clearAlignments().addAllAlignments(dataList)
        .build();
  }
}
