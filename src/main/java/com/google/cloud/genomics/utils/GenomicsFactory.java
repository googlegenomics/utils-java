/*
 * Copyright (C) 2014 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.google.cloud.genomics.utils;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.services.CommonGoogleClientRequestInitializer;
import com.google.api.client.googleapis.services.GoogleClientRequestInitializer;
import com.google.api.client.googleapis.services.json.AbstractGoogleJsonClient;
import com.google.api.client.googleapis.util.Utils;
import com.google.api.client.http.HttpBackOffIOExceptionHandler;
import com.google.api.client.http.HttpBackOffUnsuccessfulResponseHandler;
import com.google.api.client.http.HttpIOExceptionHandler;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.HttpResponse;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.HttpUnsuccessfulResponseHandler;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.util.ExponentialBackOff;
import com.google.api.services.genomics.Genomics;
import com.google.api.services.genomics.GenomicsScopes;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Optional;
import com.google.common.base.Preconditions;

import java.io.File;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Collection;
import java.util.concurrent.atomic.AtomicInteger;

import javax.annotation.Nullable;

/**
 * Code required to manufacture instances of a {@link Genomics} stub.
 *
 * Several authentication mechanisms are supported.  For more detail, see
 * https://developers.google.com/api-client-library/java/google-api-java-client/oauth2
 */
public class GenomicsFactory {

  private static final int DEFAULT_CONNECT_TIMEOUT = 20000;
  private static final int DEFAULT_READ_TIMEOUT = 20000;
  private static final int DEFAULT_NUMBER_OF_RETRIES = 5;
  private static final String DEFAULT_APPLICATION_NAME = "genomics";

  /**
   * A builder class for {@link GenomicsFactory} objects.
   */
  public static class Builder {
    // TODO is application name used for anything other than the credential store path?
    // If not, it should be removed.
    @VisibleForTesting final String applicationName;
    private HttpTransport httpTransport = Utils.getDefaultTransport();
    private int connectTimeout = DEFAULT_CONNECT_TIMEOUT;
    private int readTimeout = DEFAULT_READ_TIMEOUT;
    private int numRetries = DEFAULT_NUMBER_OF_RETRIES;
    private JsonFactory jsonFactory = Utils.getDefaultJsonFactory();
    private Optional<String> rootUrl = Optional.absent();
    private Optional<String> servicePath = Optional.absent();
    private Collection<String> scopes = GenomicsScopes.all();

    Builder(String applicationName) {
      this.applicationName = applicationName;
    }

    /**
     * Build the {@link GenomicsFactory}.
     *
     * @return The built {@link GenomicsFactory}
     */
    public GenomicsFactory build() {
      return new GenomicsFactory(
          applicationName,
          httpTransport,
          connectTimeout,
          readTimeout,
          numRetries,
          jsonFactory,
          scopes,
          rootUrl,
          servicePath);
    }

    /**
     * Sets the {@link HttpTransport} to use. Most code will never need to call this method.
     *
     * @param httpTransport the {@code HttpTransport} to use
     * @return this builder
     */
    public Builder setHttpTransport(HttpTransport httpTransport) {
      this.httpTransport = httpTransport;
      return this;
    }

    /**
     * @deprecated
     */
    @Deprecated
    public Builder setConnectTimeout(int connectTimeout) {
      this.connectTimeout = connectTimeout;
      return this;
    }

    /**
     * @deprecated
     */
    @Deprecated
    public Builder setReadTimeout(int readTimeout) {
      this.readTimeout = readTimeout;
      return this;
    }

    /**
     * The number of times to retry a failed request to the Genomics API.
     *
     * @param numRetries
     * @return this
     */
    public Builder setNumberOfRetries(int numRetries) {
      this.numRetries = numRetries;
      return this;
    }

    /**
     * Sets the {@link JsonFactory} to use. Most code will never need to call this method.
     *
     * @param jsonFactory the {@code JsonFactory} to use
     * @return this builder
     */
    public Builder setJsonFactory(JsonFactory jsonFactory) {
      this.jsonFactory = jsonFactory;
      return this;
    }

