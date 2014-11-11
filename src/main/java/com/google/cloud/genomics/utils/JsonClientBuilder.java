/*
 * Copyright (C) 2014 Google Inc.
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
package com.google.cloud.genomics.utils;

import com.google.api.client.auth.oauth2.AuthorizationCodeFlow;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.java6.auth.oauth2.VerificationCodeReceiver;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.services.CommonGoogleClientRequestInitializer;
import com.google.api.client.googleapis.services.json.AbstractGoogleJsonClient;
import com.google.api.client.googleapis.util.Utils;
import com.google.api.client.http.HttpBackOffIOExceptionHandler;
import com.google.api.client.http.HttpBackOffUnsuccessfulResponseHandler;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.util.BackOff;
import com.google.api.client.util.ExponentialBackOff;
import com.google.api.client.util.SecurityUtils;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.common.base.Optional;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.security.GeneralSecurityException;
import java.security.PrivateKey;
import java.util.Arrays;
import java.util.Collection;

/**
 * A simplified builder for creating instances of {@link AbstractGoogleJsonClient} objects.
 *
 * Example usages:
 *
 * <pre>
 * {@code
 *
 * Genomics fromApiKey = JsonClientBuilder.create("example")
 *     .setApiKey(MY_API_KEY)
 *     .build(GenomicsImplementation.INSTANCE);
 *
 * Genomics fromClientSecrets = JsonClientBuilder.create("example")
 *     .setClientSecrets(new File("/path/to/client_secrets.json"))
 *     .setScopes(GenomicsScopes.GENOMICS)
 *     .build(GenomicsImplementation.INSTANCE);
 *
 * Genomics fromServiceAccount = JsonClientBuilder.create("example")
 *     .setServiceAccount(
 *         "myserviceaccount@developer.gserviceaccount.com",
 *         new File("/path/to/service_account_private_key.p12"))
 *     .setServiceAccountScopes(GenomicsScopes.GENOMICS)
 *     .build(GenomicsImplementation.INSTANCE);
 * }
 * </pre>
 */
public final class JsonClientBuilder {

  public static final class ClientSecrets {

    private final String applicationName;
    private final GoogleClientSecrets clientSecrets;
    private final JsonFactory jsonFactory;
    private final HttpTransport transport;
    private VerificationCodeReceiver verificationCodeReceiver = new LocalServerReceiver();

    ClientSecrets(
        String applicationName,
        HttpTransport transport,
        JsonFactory jsonFactory,
        GoogleClientSecrets clientSecrets) {
      this.applicationName = applicationName;
      this.transport = transport;
      this.jsonFactory = jsonFactory;
      this.clientSecrets = clientSecrets;
    }

    /** Set the scopes */
    public JsonClientBuilder setScopes(Collection<String> scopes) throws IOException {
      AuthorizationCodeFlow authorizationCodeFlow = new GoogleAuthorizationCodeFlow
          .Builder(transport, jsonFactory, clientSecrets, scopes)
          .setDataStoreFactory(new FileDataStoreFactory(new File(
              System.getProperty("user.home"),
              String.format(".store/%s", applicationName.replace("/", "_")))))
          .build();
      return new JsonClientBuilder(
          applicationName,
          transport,
          jsonFactory,
          new AuthorizationCodeInstalledApp(authorizationCodeFlow, verificationCodeReceiver)
              .authorize(System.getProperty("user.name")));
    }

    /** Set the scopes */
    public JsonClientBuilder setScopes(String... scopes) throws IOException {
      return setScopes(Arrays.asList(scopes));
    }

    /** Optionally set the {@link VerificationCodeReceiver} */
    public ClientSecrets setVerificationCodeReceiver(
        VerificationCodeReceiver verificationCodeReceiver) {
      this.verificationCodeReceiver = verificationCodeReceiver;
      return this;
    }
  }

  private class DelegatingInitializer extends Initializer {

    @Override final void initialize(AbstractGoogleJsonClient.Builder builder) {
      initializer.initialize(builder);
      initializeFurther(builder);
    }

    @Override public final void initialize(HttpRequest request) throws IOException {
      initializer.initialize(request);
      initializeFurther(request);
    }

    void initializeFurther(AbstractGoogleJsonClient.Builder builder) {}

