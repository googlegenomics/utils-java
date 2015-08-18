package com.google.cloud.genomics.utils.grpc;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.genomics.utils.GenomicsFactory;

import io.grpc.CallOptions;
import io.grpc.Channel;
import io.grpc.ChannelImpl;
import io.grpc.ClientCall;
import io.grpc.ClientInterceptors;
import io.grpc.Metadata;
import io.grpc.MethodDescriptor;
import io.grpc.auth.ClientAuthInterceptor;
import io.grpc.netty.GrpcSslContexts;
import io.grpc.netty.NegotiationType;
import io.grpc.netty.NettyChannelBuilder;
import io.grpc.stub.MetadataUtils;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.SSLException;

/**
 * A convenience class for creating gRPC channels to the Google Genomics API.
 */
public class GenomicsChannel extends Channel {
  private static final String GENOMICS_ENDPOINT = "genomics.googleapis.com";
  private static final String GENOMICS_SCOPE = "https://www.googleapis.com/auth/genomics";
  // TODO: This constant should come from grpc-java.
  private static final String API_KEY_HEADER = "X-Goog-Api-Key";

  // NOTE: Unfortunately we need to keep a handle to both of these since Channel does not expose
  // the shutdown method and the ClientInterceptors do not return the ChannelImpl instance.
  private final ChannelImpl channelImpl;
  private final Channel delegate;
  
  private ChannelImpl getGenomicsChannelImpl() throws SSLException {
    // Java 8's implementation of GCM ciphers is extremely slow. Therefore we disable
    // them here.
    List<String> defaultCiphers =
        GrpcSslContexts.forClient().ciphers(null).build().cipherSuites();
    List<String> performantCiphers = new ArrayList<>();
    for (String cipher : defaultCiphers) {
      if (!cipher.contains("GCM")) {
        performantCiphers.add(cipher);
      }
    }

    return NettyChannelBuilder.forAddress(GENOMICS_ENDPOINT, 443)
        .negotiationType(NegotiationType.TLS)
        .sslContext(GrpcSslContexts.forClient().ciphers(performantCiphers).build())
        .build();
  }
  
  private GenomicsChannel(String apiKey) throws SSLException {
    channelImpl = getGenomicsChannelImpl();
    Metadata.Headers headers = new Metadata.Headers();
    Metadata.Key<String> apiKeyHeaderKey =
        Metadata.Key.of(API_KEY_HEADER, Metadata.ASCII_STRING_MARSHALLER);
    headers.put(apiKeyHeaderKey, apiKey);
    delegate = ClientInterceptors.intercept(channelImpl,
        MetadataUtils.newAttachHeadersInterceptor(headers)); 
  }
  
  private GenomicsChannel(GoogleCredentials creds) throws SSLException {
    channelImpl = getGenomicsChannelImpl();
    creds = creds.createScoped(
        Arrays.asList(GENOMICS_SCOPE));
    ClientAuthInterceptor interceptor = new ClientAuthInterceptor(creds,
        Executors.newSingleThreadExecutor());
    delegate = ClientInterceptors.intercept(channelImpl, interceptor);
  }
  
  @Override
  public <RequestT, ResponseT> ClientCall<RequestT, ResponseT> newCall(
      MethodDescriptor<RequestT, ResponseT> arg0, CallOptions arg1) {
    return delegate.newCall(arg0, arg1);
  }

  /**
   * @see io.grpc.ChannelImpl#shutdownNow()
   */
  public void shutdownNow() {
    channelImpl.shutdownNow();
  }
  
  /**
   * @throws InterruptedException 
   * @see io.grpc.ChannelImpl#shutdown()
   * @see io.grpc.ChannelImpl#awaitTermination(long, TimeUnit)
   */
  public void shutdown(long timeout, TimeUnit unit) throws InterruptedException {
    channelImpl.shutdown().awaitTermination(timeout, unit);
  }

  /**
   * Creates a new gRPC channel to the Google Genomics API, using the application
   * default credentials for auth.
   */
  public static GenomicsChannel fromDefaultCreds() throws IOException {
    return fromCreds(GoogleCredentials.getApplicationDefault());
  }
  
  /**
   * Creates a new gRPC channel to the Google Genomics API, using the provided
   * api key for auth.
   */
  public static GenomicsChannel fromApiKey(String apiKey) throws SSLException {
    return new GenomicsChannel(apiKey);
  }
  
  /**
   * Creates a new gRPC channel to the Google Genomics API, using the provided
   * credentials for auth.
   */
  public static GenomicsChannel fromCreds(GoogleCredentials creds) throws IOException {
    return new GenomicsChannel(creds);
  }
  
  /**
   * Initialize auth for a gRPC channel from OfflineAuth or the application default credentials.
   * 
   * This library works with both the older and newer support for OAuth2 clients.
   * 
   * https://developers.google.com/identity/protocols/application-default-credentials
   * 
   * @param auth An OfflineAuth object.
   * @return The gRPC channel authorized using either the information in the OfflineAuth or application default credentials.
   * @throws IOException
   * @throws GeneralSecurityException
   */
  public static GenomicsChannel fromOfflineAuth(GenomicsFactory.OfflineAuth auth) throws IOException, GeneralSecurityException {
    if(auth.hasUserCredentials()) {
      return fromCreds(auth.getUserCredentials());
// TODO: https://github.com/googlegenomics/utils-java/issues/51      
//    } else if(auth.hasApiKey()) {
//      return new Channels(auth.apiKey);
    }
    // Fall back to Default Credentials if the user did not specify user credentials or an api key.
    return fromDefaultCreds();
  }
}

