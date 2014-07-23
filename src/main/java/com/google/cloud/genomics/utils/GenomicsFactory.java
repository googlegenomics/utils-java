package com.google.cloud.genomics.utils;

import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.googleapis.services.CommonGoogleClientRequestInitializer;
import com.google.api.client.googleapis.services.GoogleClientRequestInitializer;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.store.DataStoreFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.genomics.Genomics;
import com.google.api.services.genomics.GenomicsScopes;
import com.google.common.base.Function;
import com.google.common.base.Optional;

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

  public final static class Builder {

    private final String applicationName;
    private int connectTimeout = 20000;
    private DataStoreFactory dataStoreFactory;
    private HttpTransport httpTransport;
    private JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();
    private int readTimeout = 20000;
    private Optional<String> rootUrl = Optional.absent();
    private Collection<String> scopes = GenomicsScopes.all();
    private String userName = System.getProperty("user.name");

    private Builder(String applicationName) throws GeneralSecurityException, IOException {
      this.applicationName = applicationName;
      setDataStoreFactory(new FileDataStoreFactory(new File(
          System.getProperty("user.home"),
          String.format(".store/%s", applicationName.replace("/", "_")))));
      setHttpTransport(GoogleNetHttpTransport.newTrustedTransport());
    }

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
          connectTimeout);
    }

    public Builder setConnectTimeout(int connectTimeout) {
      this.connectTimeout = connectTimeout;
      return this;
    }

    public Builder setDataStoreFactory(DataStoreFactory dataStoreFactory) {
      this.dataStoreFactory = dataStoreFactory;
      return this;
    }

    public Builder setHttpTransport(HttpTransport httpTransport) {
      this.httpTransport = httpTransport;
      return this;
    }

    public Builder setJsonFactory(JsonFactory jsonFactory) {
      this.jsonFactory = jsonFactory;
      return this;
    }

    public Builder setReadTimeout(int readTimeout) {
      this.readTimeout = readTimeout;
      return this;
    }

    public Builder setRootUrl(String rootUrl) {
      this.rootUrl = Optional.of(rootUrl);
      return this;
    }

    public Builder setScopes(Collection<String> scopes) {
      this.scopes = scopes;
      return this;
    }

    public Builder setUserName(String userName) {
      this.userName = userName;
      return this;
    }
  }

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
  private final Collection<String> scopes;
  private final String userName;

  private GenomicsFactory(
      String applicationName,
      DataStoreFactory dataStoreFactory,
      HttpTransport httpTransport,
      JsonFactory jsonFactory,
      Collection<String> scopes,
      String userName,
      int readTimeout,
      Optional<String> rootUrl,
      int connectTimeout) {
    this.applicationName = applicationName;
    this.dataStoreFactory = dataStoreFactory;
    this.httpTransport = httpTransport;
    this.jsonFactory = jsonFactory;
    this.scopes = scopes;
    this.userName = userName;
    this.readTimeout = readTimeout;
    this.rootUrl = rootUrl;
    this.connectTimeout = connectTimeout;
  }

  private Genomics create(
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
                httpRequest.setReadTimeout(readTimeout);
                httpRequest.setConnectTimeout(connectTimeout);
              }
            })
        .setApplicationName(applicationName)
        .setGoogleClientRequestInitializer(googleClientRequestInitializer);
    return rootUrl
        .transform(
            new Function<String, Genomics.Builder>() {
              @Override public Genomics.Builder apply(String url) {
                return builder.setRootUrl(url);
              }
            })
        .or(builder).build();
  }

  public Genomics fromApiKey(String apiKey) {
    return create(null, new CommonGoogleClientRequestInitializer(apiKey));
  }

  public Genomics fromClientSecretsFile(File clientSecretsJson) throws IOException {
    try (Reader in = new FileReader(clientSecretsJson)) {
      GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow
          .Builder(
              httpTransport,
              jsonFactory,
              GoogleClientSecrets.load(jsonFactory, in),
              scopes)
          .setDataStoreFactory(dataStoreFactory)
          .build();
      return create(
          new AuthorizationCodeInstalledApp(flow, new LocalServerReceiver()).authorize(userName),
          null);
    }
  }

  public Genomics fromServiceAccount(String serviceAccountId, File p12File)
      throws GeneralSecurityException, IOException {
    return create(
        new GoogleCredential.Builder()
            .setTransport(httpTransport)
            .setJsonFactory(jsonFactory)
            .setServiceAccountId(serviceAccountId)
            .setServiceAccountScopes(scopes)
            .setServiceAccountPrivateKeyFromP12File(p12File)
            .build(),
        null);
  }
}

