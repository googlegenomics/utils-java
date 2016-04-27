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

import io.grpc.ClientInterceptor;
import io.grpc.ManagedChannel;
import io.grpc.auth.ClientAuthInterceptor;
import io.grpc.netty.GrpcSslContexts;
import io.grpc.netty.NegotiationType;
import io.grpc.netty.NettyChannelBuilder;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Executors;

import javax.net.ssl.SSLException;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.genomics.utils.CredentialFactory;
import com.google.cloud.genomics.utils.OfflineAuth;

/**
 * A convenience class for creating gRPC channels to the Google Genomics API.
 */
public class GenomicsChannel {
  private static final String GENOMICS_ENDPOINT = "genomics.googleapis.com";
  private static final String GENOMICS_SCOPE = "https://www.googleapis.com/auth/genomics";
  // TODO https://github.com/googlegenomics/utils-java/issues/48
  private static final String PARTIAL_RESPONSE_HEADER = "X-Goog-FieldMask";

  private static ManagedChannel getGenomicsManagedChannel(List<ClientInterceptor> interceptors)
      throws SSLException {
    // Java 8's implementation of GCM ciphers is extremely slow. Therefore we disable
    // them here.
    List<String> defaultCiphers = GrpcSslContexts.forClient().ciphers(null).build().cipherSuites();
    List<String> performantCiphers = new ArrayList<>();
    for (String cipher : defaultCiphers) {
      if (!cipher.contains("GCM")) {
        performantCiphers.add(cipher);
      }
    }

    return NettyChannelBuilder.forAddress(GENOMICS_ENDPOINT, 443)
        .negotiationType(NegotiationType.TLS)
        .sslContext(GrpcSslContexts.forClient().ciphers(performantCiphers).build())
        .intercept(interceptors).build();
  }

  /**
   * Create a new gRPC channel to the Google Genomics API, using the provided credentials for auth.
   *
   * @param creds the credential
   * @return the ManagedChannel
   * @throws SSLException
   */
  public static ManagedChannel fromCreds(GoogleCredentials creds) throws SSLException {
    ClientInterceptor interceptor =
        new ClientAuthInterceptor(creds.createScoped(Arrays.asList(GENOMICS_SCOPE)),
            Executors.newSingleThreadExecutor());
    // TODO https://github.com/googlegenomics/utils-java/issues/48
    return getGenomicsManagedChannel(Collections.singletonList(interceptor));
  }

  /**
   * Create a new gRPC channel to the Google Genomics API, using the application default credentials
   * for auth.
   *
   * @return the ManagedChannel
   * @throws SSLException
   * @throws IOException
   */
  public static ManagedChannel fromDefaultCreds() throws SSLException, IOException {
    return fromCreds(CredentialFactory.getApplicationDefaultCredentials());
  }

  /**
   * Create a new gRPC channel to the Google Genomics API, using OfflineAuth or the application
   * default credentials.
   *
   * This library works with both the older and newer support for OAuth2 clients.
   *
   * https://developers.google.com/identity/protocols/application-default-credentials
   *
   * @param auth the OfflineAuth object
   * @return the ManagedChannel
   * @throws IOException
   * @throws GeneralSecurityException
   */
  public static ManagedChannel fromOfflineAuth(OfflineAuth auth)
      throws IOException, GeneralSecurityException {
    return fromCreds(auth.getCredentials());
  }
}