    void initializeFurther(HttpRequest request) {}
  }

  /**
   * The client specific building logic for use with {@link #build}.
   *
   * @param <C> The type of {@link AbstractGoogleJsonClient} to build
   * @param <B> The
   *        {@link com.google.api.client.googleapis.services.json.AbstractGoogleJsonClient.Builder}
   *        to use for building.
   */
  public interface Implementation<
      C extends AbstractGoogleJsonClient,
      B extends AbstractGoogleJsonClient.Builder> {

    /** Build the builder */
    C build(B builder);

    /** Instantiate the builder */
    B newBuilder(
        HttpTransport transport,
        JsonFactory jsonFactory,
        HttpRequestInitializer requestInitializer);
  }

  public static final class Initial {

    private static GoogleClientSecrets loadClientSecrets(JsonFactory jsonFactory, File file)
        throws IOException {
      Reader in = null;
      try {
        return GoogleClientSecrets.load(jsonFactory, in = new FileReader(file));
      } finally {
        if (null != in) {
          in.close();
        }
      }
    }

    private static PrivateKey loadPrivateKey(File file)
        throws GeneralSecurityException, IOException {
      InputStream in = null;
      try {
        return SecurityUtils.loadPrivateKeyFromKeyStore(
            SecurityUtils.getPkcs12KeyStore(),
            in = new FileInputStream(file),
            "notasecret",
            "privatekey",
            "notasecret");
      } finally {
        if (null != in) {
          in.close();
        }
      }
    }

    private final String applicationName;
    private JsonFactory jsonFactory = Utils.getDefaultJsonFactory();
    private HttpTransport transport = Utils.getDefaultTransport();

    Initial(String applicationName) {
      this.applicationName = applicationName;
    }

    /** Set the API key to use for unauthenticated access */
    public JsonClientBuilder setApiKey(final String apiKey) {
      return new JsonClientBuilder(applicationName, transport, jsonFactory,
          new Initializer() {
            @Override void initialize(AbstractGoogleJsonClient.Builder builder) {
              builder.setGoogleClientRequestInitializer(
                  new CommonGoogleClientRequestInitializer(apiKey));
            }
          });
    }

    /** Set the client secrets */
    public ClientSecrets setClientSecrets(File clientSecretsFile) throws IOException {
      return setClientSecrets(loadClientSecrets(jsonFactory, clientSecretsFile));
    }

    /** Set the client secrets */
    public ClientSecrets setClientSecrets(GoogleClientSecrets clientSecrets) {
      return new ClientSecrets(applicationName, transport, jsonFactory, clientSecrets);
    }

    /** Set the {@link Credential} */
    public JsonClientBuilder setCredential(Credential credential) {
      return new JsonClientBuilder(applicationName, transport, jsonFactory, credential);
    }

    /** Optionally set the {@link JsonFactory} */
    public Initial setJsonFactory(JsonFactory jsonFactory) {
      this.jsonFactory = jsonFactory;
      return this;
    }

    /** Set the service account */
    public ServiceAccount setServiceAccount(String serviceAccountId, File p12File)
        throws GeneralSecurityException, IOException {
      return setServiceAccount(serviceAccountId, loadPrivateKey(p12File));
    }

    /** Set the service account */
    public ServiceAccount setServiceAccount(String serviceAccountId, PrivateKey privateKey) {
      return new ServiceAccount(
          applicationName,
          transport,
          jsonFactory,
          serviceAccountId,
          privateKey);
    }

    /** Optionally set the {@link HttpTransport} */
    public Initial setTransport(HttpTransport transport) {
      this.transport = transport;
      return this;
    }
  }

  private static class Initializer implements HttpRequestInitializer {

    private static final BackOff DEFAULT_BACK_OFF = new ExponentialBackOff();

    void initialize(AbstractGoogleJsonClient.Builder builder) {}

    final void initialize(AbstractGoogleJsonClient.Builder builder, String applicationName) {
      initialize(builder.setApplicationName(applicationName));
    }

    @Override public void initialize(HttpRequest request) throws IOException {
      request
          .setIOExceptionHandler(
              new HttpBackOffIOExceptionHandler(DEFAULT_BACK_OFF))
          .setUnsuccessfulResponseHandler(
              new HttpBackOffUnsuccessfulResponseHandler(DEFAULT_BACK_OFF));
    }
  }

