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
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.java6.auth.oauth2.VerificationCodeReceiver;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
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
import com.google.api.client.util.store.DataStoreFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.genomics.Genomics;
import com.google.api.services.genomics.GenomicsScopes;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Charsets;
import com.google.common.base.Optional;
import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import com.google.common.io.Files;

import javax.annotation.Nullable;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.io.Serializable;
import java.io.StringReader;
import java.security.GeneralSecurityException;
import java.util.Collection;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Code required to manufacture instances of a {@link Genomics} stub. Right now, there are 3
 * supported methods of obtaining a stub:
 *
 * <ul>
 *   <li>Using an <a href="https://developers.google.com/api-client-library/python/guide/aaa_apikeys">API key</a></li>
 *   <li>Using <a href="https://developers.google.com/api-client-library/python/guide/aaa_client_secrets">client secrets</a></li>
 *   <li>Using a <a href="https://developers.google.com/accounts/docs/OAuth2ServiceAccount">service account</a></li>
 * </ul>
 */
public class GenomicsFactory {

  private static final int DEFAULT_CONNECT_TIMEOUT = 20000;
  private static final int DEFAULT_READ_TIMEOUT = 20000;
  private static final int DEFAULT_NUMBER_OF_RETRIES = 5;
  
  /**
   * A builder class for {@link GenomicsFactory} objects.
   */
  public static class Builder {
    private static final int DATA_STORE_FACTORY_RETRIES = 3;

    @VisibleForTesting final String applicationName;
    @VisibleForTesting final File userDir;
    @VisibleForTesting DataStoreFactory dataStoreFactory;
    private HttpTransport httpTransport = Utils.getDefaultTransport();
    private int connectTimeout = DEFAULT_CONNECT_TIMEOUT;
    private int readTimeout = DEFAULT_READ_TIMEOUT;
    private int numRetries = DEFAULT_NUMBER_OF_RETRIES;
    private JsonFactory jsonFactory = Utils.getDefaultJsonFactory();
    private Optional<String> rootUrl = Optional.absent();
    private Optional<String> servicePath = Optional.absent();
    private Collection<String> scopes = GenomicsScopes.all();
    private String userName = System.getProperty("user.name");
    private Supplier<? extends VerificationCodeReceiver>
        verificationCodeReceiver = Suppliers.ofInstance(new LocalServerReceiver());

    Builder(String applicationName) throws IOException {
      this.applicationName = applicationName;
      this.userDir = new File(
          System.getProperty("user.home"),
          String.format(".store/%s", applicationName.replace("/", "_")));

      int retries = 0;
      while (dataStoreFactory == null) {
        try {
          dataStoreFactory = makeFileDataStoreFactory();
        } catch (IOException e) {
          // We'll retry the creation up to three times to handle a race condition
          // when multiple workers are on the same machine
          retries++;
          if (retries > DATA_STORE_FACTORY_RETRIES) {
            throw e;
          }
        }
      }
    }

    @VisibleForTesting FileDataStoreFactory makeFileDataStoreFactory() throws IOException {
      return new FileDataStoreFactory(userDir);
    }

    /**
     * Build the {@link GenomicsFactory}.
     *
     * @return The built {@link GenomicsFactory}
     */
    public GenomicsFactory build() {
      return new GenomicsFactory(
          applicationName,
          dataStoreFactory,
          httpTransport,
          connectTimeout,
          readTimeout,
          numRetries,
          jsonFactory,
          scopes,
          userName,
          rootUrl,
          servicePath,
          verificationCodeReceiver,
          userDir);
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
    public Builder setConnectTimeout(int connectTimeout) {
      this.connectTimeout = connectTimeout;
      return this;
    }

    /**
     * @deprecated
     */
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

    /**
     * Set the user name. The default is {@code System.getProperty("user.name")}. Most code will
     * rarely have to call this method.
     *
     * @param userName The user name to use.
     * @return this builder
     */
    public Builder setUserName(String userName) {
      this.userName = userName;
      return this;
    }

    /**
     * Sets the {@link Supplier} of the {@link VerificationCodeReceiver}.
     *
     * @param verificationCodeReceiver The {@code Supplier} of the {@code VerificationCodeReceiver}
     *        to use.
     * @return this builder
     */
    public Builder setVerificationCodeReceiver(
        Supplier<? extends VerificationCodeReceiver> verificationCodeReceiver) {
      this.verificationCodeReceiver = verificationCodeReceiver;
      return this;
    }
  }

