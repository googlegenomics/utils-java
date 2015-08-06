package com.google.cloud.genomics.utils.grpc;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Iterator;

import com.google.cloud.genomics.utils.GenomicsFactory;
import com.google.common.collect.ForwardingIterator;
import com.google.genomics.v1.StreamReadsRequest;
import com.google.genomics.v1.StreamReadsResponse;
import com.google.genomics.v1.StreamingReadServiceGrpc;

/**
 *  Class with tools for streaming reads via gRPC.
 * 
 * TODO:
 * - facilitate shard boundary requirement https://github.com/googlegenomics/utils-java/issues/30
 * - facilitate partial requests https://github.com/googlegenomics/utils-java/issues/48
 */
public class ReadStreamIterator extends ForwardingIterator<StreamReadsResponse> {

  private Iterator<StreamReadsResponse> delegate;

  /**
   * @param request
   * @param auth
   * @throws IOException
   * @throws GeneralSecurityException
   */
  public ReadStreamIterator(StreamReadsRequest request, GenomicsFactory.OfflineAuth auth) throws IOException, GeneralSecurityException {
    // TODO: Facilitate shard boundary predicate here by checking for minimum set of fields in
    // partial request.

    StreamingReadServiceGrpc.StreamingReadServiceBlockingStub readStub =
        StreamingReadServiceGrpc.newBlockingStub(Channels.fromOfflineAuth(auth));
    
    delegate = readStub.streamReads(request);
  }
  
  @Override
  protected Iterator<StreamReadsResponse> delegate() {
    return delegate;
  }

  /**
   * @see java.util.Iterator#next()
   */
  public StreamReadsResponse next() {
    // TODO: Facilitate shard boundary predicate here by skipping any reads that overlap the
    // start position.
    return delegate.next();
  }
  
}
