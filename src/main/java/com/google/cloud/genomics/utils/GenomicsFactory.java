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
import com.google.api.client.googleapis.util.Utils;
import com.google.api.client.http.HttpBackOffUnsuccessfulResponseHandler;
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
import com.google.common.base.Optional;
import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.security.GeneralSecurityException;
import java.util.Collection;

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

  /**
   * A builder class for {@link GenomicsFactory} objects.
   */
  public final static class Builder {

    private final String applicationName;
    private int connectTimeout = 20000;
    private final DataStoreFactory dataStoreFactory;
    private HttpTransport httpTransport = Utils.getDefaultTransport();
    private JsonFactory jsonFactory = Utils.getDefaultJsonFactory();
    private int readTimeout = 20000;
    private Optional<String> rootUrl = Optional.absent();
    private Optional<String> servicePath = Optional.absent();
    private Collection<String> scopes = GenomicsScopes.all();
    private HttpUnsuccessfulResponseHandler unsuccessfulResponseHandler =
        new HttpUnsuccessfulResponseHandler() {

          private final HttpUnsuccessfulResponseHandler delegate =
              new HttpBackOffUnsuccessfulResponseHandler(new ExponentialBackOff());

          @Override public boolean handleResponse(HttpRequest request,
              HttpResponse response, boolean supportsRetry) throws IOException {
            int statusCode = response.getStatusCode();
            return 500 <= statusCode
                && statusCode < 600
                && delegate.handleResponse(request, response, supportsRetry);
          }
        };

    private final File userDir;
    private String userName = System.getProperty("user.name");
    private Supplier<? extends VerificationCodeReceiver>
        verificationCodeReceiver = Suppliers.ofInstance(new LocalServerReceiver());

    private Builder(String applicationName) throws IOException {
      this.dataStoreFactory = new FileDataStoreFactory(this.userDir = new File(
          System.getProperty("user.home"),
          String.format(".store/%s", (this.applicationName = applicationName).replace("/", "_"))));
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
          jsonFactory,
          scopes,
          userName,
          readTimeout,
          rootUrl,
          servicePath,
          connectTimeout,
          verificationCodeReceiver,
          unsuccessfulResponseHandler,
          userDir);
    }

    /**
     * Set the connect timeout
     *
     * @param connectTimeout The connect timeout in milliseconds
     * @return this builder
     */
    public Builder setConnectTimeout(int connectTimeout) {
      this.connectTimeout = connectTimeout;
      return this;
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
     * Set the read timeout
     *
     * @param readTimeout The read timeout in milliseconds
     * @return this builder
     */
    public Builder setReadTimeout(int readTimeout) {
      this.readTimeout = readTimeout;
      return this;
    }

    /**
     * The URL of the endpoint to send requests to. The default is
     * {@code https://www.googleapis.com/genomics/v1beta}.
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
     * Set the unsuccessful response handler for this client.
     *
     * @param unsuccessfulResponseHandler the unsuccessful response handler
     * @return this builder
     */
    public Builder setUnsuccessfulResponseHandler(
        HttpUnsuccessfulResponseHandler unsuccessfulResponseHandler) {
      this.unsuccessfulResponseHandler = unsuccessfulResponseHandler;
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

  private final int connectTimeout;
  private final DataStoreFactory dataStoreFactory;
  private final HttpTransport httpTransport;
  private final JsonFactory jsonFactory;
  private final int readTimeout;
  private final Optional<String> rootUrl;
  private final Optional<String> servicePath;
  private final Collection<String> scopes;
  private final File userDir;
  private final HttpUnsuccessfulResponseHandler unsuccessfulResponseHandler;
  private final String userName;
  private final Supplier<? extends VerificationCodeReceiver> verificationCodeReceiver;

  private GenomicsFactory(
      String applicationName,
      DataStoreFactory dataStoreFactory,
      HttpTransport httpTransport,
      JsonFactory jsonFactory,
      Collection<String> scopes,
      String userName,
      int readTimeout,
      Optional<String> rootUrl,
      Optional<String> servicePath,
      int connectTimeout,
      Supplier<? extends VerificationCodeReceiver> verificationCodeReceiver,
      HttpUnsuccessfulResponseHandler unsuccessfulResponseHandler,
      File userDir) {
    this.applicationName = applicationName;
    this.dataStoreFactory = dataStoreFactory;
    this.httpTransport = httpTransport;
    this.jsonFactory = jsonFactory;
    this.scopes = scopes;
    this.userName = userName;
    this.readTimeout = readTimeout;
    this.rootUrl = rootUrl;
    this.servicePath = servicePath;
    this.connectTimeout = connectTimeout;
    this.verificationCodeReceiver = verificationCodeReceiver;
    this.unsuccessfulResponseHandler = unsuccessfulResponseHandler;
    this.userDir = userDir;
  }

  private RequestCountingGenomics create(
      final HttpRequestInitializer delegate,
      GoogleClientRequestInitializer googleClientRequestInitializer) {
    final Genomics.Builder builder = new Genomics
        .Builder(
            httpTransport,
            jsonFactory,
            new HttpRequestInitializer() {
              @Override public void initialize(HttpRequest httpRequest) throws IOException {
                if (null != delegate) {
                  delegate.initialize(httpRequest);
                }
                httpRequest
                    .setConnectTimeout(connectTimeout)
                    .setReadTimeout(readTimeout)
                    .setUnsuccessfulResponseHandler(unsuccessfulResponseHandler);
              }
            })
        .setApplicationName(applicationName)
        .setGoogleClientRequestInitializer(googleClientRequestInitializer);

    if (rootUrl.isPresent()) {
      builder.setRootUrl(rootUrl.get());
    }
    if (servicePath.isPresent()) {
      builder.setServicePath(servicePath.get());
    }
    return RequestCountingGenomics.of(builder.build());
  }

  public DataStoreFactory getDataStoreFactory() {
    return dataStoreFactory;
  }

  /**
   * Create a {@link Genomics} stub using an API key.
   *
   * @param apiKey The API key of the Google Cloud project to charge requests to.
   * @return The new {@code Genomics} stub
   */
  public RequestCountingGenomics fromApiKey(String apiKey) {
    return create(null, new CommonGoogleClientRequestInitializer(apiKey));
  }

  /**
   * Create a {@link Genomics} stub from a {@link Credential}.
   *
   * @param credential A Credential that has already been authorized.
   * @return The new {@code Genomics} stub
   * @throws IOException
   */
  public RequestCountingGenomics fromCredential(Credential credential) throws IOException {
    return create(credential, null);
  }

  /**
   * Create a {@link Genomics} stub using a {@code client_secrets.json} {@link File}.
   *
   * @param clientSecretsJson {@code client_secrets.json} file.
   * @return The new {@code Genomics} stub
   * @throws IOException
   */
  public RequestCountingGenomics fromClientSecretsFile(File clientSecretsJson) throws IOException {
    return fromCredential(makeCredential(clientSecretsJson));
  }

  /**
   * Create an authorized credential using a {@code client_secrets.json} {@link File}.
   * Use this method when you need to store the resulting accessToken for later use.
   * Otherwise, you should create a {@link Genomics} object directly using
   * {@code fromClientSecretsFile}.
   *
   * @param clientSecretsJson {@code client_secrets.json} file.
   * @return An authorized {@link Credential}
   * @throws IOException
   */
  public Credential makeCredential(File clientSecretsJson) throws IOException {
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

  /**
   * Create a new genomics stub from the given service account ID and private key {@link File}.
   *
   * @param serviceAccountId The service account ID (typically an email address)
   * @param p12File The file on disk containing the private key
   * @return The new {@code Genomics} stub
   * @throws GeneralSecurityException
   * @throws IOException
   */
  public RequestCountingGenomics fromServiceAccount(String serviceAccountId, File p12File)
      throws GeneralSecurityException, IOException {
    return fromCredential(
        refreshToken(
            new GoogleCredential.Builder()
                .setTransport(httpTransport)
                .setJsonFactory(jsonFactory)
                .setServiceAccountId(serviceAccountId)
                .setServiceAccountScopes(scopes)
                .setServiceAccountPrivateKeyFromP12File(p12File)
                .build()));
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
}