  public static final class ServiceAccount {

    private final String applicationName;
    private final JsonFactory jsonFactory;
    private final String serviceAccountId;
    private final PrivateKey serviceAccountPrivateKey;
    private Optional<String> serviceAccountUser = Optional.absent();
    private final HttpTransport transport;

    ServiceAccount(
        String applicationName,
        HttpTransport transport,
        JsonFactory jsonFactory,
        String serviceAccountId,
        PrivateKey serviceAccountPrivateKey) {
      this.applicationName = applicationName;
      this.transport = transport;
      this.jsonFactory = jsonFactory;
      this.serviceAccountId = serviceAccountId;
      this.serviceAccountPrivateKey = serviceAccountPrivateKey;
    }

    /** Set the scopes for the service account to use */
    public JsonClientBuilder setServiceAccountScopes(Collection<String> serviceAccountScopes) {
      GoogleCredential.Builder credential = new GoogleCredential.Builder()
          .setTransport(transport)
          .setJsonFactory(jsonFactory)
          .setServiceAccountId(serviceAccountId)
          .setServiceAccountPrivateKey(serviceAccountPrivateKey)
          .setServiceAccountScopes(serviceAccountScopes);
      if (serviceAccountUser.isPresent()) {
        credential.setServiceAccountUser(serviceAccountUser.get());
      }
      return new JsonClientBuilder(applicationName, transport, jsonFactory, credential.build());
    }

    /** Set the scopes for the service account to use */
    public JsonClientBuilder setServiceAccountScopes(String... serviceAccountScopes) {
      return setServiceAccountScopes(Arrays.asList(serviceAccountScopes));
    }

    /** Optionally set the user to impersonate */
    public ServiceAccount setServiceAccountUser(String serviceAccountUser) {
      this.serviceAccountUser = Optional.of(serviceAccountUser);
      return this;
    }
  }

  /** Start here to construct the builder by supplying the application name */
  public static Initial create(String applicationName) {
    return new Initial(applicationName);
  }

  private final String applicationName;
  private final Initializer initializer;
  private final JsonFactory jsonFactory;
  private final HttpTransport transport;

  JsonClientBuilder(
      String applicationName,
      HttpTransport transport,
      JsonFactory jsonFactory,
      final Credential credential) {
    this(applicationName, transport, jsonFactory,
        new Initializer() {
          @Override public void initialize(HttpRequest request) throws IOException {
            credential.initialize(request);
          }
        });
  }

  JsonClientBuilder(
      String applicationName,
      HttpTransport transport,
      JsonFactory jsonFactory,
      Initializer initializer) {
    this.applicationName = applicationName;
    this.transport = transport;
    this.jsonFactory = jsonFactory;
    this.initializer = initializer;
  }

  /** Build the client */
  public <C extends AbstractGoogleJsonClient, B extends AbstractGoogleJsonClient.Builder> C
      build(Implementation<? extends C, B> implementation) {
    B builder = implementation.newBuilder(transport, jsonFactory, initializer);
    initializer.initialize(builder, applicationName);
    return implementation.build(builder);
  }

  /** Set the root URL */
  public JsonClientBuilder setRootUrl(final String rootUrl) {
    return substituteInitializer(
        new DelegatingInitializer() {
          @Override void initializeFurther(AbstractGoogleJsonClient.Builder builder) {
            builder.setRootUrl(rootUrl);
          }
        });
  }

  /** Set the service path */
  public JsonClientBuilder setServicePath(final String rootUrl) {
    return substituteInitializer(
        new DelegatingInitializer() {
          @Override void initializeFurther(AbstractGoogleJsonClient.Builder builder) {
            builder.setServicePath(rootUrl);
          }
        });
  }

  /** Set the connect and read timeouts */
  public JsonClientBuilder setTimeouts(final int connectTimeout, final int readTimeout) {
    return substituteInitializer(
        new DelegatingInitializer() {
          @Override void initializeFurther(HttpRequest request) {
            request.setConnectTimeout(connectTimeout).setReadTimeout(readTimeout);
          }
        });
  }

  private JsonClientBuilder substituteInitializer(Initializer initializer) {
    return new JsonClientBuilder(applicationName, transport, jsonFactory, initializer);
  }
}