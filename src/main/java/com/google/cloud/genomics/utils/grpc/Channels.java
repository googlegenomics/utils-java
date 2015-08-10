package com.google.cloud.genomics.utils.grpc;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.genomics.utils.GenomicsFactory;

import io.grpc.Channel;
import io.grpc.ClientInterceptors;
import io.grpc.Metadata;
import io.grpc.auth.ClientAuthInterceptor;
import io.grpc.stub.MetadataUtils;
import io.grpc.transport.netty.GrpcSslContexts;
import io.grpc.transport.netty.NegotiationType;
import io.grpc.transport.netty.NettyChannelBuilder;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Executors;

import javax.net.ssl.SSLException;

/**
 * A convenience class for creating gRPC channels to the Google Genomics API.
 */
public class Channels {
  // TODO: This constant should come from grpc-java.
  private static final String API_KEY_HEADER = "X-Goog-Api-Key";

  /**
   * Creates a new gRPC channel to the Google Genomics API, using the application
   * default credentials for auth.
   */
  public static Channel fromDefaultCreds() throws IOException {
    return fromCreds(GoogleCredentials.getApplicationDefault());
  }
  
  /**
   * Creates a new gRPC channel to the Google Genomics API, using the provided
   * api key for auth.
   */
  public static Channel fromApiKey(String apiKey) throws SSLException {
    Metadata.Headers headers = new Metadata.Headers();
    Metadata.Key<String> apiKeyHeaderKey =
        Metadata.Key.of(API_KEY_HEADER, Metadata.ASCII_STRING_MARSHALLER);
    headers.put(apiKeyHeaderKey, apiKey);
    return ClientInterceptors.intercept(getGenomicsChannel(),
        MetadataUtils.newAttachHeadersInterceptor(headers));
  }
  
  /**
   * Creates a new gRPC channel to the Google Genomics API, using the provided
   * credentials for auth.
   */
  public static Channel fromCreds(GoogleCredentials creds) throws IOException {
    creds = creds.createScoped(
        Arrays.asList("https://www.googleapis.com/auth/genomics"));
    ClientAuthInterceptor interceptor = new ClientAuthInterceptor(creds,
        Executors.newSingleThreadExecutor());
    return ClientInterceptors.intercept(getGenomicsChannel(), interceptor);     
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
  public static Channel fromOfflineAuth(GenomicsFactory.OfflineAuth auth) throws IOException, GeneralSecurityException {
    if(auth.hasUserCredentials()) {
      return fromCreds(auth.getUserCredentials());
// TODO: https://github.com/googlegenomics/utils-java/issues/51      
//    } else if(auth.hasApiKey()) {
//      return fromApiKey(auth.apiKey);
    }
    // Fall back to Default Credentials if the user did not specify user credentials or an api key.
    return fromDefaultCreds();
  }
  
  private static Channel getGenomicsChannel() throws SSLException {
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
    
    Channel channel = NettyChannelBuilder.forAddress("genomics.googleapis.com", 443)
        .negotiationType(NegotiationType.TLS)
        .streamWindowSize(1000000)
        .sslContext(GrpcSslContexts.forClient().ciphers(performantCiphers).build())
        .build();
    return channel;
  }
}