  /**
   * Create a new {@link Builder} for {@code GenomicsFactory} objects.
   *
   * @param applicationName The name of this application.
   * @return the new {@code Builder} object.
   * @throws GeneralSecurityException
   * @throws IOException
   */
  public static Builder builder(String applicationName)
      throws GeneralSecurityException, IOException {
    return new Builder(applicationName);
  }

  private final String applicationName;

  private final DataStoreFactory dataStoreFactory;
  private final HttpTransport httpTransport;
  private final int connectTimeout;
  private final int readTimeout;
  private final int numRetries;
  private final JsonFactory jsonFactory;
  private final Optional<String> rootUrl;
  private final Optional<String> servicePath;
  private final Collection<String> scopes;
  private final File userDir;
  private final String userName;
  private final Supplier<? extends VerificationCodeReceiver> verificationCodeReceiver;

  private final AtomicInteger initializedRequestsCount = new AtomicInteger();
  private final AtomicInteger unsuccessfulResponsesCount = new AtomicInteger();
  private final AtomicInteger ioExceptionsCount = new AtomicInteger();

  private GenomicsFactory(
      String applicationName,
      DataStoreFactory dataStoreFactory,
      HttpTransport httpTransport,
      int connectTimeout, 
      int readTimeout,
      int numRetries,
      JsonFactory jsonFactory,
      Collection<String> scopes,
      String userName,
      Optional<String> rootUrl,
      Optional<String> servicePath,
      Supplier<? extends VerificationCodeReceiver> verificationCodeReceiver,
      File userDir) {
    this.applicationName = applicationName;
    this.dataStoreFactory = dataStoreFactory;
    this.httpTransport = httpTransport;
    this.connectTimeout = connectTimeout; 
    this.readTimeout = readTimeout;
    this.numRetries = numRetries;
    this.jsonFactory = jsonFactory;
    this.scopes = scopes;
    this.userName = userName;
    this.rootUrl = rootUrl;
    this.servicePath = servicePath;
    this.verificationCodeReceiver = verificationCodeReceiver;
    this.userDir = userDir;
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

                  @Override public boolean handleResponse(HttpRequest request,
                      HttpResponse response, boolean supportsRetry) throws IOException {
                    unsuccessfulResponsesCount.incrementAndGet();
                    
                    return (null != delegate
                        && delegate.handleResponse(request, response, supportsRetry))
                        || unsuccessfulResponseHandler.handleResponse(
                            request, response, supportsRetry);
                  }
                })
            .setIOExceptionHandler(
                new HttpIOExceptionHandler() {

                  @Nullable private final HttpIOExceptionHandler
                      delegate = request.getIOExceptionHandler();

                  @Override public boolean handleIOException(HttpRequest request,
                      boolean supportsRetry) throws IOException {
                    ioExceptionsCount.incrementAndGet();
                    
                    return (null != delegate
                        && delegate.handleIOException(request, supportsRetry))
                        || ioExceptionHandler.handleIOException(request, supportsRetry);
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

  public DataStoreFactory getDataStoreFactory() {
    return dataStoreFactory;
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
   * @param apiKey The API key of the Google Cloud project to charge requests to.
   * @return The new {@code Genomics} stub
   */
  public Genomics fromApiKey(String apiKey) {
    return fromApiKey(getGenomicsBuilder(), apiKey).build();
  }

  /**
   * Prepare an AbstractGoogleJsonClient.Builder using an API key.
   *
   * @param builder The builder to be prepared.
   * @param apiKey The API key of the Google Cloud project to charge requests to.
   * @return The passed in builder, for easy chaining.
   */
  public <T extends AbstractGoogleJsonClient.Builder> T fromApiKey(T builder, String apiKey) {
    return prepareBuilder(builder, null, new CommonGoogleClientRequestInitializer(apiKey));
  }

  /**
   * Create a {@link Genomics} stub using a {@code client_secrets.json} {@link File}.
   *
   * @param clientSecretsJson {@code client_secrets.json} file.
   * @return The new {@code Genomics} stub
   * @throws IOException
   */
  public Genomics fromClientSecretsFile(File clientSecretsJson) throws IOException {
    return fromClientSecretsFile(getGenomicsBuilder(), clientSecretsJson).build();
  }

  /**
   * Prepare an AbstractGoogleJsonClient.Builder using a {@code client_secrets.json} {@link File}.
   *
   * @param builder The builder to be prepared.
   * @param clientSecretsJson {@code client_secrets.json} file.
   * @return The passed in builder, for easy chaining.
   * @throws IOException
   */
  public <T extends AbstractGoogleJsonClient.Builder> T fromClientSecretsFile(T builder,
      File clientSecretsJson) throws IOException {
    return prepareBuilder(builder, makeCredential(clientSecretsJson), null);
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
    return prepareBuilder(builder, refreshToken(
        new GoogleCredential.Builder()
            .setTransport(httpTransport)
            .setJsonFactory(jsonFactory)
            .setServiceAccountId(serviceAccountId)
            .setServiceAccountScopes(scopes)
            .setServiceAccountPrivateKeyFromP12File(p12File)
            .build()), null);
  }

  private Credential makeCredential(File clientSecretsJson) throws IOException {
    Reader in = null;
    boolean returnNormally = true;
    try {
      GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow
          .Builder(
              httpTransport,
              jsonFactory,
              GoogleClientSecrets.load(jsonFactory, in = new FileReader(clientSecretsJson)),
              scopes)
          .setDataStoreFactory(dataStoreFactory)
          .setAccessType("offline")
          .setApprovalPrompt("force")
          .build();
      return refreshToken(
          new AuthorizationCodeInstalledApp(flow, verificationCodeReceiver.get())
              .authorize(userName));
    } catch (IOException e) {
      returnNormally = false;
      throw e;
    } finally {
      if (null != in) {
        try {
          in.close();
        } catch (IOException e) {
          if (returnNormally) {
            throw e;
          }
        }
      }
    }
  }

  private Credential refreshToken(Credential credential) throws IOException {
    try {
      credential.refreshToken();
      return credential;
    } catch (NullPointerException e) {
      throw new IllegalStateException(
          "Couldn't refresh the OAuth token. Are you using different client secrets? If so, you "
              + "need to first clear the stored credentials by removing the file at "
              + userDir.getPath() + "/StoredCredential",
          e);
    }
  }

  /**
   * Creates offline-friendly auth object using an apiKey.
   *
   * Use this method when you need to store the resulting OfflineAuth for later use.
   * Otherwise, you should create a {@link Genomics} object directly using
   * {@code fromClientSecretsFile}.
   *
   * @param apiKey The API key of the Google Cloud project to charge requests to.
   * @return An OfflineAuth object that can be passed to {@code fromOfflineAuth}
   * @throws IOException
   */
  public OfflineAuth getOfflineAuthFromApiKey(String apiKey)
      throws IOException {
    if (apiKey == null) {
      throw new IllegalArgumentException(
          "An API key must be specified.");
    }

    OfflineAuth offlineAuth = new OfflineAuth();
    offlineAuth.applicationName = applicationName;
    offlineAuth.apiKey = apiKey;

    return offlineAuth;
  }

  /**
   * Creates offline-friendly auth object using a clientSecretsJson file path.
   *
   * Use this method when you need to store the resulting OfflineAuth for later use.
   * Otherwise, you should create a {@link Genomics} object directly using
   * {@code fromClientSecretsFile}.
   *
   * @param clientSecretsFilepath {@code client_secrets.json} file path.
   * @return An OfflineAuth object that can be passed to {@code fromOfflineAuth}
   * @throws IOException
   */
  public OfflineAuth getOfflineAuthFromClientSecretsFile(String clientSecretsFilepath)
      throws IOException {
    if (clientSecretsFilepath == null) {
      throw new IllegalArgumentException(
          "A client secrets file path must be specified.");
    }

    clientSecretsFilepath.replaceFirst("^~", System.getProperty("user.home"));
    File clientSecretsFile = new File(clientSecretsFilepath);
    Credential credential = makeCredential(clientSecretsFile);
    return createOfflineAuth(credential, clientSecretsFilepath);
  }

  /**
   * Creates offline-friendly auth object using a credential object.
   *
   * Use this method when your application has already performed the oauth
   * flow (e.g., Google Cloud Dataflow) and you need to store the resulting
   * OfflineAuth for later use.  
   * 
   * Otherwise, you should create a {@link Genomics} object directly using
   * {@code fromClientSecretsFile}.
   *
   * @param credential
   * @param clientSecretsFilepath {@code client_secrets.json} file path.
   * @return An OfflineAuth object that can be passed to {@code fromOfflineAuth}
   * @throws IOException
   */
  public OfflineAuth getOfflineAuthFromCredential(Credential credential, String clientSecretsFilepath)
      throws IOException {
    if (credential == null) {
      throw new IllegalArgumentException(
          "A credential object must be specified.");
    }
    if (clientSecretsFilepath == null) {
        throw new IllegalArgumentException(
            "A client secrets file path must be specified.");
    }
    return createOfflineAuth(credential, clientSecretsFilepath);
  }
  
  private OfflineAuth createOfflineAuth(Credential credential, String clientSecretsFilepath)
      throws IOException {

    OfflineAuth offlineAuth = new OfflineAuth();
    offlineAuth.applicationName = applicationName;
    
    offlineAuth.accessToken = credential.getAccessToken();
    offlineAuth.refreshToken = credential.getRefreshToken();
    clientSecretsFilepath.replaceFirst("^~", System.getProperty("user.home"));
    File clientSecretsFile = new File(clientSecretsFilepath);
    offlineAuth.clientSecretsString = Files.toString(clientSecretsFile, Charsets.UTF_8);

    return offlineAuth;
  }

  public static class OfflineAuth implements Serializable {
    public String applicationName;
    public String accessToken;
    public String refreshToken;
    public String clientSecretsString;
    public String apiKey;
 
    public GenomicsFactory getDefaultFactory() throws GeneralSecurityException, IOException {
      return GenomicsFactory.builder(applicationName).build();
    }

    /**
     * Create a {@link Genomics} stub.
     *
     * @return The new {@code Genomics} stub
     * @param factory The factory used to generate the Genomics object
     */
    public Genomics getGenomics(GenomicsFactory factory) throws IOException {
      return setupAuthentication(factory, factory.getGenomicsBuilder()).build();
    }

    /**
     * Setup authentication on an AbstractGoogleJsonClient.Builder using the saved credentials.
     *
     * @return The passed in builder, for easy chaining.
     * @param factory The factory used to setup the authentication.
     * @param builder The builder to setup authentication on
     */
    public <T extends AbstractGoogleJsonClient.Builder> T setupAuthentication(
        GenomicsFactory factory, T builder) throws IOException {
      if (clientSecretsString == null) {
        return factory.fromApiKey(builder, apiKey);
      }

      return factory.prepareBuilder(builder, new GoogleCredential.Builder()
          .setTransport(factory.httpTransport)
          .setJsonFactory(factory.jsonFactory)
          .setClientSecrets(GoogleClientSecrets.load(factory.jsonFactory,
              new StringReader(clientSecretsString)))
          .build()
          .setAccessToken(accessToken)
          .setRefreshToken(refreshToken), new CommonGoogleClientRequestInitializer(apiKey));
    }
  }
}