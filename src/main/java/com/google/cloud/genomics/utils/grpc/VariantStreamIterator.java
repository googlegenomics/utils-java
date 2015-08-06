package com.google.cloud.genomics.utils.grpc;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Iterator;

import com.google.cloud.genomics.utils.GenomicsFactory;
import com.google.common.collect.ForwardingIterator;
import com.google.genomics.v1.StreamVariantsRequest;
import com.google.genomics.v1.StreamVariantsResponse;
import com.google.genomics.v1.StreamingVariantServiceGrpc;

/**
 *  Class with tools for streaming variants via gRPC.
 * 
 * TODO:
 * - facilitate shard boundary requirement https://github.com/googlegenomics/utils-java/issues/30
 * - facilitate partial requests https://github.com/googlegenomics/utils-java/issues/48
 */
public class VariantStreamIterator extends ForwardingIterator<StreamVariantsResponse> {

  private Iterator<StreamVariantsResponse> delegate;

  /**
   * @param request
   * @param auth
   * @throws IOException
   * @throws GeneralSecurityException
   */
  public VariantStreamIterator(StreamVariantsRequest request, GenomicsFactory.OfflineAuth auth) throws IOException, GeneralSecurityException {
    // TODO: Facilitate shard boundary predicate here by checking for minimum set of fields in
    // partial request.
    
    StreamingVariantServiceGrpc.StreamingVariantServiceBlockingStub variantStub =
        StreamingVariantServiceGrpc.newBlockingStub(Channels.fromOfflineAuth(auth));
    
    delegate = variantStub.streamVariants(request);
  }
  
  @Override
  protected Iterator<StreamVariantsResponse> delegate() {
    return delegate;
  }

  /**
   * @see java.util.Iterator#next()
   */
  public StreamVariantsResponse next() {
    // TODO: Facilitate shard boundary predicate here by skipping any variants that overlap the
    // start position.
    return delegate.next();
  }
  
}
