package com.google.cloud.genomics.utils;

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

public final class JsonClientFactory {

  public static final class Builder {

    private static GoogleClientSecrets loadClientSecrets(
        JsonFactory jsonFactory,
        File clientSecretsFile)
        throws IOException {
      Reader in = null;
      try {
        return GoogleClientSecrets.load(jsonFactory, in = new FileReader(clientSecretsFile));
      } finally {
        if (null != in) {
          in.close();
        }
      }
    }

    private static PrivateKey loadPrivateKey(File p12File)
        throws GeneralSecurityException, IOException {
      InputStream in = null;
      try {
        return SecurityUtils.loadPrivateKeyFromKeyStore(
            SecurityUtils.getPkcs12KeyStore(),
            in = new FileInputStream(p12File),
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

    Builder(String applicationName) {
      this.applicationName = applicationName;
    }

    public JsonClientFactory fromApiKey(final String apiKey) {
      return new JsonClientFactory(applicationName, transport, jsonFactory,
          new Initializer() {
            @Override void initialize(AbstractGoogleJsonClient.Builder builder) {
              builder.setGoogleClientRequestInitializer(
                  new CommonGoogleClientRequestInitializer(apiKey));
            }
          });
    }

    public JsonClientFactory fromClientSecrets(
        File clientSecretsFile,
        Collection<String> scopes)
        throws IOException {
      return fromClientSecrets(
          clientSecretsFile,
          new LocalServerReceiver(),
          scopes);
    }

    public JsonClientFactory fromClientSecrets(
        File clientSecretsFile,
        String... scopes)
        throws IOException {
      return fromClientSecrets(
          clientSecretsFile,
          Arrays.asList(scopes));
    }

    public JsonClientFactory fromClientSecrets(
        File clientSecretsFile,
        VerificationCodeReceiver verificationCodeReceiver,
        Collection<String> scopes)
        throws IOException {
      return fromClientSecrets(
          loadClientSecrets(jsonFactory, clientSecretsFile),
          verificationCodeReceiver,
          scopes);
    }

    public JsonClientFactory fromClientSecrets(
        File clientSecretsFile,
        VerificationCodeReceiver verificationCodeReceiver,
        String... scopes)
        throws IOException {
      return fromClientSecrets(
          clientSecretsFile,
          verificationCodeReceiver,
          Arrays.asList(scopes));
    }

    public JsonClientFactory fromClientSecrets(
        GoogleClientSecrets clientSecrets,
        Collection<String> scopes)
        throws IOException {
      return fromClientSecrets(
          clientSecrets,
          new LocalServerReceiver(),
          scopes);
    }

    public JsonClientFactory fromClientSecrets(
        GoogleClientSecrets clientSecrets,
        String... scopes) throws IOException {
      return fromClientSecrets(clientSecrets, Arrays.asList(scopes));
    }

    public JsonClientFactory fromClientSecrets(
        GoogleClientSecrets clientSecrets,
        VerificationCodeReceiver verificationCodeReceiver,
        Collection<String> scopes)
        throws IOException {
      return fromCredential(new AuthorizationCodeInstalledApp(new GoogleAuthorizationCodeFlow.
          Builder(transport, jsonFactory, clientSecrets, scopes).setDataStoreFactory(new
          FileDataStoreFactory(new File(System.getProperty("user.home"), String.format(".store/%s",
          applicationName.replace("/", "_"))))).build(), verificationCodeReceiver).authorize(System.
          getProperty("user.name")));
    }

    public JsonClientFactory fromClientSecrets(
        GoogleClientSecrets clientSecrets,
        VerificationCodeReceiver verificationCodeReceiver,
        String... scopes)
        throws IOException {
      return fromClientSecrets(
          clientSecrets,
          verificationCodeReceiver,
          Arrays.asList(scopes));
    }

    public JsonClientFactory fromCredential(final Credential credential) {
      return new JsonClientFactory(applicationName, transport, jsonFactory,
          new Initializer() {
            @Override public void initialize(HttpRequest request) throws IOException {
              credential.initialize(request);
            }
          });
    }

    public JsonClientFactory fromServiceAccount(
        String serviceAccountId,
        File serviceAccountP12File,
        Collection<String> serviceAccountScopes)
        throws GeneralSecurityException, IOException {
      return fromServiceAccount(
          serviceAccountId,
          loadPrivateKey(serviceAccountP12File),
          Optional.<String>absent(),
          serviceAccountScopes);
    }

    public JsonClientFactory fromServiceAccount(
        String serviceAccountId,
        File serviceAccountP12File,
        String... serviceAccountScopes)
        throws GeneralSecurityException, IOException {
      return fromServiceAccount(
          serviceAccountId,
          serviceAccountP12File,
          Arrays.asList(serviceAccountScopes));
    }

    public JsonClientFactory fromServiceAccount(
        String serviceAccountId,
        File serviceAccountP12File,
        String serviceAccountUser,
        Collection<String> serviceAccountScopes)
        throws GeneralSecurityException, IOException {
      return fromServiceAccount(
          serviceAccountId,
          loadPrivateKey(serviceAccountP12File),
          Optional.of(serviceAccountUser),
          serviceAccountScopes);
    }

    public JsonClientFactory fromServiceAccount(
        String serviceAccountId,
        File serviceAccountP12File,
        String serviceAccountUser,
        String... serviceAccountScopes)
        throws GeneralSecurityException, IOException {
      return fromServiceAccount(
          serviceAccountId,
          serviceAccountP12File,
          serviceAccountUser,
          Arrays.asList(serviceAccountScopes));
    }

    public JsonClientFactory fromServiceAccount(
        String serviceAccountId,
        PrivateKey serviceAccountPrivateKey,
        Collection<String> serviceAccountScopes) {
      return fromServiceAccount(
          serviceAccountId,
          serviceAccountPrivateKey,
          Optional.<String>absent(),
          serviceAccountScopes);
    }

    private JsonClientFactory fromServiceAccount(
        String serviceAccountId,
        PrivateKey serviceAccountPrivateKey,
        Optional<String> serviceAccountUser,
        Collection<String> serviceAccountScopes) {
      GoogleCredential.Builder builder = new GoogleCredential.Builder()
          .setTransport(transport).setJsonFactory(jsonFactory)
          .setServiceAccountId(serviceAccountId)
          .setServiceAccountPrivateKey(serviceAccountPrivateKey)
          .setServiceAccountScopes(serviceAccountScopes);
      if (serviceAccountUser.isPresent()) {
        builder.setServiceAccountUser(serviceAccountUser.get());
      }
      return fromCredential(builder.build());
    }

    public JsonClientFactory fromServiceAccount(
        String serviceAccountId,
        PrivateKey serviceAccountPrivateKey,
        String... serviceAccountScopes) {
      return fromServiceAccount(
          serviceAccountId,
          serviceAccountPrivateKey,
          Arrays.asList(serviceAccountScopes));
    }

    public JsonClientFactory fromServiceAccount(
        String serviceAccountId,
        PrivateKey serviceAccountPrivateKey,
        String serviceAccountUser,
        Collection<String> serviceAccountScopes) {
      return fromServiceAccount(
          serviceAccountId,
          serviceAccountPrivateKey,
          Optional.of(serviceAccountUser),
          serviceAccountScopes);
    }

    public JsonClientFactory fromServiceAccount(
        String serviceAccountId,
        PrivateKey serviceAccountPrivateKey,
        String serviceAccountUser,
        String... serviceAccountScopes) {
      return fromServiceAccount(
          serviceAccountId,
          serviceAccountPrivateKey,
          serviceAccountUser,
          Arrays.asList(serviceAccountScopes));
    }

    public Builder setJsonFactory(JsonFactory jsonFactory) {
      this.jsonFactory = jsonFactory;
      return this;
    }

    public Builder setTransport(HttpTransport transport) {
      this.transport = transport;
      return this;
    }
  }

  public interface Implementation<
      C extends AbstractGoogleJsonClient,
      B extends AbstractGoogleJsonClient.Builder> {

    C build(B builder);

    B newBuilder(
        HttpTransport transport,
        JsonFactory jsonFactory,
        HttpRequestInitializer requestInitializer);
  }

  private static class Initializer implements HttpRequestInitializer {

    private static final BackOff DEFAULT_BACK_OFF = new ExponentialBackOff();

    void initialize(AbstractGoogleJsonClient.Builder builder) {}

    final void initialize(AbstractGoogleJsonClient.Builder builder, String applicationName) {
      initialize(builder.setApplicationName(applicationName));
    }

    @Override public void initialize(HttpRequest request) throws IOException {
      request
          .setUnsuccessfulResponseHandler(
              new HttpBackOffUnsuccessfulResponseHandler(DEFAULT_BACK_OFF))
          .setIOExceptionHandler(
              new HttpBackOffIOExceptionHandler(DEFAULT_BACK_OFF));
    }
  }

  public static Builder builder(String applicationName) {
    return new Builder(applicationName);
  }

  private final String applicationName;
  private final Initializer initializer;
  private final JsonFactory jsonFactory;
  private final HttpTransport transport;

  private JsonClientFactory(
      String applicationName,
      HttpTransport transport,
      JsonFactory jsonFactory,
      Initializer initializer) {
    this.applicationName = applicationName;
    this.transport = transport;
    this.jsonFactory = jsonFactory;
    this.initializer = initializer;
  }

  public <C extends AbstractGoogleJsonClient, B extends AbstractGoogleJsonClient.Builder>
      C createClient(Implementation<? extends C, B> implementation) {
    B builder = implementation.newBuilder(transport, jsonFactory, initializer);
    initializer.initialize(builder, applicationName);
    return implementation.build(builder);
  }
}