package com.google.cloud.genomics.grpc;

import com.google.auth.oauth2.GoogleCredentials;
import io.grpc.Channel;
import io.grpc.ClientInterceptors;
import io.grpc.auth.ClientAuthInterceptor;
import io.grpc.transport.netty.GrpcSslContexts;
import io.grpc.transport.netty.NegotiationType;
import io.grpc.transport.netty.NettyChannelBuilder;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Executors;

/**
 * A convenience class for creating gRPC channels to the Google Genomics API.
 */
public class Channels {
  /**
   * Creates a new gRPC channel to the Google Genomics API, using the application
   * default credentials for auth.
   */
  public static Channel fromDefaultCreds() throws IOException {
    return fromCreds(GoogleCredentials.getApplicationDefault());
  }
  
  /**
   * Creates a new gRPC channel to the Google Genomics API, using the provided
   * credentials for auth.
   */
  public static Channel fromCreds(GoogleCredentials creds) throws IOException {
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
    creds = creds.createScoped(
        Arrays.asList("https://www.googleapis.com/auth/genomics"));
    ClientAuthInterceptor interceptor = new ClientAuthInterceptor(creds,
        Executors.newSingleThreadExecutor());
    return ClientInterceptors.intercept(channel, interceptor);     
  }
}

