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

import com.google.cloud.genomics.utils.GenomicsFactory;
import com.google.cloud.genomics.utils.ShardBoundary;
import com.google.common.base.Predicate;
import com.google.common.collect.ForwardingIterator;
import com.google.common.collect.Iterables;
import com.google.genomics.v1.StreamVariantsRequest;
import com.google.genomics.v1.StreamVariantsResponse;
import com.google.genomics.v1.StreamingVariantServiceGrpc;
import com.google.genomics.v1.Variant;

/**
 * Class with tools for streaming variants via gRPC.
 * 
 * TODO:
 * - facilitate partial requests https://github.com/googlegenomics/utils-java/issues/48
 */
public class VariantStreamIterator extends ForwardingIterator<StreamVariantsResponse> {
  protected final Predicate<Variant> shardPredicate;
  protected final Iterator<StreamVariantsResponse> delegate;
  protected final GenomicsChannel genomicsChannel;

  /**
   * Create a stream iterator that can enforce shard boundary semantics.
   * 
   * @param request The request for the shard of data.
   * @param auth The OfflineAuth to use for the request.
   * @param shardBoundary The shard boundary semantics to enforce.
   * @param fields Which fields to include in a partial response or null for all.  NOT YET IMPLEMENTED.
   * @throws IOException
   * @throws GeneralSecurityException
   */
  public VariantStreamIterator(StreamVariantsRequest request, GenomicsFactory.OfflineAuth auth,
      ShardBoundary.Requirement shardBoundary, String fields) throws IOException, GeneralSecurityException {
    // TODO: Facilitate shard boundary predicate here by checking for minimum set of fields in
    // partial request.
    shardPredicate = ShardBoundary.Requirement.STRICT == shardBoundary ?
        ShardBoundary.getStrictVariantPredicate(request.getStart()) :
          null;

    genomicsChannel = GenomicsChannel.fromOfflineAuth(auth);
    StreamingVariantServiceGrpc.StreamingVariantServiceBlockingStub variantStub =
        StreamingVariantServiceGrpc.newBlockingStub(genomicsChannel);


    delegate = variantStub.streamVariants(request);
  }

  @Override
  protected Iterator<StreamVariantsResponse> delegate() {
    return delegate;
  }

  /**
   * @see java.util.Iterator#hasNext()
   */
  public boolean hasNext() {
    boolean hasNext;
    try {
      hasNext = delegate.hasNext();
    } catch (Exception e) {
      genomicsChannel.shutdownNow();
      throw e;
    }
    if(!hasNext) {
      genomicsChannel.shutdownNow();      
    }
    return hasNext;
  }

  /**
   * @see java.util.Iterator#next()
   */
  public StreamVariantsResponse next() {
    StreamVariantsResponse response = null;
    try {
      response = delegate.next();
    } catch (Exception e) {
      genomicsChannel.shutdownNow();
      throw e;
    }

    if(null == shardPredicate) {
      return response;
    }
    List<Variant> variants = response.getVariantsList();
    Iterable<Variant> filteredVariants = Iterables.filter(variants, shardPredicate);
    return StreamVariantsResponse.newBuilder(response)
        .clearVariants()
        .addAllVariants(filteredVariants)
        .build();
  }
}