    /**
     * The URL of the endpoint to send requests to. The default is
     * {@code https://www.googleapis.com}.
     *
     * @param rootUrl The URL of the endpoint to send requests to
     * @return this builder
     */
    public Builder setRootUrl(String rootUrl) {
      this.rootUrl = Optional.of(rootUrl);
      return this;
    }

    /**
     * Sets the URL-encoded service path of the service.
     * Setting this field is uncommon and should only be used when trying to use a
     * non-Google API provider.
     *
     * @param servicePath The URL-encoded service path of the service.
     * @return this builder
     */
    public Builder setServicePath(String servicePath) {
      this.servicePath = Optional.of(servicePath);
      return this;
    }

    /**
     * The OAuth scopes to attach to outgoing requests. Most code will not have to call this method.
     *
     * @param scopes The OAuth scopes to attach to outgoing requests
     * @return this builder
     */
    public Builder setScopes(Collection<String> scopes) {
      this.scopes = scopes;
      return this;
    }
  }

  /**
   * Create a new {@link Builder} for {@code GenomicsFactory} objects.
   *
   * @return the new {@code Builder} object.
   */
  public static Builder builder() {
    return new Builder(DEFAULT_APPLICATION_NAME);
  }

  /**
   * Create a new {@link Builder} for {@code GenomicsFactory} objects.
   *
   * @param applicationName The name of this application.
   * @return the new {@code Builder} object.
   */
  public static Builder builder(String applicationName) {
    return new Builder(applicationName);
  }

  private final String applicationName;

  private final HttpTransport httpTransport;
  private final int connectTimeout;
  private final int readTimeout;
  private final int numRetries;
  private final JsonFactory jsonFactory;
  private final Optional<String> rootUrl;
  private final Optional<String> servicePath;
  private final Collection<String> scopes;

  private final AtomicInteger initializedRequestsCount = new AtomicInteger();
  private final AtomicInteger unsuccessfulResponsesCount = new AtomicInteger();
  private final AtomicInteger ioExceptionsCount = new AtomicInteger();

  private GenomicsFactory(
      String applicationName,
      HttpTransport httpTransport,
      int connectTimeout,
      int readTimeout,
      int numRetries,
      JsonFactory jsonFactory,
      Collection<String> scopes,
      Optional<String> rootUrl,
      Optional<String> servicePath) {
    this.applicationName = applicationName;
    this.httpTransport = httpTransport;
    this.connectTimeout = connectTimeout;
    this.readTimeout = readTimeout;
    this.numRetries = numRetries;
    this.jsonFactory = jsonFactory;
    this.scopes = scopes;
    this.rootUrl = rootUrl;
    this.servicePath = servicePath;
  }

  private <T extends AbstractGoogleJsonClient.Builder> T prepareBuilder(T builder,
      final HttpRequestInitializer delegate,
      GoogleClientRequestInitializer googleClientRequestInitializer) {
    builder
        .setHttpRequestInitializer(getHttpRequestInitializer(delegate))
        .setApplicationName(applicationName)
        .setGoogleClientRequestInitializer(googleClientRequestInitializer);

    if (rootUrl.isPresent()) {
      builder.setRootUrl(rootUrl.get());
    }
    if (servicePath.isPresent()) {
      builder.setServicePath(servicePath.get());
    }
    return builder;
  }

  private Genomics.Builder getGenomicsBuilder() {
    return new Genomics.Builder(httpTransport, jsonFactory, null);
  }

