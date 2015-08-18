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
import com.google.genomics.v1.Read;
import com.google.genomics.v1.StreamReadsRequest;
import com.google.genomics.v1.StreamReadsResponse;
import com.google.genomics.v1.StreamingReadServiceGrpc;

/**
 * Class with tools for streaming reads via gRPC.
 * 
 * TODO:
 * - facilitate partial requests https://github.com/googlegenomics/utils-java/issues/48
 */
public class ReadStreamIterator extends ForwardingIterator<StreamReadsResponse> {
  protected final Predicate<Read> shardPredicate;
  protected final Iterator<StreamReadsResponse> delegate;
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
  public ReadStreamIterator(StreamReadsRequest request, GenomicsFactory.OfflineAuth auth,
      ShardBoundary.Requirement shardBoundary, String fields) throws IOException, GeneralSecurityException {
    // TODO: Facilitate shard boundary predicate here by checking for minimum set of fields in
    // partial request.
    shardPredicate = ShardBoundary.Requirement.STRICT == shardBoundary ?
        ShardBoundary.getStrictReadPredicate(request.getStart()) :
          null;

    genomicsChannel = GenomicsChannel.fromOfflineAuth(auth);
    StreamingReadServiceGrpc.StreamingReadServiceBlockingStub readStub =
        StreamingReadServiceGrpc.newBlockingStub(genomicsChannel.getChannel());
    
    delegate = readStub.streamReads(request);
  }
  
  @Override
  protected Iterator<StreamReadsResponse> delegate() {
    return delegate;
  }

  /**
   * @see java.util.Iterator#hasNext()
   */
  public boolean hasNext() {
    try {
      return delegate.hasNext();
    } catch (RuntimeException e) {
      genomicsChannel.shutdownNow();
      throw e;
    }
  }

  /**
   * @see java.util.Iterator#next()
   */
  public StreamReadsResponse next() {
    StreamReadsResponse response = null;
    try {
      response = delegate.next();
    } catch (RuntimeException e) {
      genomicsChannel.shutdownNow();
      throw e;
    }
    
    if(null == shardPredicate) {
      return response;
    }
    List<Read> reads = response.getAlignmentsList();
    Iterable<Read> filteredReads = Iterables.filter(reads, shardPredicate);
    return StreamReadsResponse.newBuilder(response)
        .clearAlignments()
        .addAllAlignments(filteredReads)
        .build();
  }
}
