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
import java.util.Iterator;
import java.util.List;

import com.google.cloud.genomics.utils.GenomicsFactory.OfflineAuth;
import com.google.cloud.genomics.utils.ShardBoundary;
import com.google.cloud.genomics.utils.ShardBoundary.Requirement;
import com.google.common.base.Predicate;
import com.google.genomics.v1.StreamVariantsRequest;
import com.google.genomics.v1.StreamVariantsResponse;
import com.google.genomics.v1.StreamingVariantServiceGrpc;
import com.google.genomics.v1.StreamingVariantServiceGrpc.StreamingVariantServiceBlockingStub;
import com.google.genomics.v1.Variant;

/**
 * An iterator for streaming genomic variants via gRPC with shard boundary semantics.
 * 
 * Includes complex retry logic to upon failure resume the stream at the last known good start
 * position without returning duplicate data.
 * 
 * TODO: - facilitate partial requests https://github.com/googlegenomics/utils-java/issues/48
 */
public class VariantStreamIterator
    extends
    GenomicsStreamIterator<StreamVariantsRequest, StreamVariantsResponse, Variant, StreamingVariantServiceGrpc.StreamingVariantServiceBlockingStub> {

  /**
   * Create a stream iterator that can enforce shard boundary semantics.
   * 
   * @param request
   * @param auth
   * @param shardBoundary
   * @param fields
   * @throws IOException
   * @throws GeneralSecurityException
   */
  public static VariantStreamIterator enforceShardBoundary(StreamVariantsRequest request,
      OfflineAuth auth, Requirement shardBoundary, String fields) throws IOException,
      GeneralSecurityException {
    Predicate<Variant> shardPredicate =
        (ShardBoundary.Requirement.STRICT == shardBoundary) ? ShardBoundary
            .getStrictVariantPredicate(request.getStart()) : null;
    // TODO: Facilitate shard boundary predicate here by checking for minimum set of fields in
    // partial request.
    return new VariantStreamIterator(request, auth, fields, shardPredicate);
  }

  /**
   * Create a stream iterator.
   * 
   * @param request The request for the shard of data.
   * @param auth The OfflineAuth to use for the request.
   * @param fields Which fields to include in a partial response or null for all. NOT YET
   *        IMPLEMENTED.
   * @param shardPredicate A predicate used to client-side filter results returned (e.g., enforce
   *             a shard boundary and/or limit to SNPs only) or null for no filtering.
   * @throws IOException
   * @throws GeneralSecurityException
   */
  public VariantStreamIterator(StreamVariantsRequest request, OfflineAuth auth, String fields,
      Predicate<Variant> shardPredicate) throws IOException, GeneralSecurityException {
    super(request, auth, fields, shardPredicate);
  }

  @Override
  StreamingVariantServiceBlockingStub createStub(GenomicsChannel genomicsChannel) {
    return StreamingVariantServiceGrpc.newBlockingStub(genomicsChannel);
  }

  @Override
  Iterator<StreamVariantsResponse> createIteratorFromStub(StreamVariantsRequest request) {
    return stub.streamVariants(request);
  }

  @Override
  long getRequestStart(StreamVariantsRequest request) {
    return request.getStart();
  }

  @Override
  long getDataItemStart(Variant dataItem) {
    return dataItem.getStart();
  }

  @Override
  String getDataItemId(Variant dataItem) {
    return dataItem.getId();
  }

  @Override
  StreamVariantsRequest getRevisedRequest(long updatedStart) {
    return StreamVariantsRequest.newBuilder(originalRequest).setStart(updatedStart).build();
  }

  @Override
  List<Variant> getDataList(StreamVariantsResponse response) {
    return response.getVariantsList();
  }

  @Override
  StreamVariantsResponse buildResponse(StreamVariantsResponse response, Iterable<Variant> dataList) {
    return StreamVariantsResponse.newBuilder(response).clearVariants().addAllVariants(dataList)
        .build();
  }
}