  private HttpRequestInitializer getHttpRequestInitializer(final HttpRequestInitializer delegate) {
    return new HttpRequestInitializer() {
      @Override public void initialize(final HttpRequest request) throws IOException {
        initializedRequestsCount.incrementAndGet();
        if (null != delegate) {
          delegate.initialize(request);
        }

        final HttpBackOffUnsuccessfulResponseHandler unsuccessfulResponseHandler
            = new HttpBackOffUnsuccessfulResponseHandler(new ExponentialBackOff());
        final HttpIOExceptionHandler ioExceptionHandler
            = new HttpBackOffIOExceptionHandler(new ExponentialBackOff());

        request
            .setConnectTimeout(connectTimeout)
            .setReadTimeout(readTimeout)
            .setNumberOfRetries(numRetries)
            .setUnsuccessfulResponseHandler(
                new HttpUnsuccessfulResponseHandler() {

                  @Nullable private final HttpUnsuccessfulResponseHandler
                      delegate = request.getUnsuccessfulResponseHandler();

                  @Override public boolean handleResponse(HttpRequest req,
                      HttpResponse response, boolean supportsRetry) throws IOException {
                    unsuccessfulResponsesCount.incrementAndGet();

                    return (null != delegate
                        && delegate.handleResponse(req, response, supportsRetry))
                        || unsuccessfulResponseHandler.handleResponse(
                            req, response, supportsRetry);
                  }
                })
            .setIOExceptionHandler(
                new HttpIOExceptionHandler() {

                  @Nullable private final HttpIOExceptionHandler
                      delegate = request.getIOExceptionHandler();

                  @Override public boolean handleIOException(HttpRequest req,
                      boolean supportsRetry) throws IOException {
                    ioExceptionsCount.incrementAndGet();

                    return (null != delegate
                        && delegate.handleIOException(req, supportsRetry))
                        || ioExceptionHandler.handleIOException(req, supportsRetry);
                  }
                });
      }
    };
  }

  public HttpTransport getHttpTransport() {
    return httpTransport;
  }

  public JsonFactory getJsonFactory() {
    return jsonFactory;
  }

  public final int initializedRequestsCount() {
    return initializedRequestsCount.get();
  }

  public final int unsuccessfulResponsesCount() {
    return unsuccessfulResponsesCount.get();
  }

  public final int ioExceptionsCount() {
    return ioExceptionsCount.get();
  }

  /**
   * Create a {@link Genomics} stub using an API key.
   *
   * @param apiKey The API key of the Google Cloud Platform project.
   * @return The new {@code Genomics} stub
   */
  public Genomics fromApiKey(String apiKey) {
    Preconditions.checkNotNull(apiKey);
    return fromApiKey(getGenomicsBuilder(), apiKey).build();
  }

  /**
   * Prepare an AbstractGoogleJsonClient.Builder using an API key.
   *
   * @param builder The builder to be prepared.
   * @param apiKey The API key of the Google Cloud Platform project.
   * @return The passed in builder, for easy chaining.
   */
  public <T extends AbstractGoogleJsonClient.Builder> T fromApiKey(T builder, String apiKey) {
    Preconditions.checkNotNull(builder);
    Preconditions.checkNotNull(apiKey);
    return prepareBuilder(builder, null, new CommonGoogleClientRequestInitializer(apiKey));
  }

  /**
   * Create a {@link Genomics} stub using a {@code client_secrets.json} {@link File}.
   *
   * @param clientSecretsJson {@code client_secrets.json} file.
   * @return The new {@code Genomics} stub
   */
  public Genomics fromClientSecretsFile(File clientSecretsJson) {
    Preconditions.checkNotNull(clientSecretsJson);
    return fromClientSecretsFile(getGenomicsBuilder(), clientSecretsJson).build();
  }

  /**
   * Prepare an AbstractGoogleJsonClient.Builder using a {@code client_secrets.json} {@link File}.
   *
   * @param builder The builder to be prepared.
   * @param clientSecretsJson {@code client_secrets.json} file.
   * @return The passed in builder, for easy chaining.
   */
  public <T extends AbstractGoogleJsonClient.Builder> T fromClientSecretsFile(T builder,
      File clientSecretsJson) {
    Preconditions.checkNotNull(builder);
    Preconditions.checkNotNull(clientSecretsJson);
    return prepareBuilder(builder,
        CredentialFactory.getCredentialFromClientSecrets(clientSecretsJson.getAbsolutePath(),
            applicationName),
        null);
  }

  /**
   * Create a {@link Genomics} stub using a credential.
   *
   * @param credential The credential to be used for requests.
   * @return The new {@code Genomics} stub
   */
  public Genomics fromCredential(Credential credential) {
    Preconditions.checkNotNull(credential);
    return fromCredential(getGenomicsBuilder(), credential).build();
  }

  /**
   * Prepare an AbstractGoogleJsonClient.Builder using a credential.
   *
   * @param builder The builder to be prepared.
   * @param credential The credential to be used for requests.
   * @return The passed in builder, for easy chaining.
   */
  public <T extends AbstractGoogleJsonClient.Builder> T fromCredential(T builder, Credential credential) {
    Preconditions.checkNotNull(builder);
    Preconditions.checkNotNull(credential);
    return prepareBuilder(builder, credential, null);
  }

  /**
   * Create a {@link Genomics} stub using the Application Default Credential.
   *
   * @return The new {@code Genomics} stub
   */
  public Genomics fromApplicationDefaultCredential() {
    return fromCredential(CredentialFactory.getApplicationDefaultCredential());
  }

  /**
   * Prepare an AbstractGoogleJsonClient.Builder using the Application Default Credential.
   *
   * @param builder The builder to be prepared.
   * @return The passed in builder, for easy chaining.
   */
  public <T extends AbstractGoogleJsonClient.Builder> T fromApplicationDefaultCredential(T builder) {
    Preconditions.checkNotNull(builder);
    return fromCredential(builder, CredentialFactory.getApplicationDefaultCredential());
  }

  /**
   * Create a new genomics stub from the given service account ID and private key {@link File}.
   *
   * @param serviceAccountId The service account ID (typically an email address)
   * @param p12File The file on disk containing the private key
   * @return The new {@code Genomics} stub
   * @throws GeneralSecurityException
   * @throws IOException
   */
  public Genomics fromServiceAccount(String serviceAccountId, File p12File)
      throws GeneralSecurityException, IOException {
    Preconditions.checkNotNull(serviceAccountId);
    Preconditions.checkNotNull(p12File);
    return fromServiceAccount(getGenomicsBuilder(), serviceAccountId, p12File).build();
  }

  /**
   * Prepare an AbstractGoogleJsonClient.Builder with the given service account ID
   * and private key {@link File}.
   *
   * @param builder The builder to be prepared.
   * @param serviceAccountId The service account ID (typically an email address)
   * @param p12File The file on disk containing the private key
   * @return The passed in builder, for easy chaining.
   * @throws GeneralSecurityException
   * @throws IOException
   */
  public <T extends AbstractGoogleJsonClient.Builder> T fromServiceAccount(T builder,
      String serviceAccountId, File p12File) throws GeneralSecurityException, IOException {
    Preconditions.checkNotNull(builder);
    GoogleCredential creds = new GoogleCredential.Builder()
        .setTransport(httpTransport)
        .setJsonFactory(jsonFactory)
        .setServiceAccountId(serviceAccountId)
        .setServiceAccountScopes(scopes)
        .setServiceAccountPrivateKeyFromP12File(p12File)
        .build();
    creds.refreshToken();
    return prepareBuilder(builder, creds, null);
  }

  /**
   * Create a new genomics stub from the given OfflineAuth object.
   *
   * @param auth The OfflineAuth
   * @return The new {@code Genomics} stub
   */
  public Genomics fromOfflineAuth(OfflineAuth auth) {
    Preconditions.checkNotNull(auth);
    return fromOfflineAuth(getGenomicsBuilder(), auth).build();
  }

  /**
   * Prepare an AbstractGoogleJsonClient.Builder with the given OfflineAuth object.
   *
   * @param builder The builder to be prepared.
   * @param auth The OfflineAuth
   * @return The passed in builder, for easy chaining.
   */
  public <T extends AbstractGoogleJsonClient.Builder> T fromOfflineAuth(T builder, OfflineAuth auth) {
    Preconditions.checkNotNull(builder);
    Preconditions.checkNotNull(auth);
    if(auth.hasApiKey()) {
      return fromApiKey(builder, auth.getApiKey());
    }
    return fromCredential(builder, auth.getCredential());
  }
}
