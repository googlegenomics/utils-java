/*
 * Copyright (C) 2015 Google Inc.
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

import java.io.IOException;
import java.io.Serializable;
import java.security.GeneralSecurityException;

import com.google.api.client.auth.oauth2.ClientParametersAuthentication;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.auth.oauth2.UserCredentials;
import com.google.common.base.Preconditions;

/**
 * The purpose of the OfflineAuth class is to encapsulate an apiKey or stored user credential
 * for offline use in pipeline systems such as Dataflow or Spark.
 * 
 * If the OfflineAuth object contains neither an apiKey or user credential, it will fall back to 
 * the Application Default Credential.
 * 
 * For more information about auth, please see:
 * <ul>
 *    <li>https://developers.google.com/identity/protocols/application-default-credentials
 *    <li>https://developers.google.com/api-client-library/java/google-oauth-java-client/oauth2
 *    <li>https://github.com/google/google-api-java-client/tree/master/google-api-client/src/main/java/com/google/api/client/googleapis/auth/oauth2
 *    <li>https://github.com/google/google-oauth-java-client
 *    <li>https://github.com/google/google-auth-library-java
 * </ul>
 */
@SuppressWarnings("serial")
public class OfflineAuth implements Serializable {
  private String apiKey;
  private String clientId;
  private String clientSecret;
  private String refreshToken;
  
  /**
   * Creates an empty offline-friendly auth object.
   *
   * Use this method when your application will fall back to using the Application Default
   * Credential (e.g., via Google Cloud Dataflow) but the code interface takes an OfflineAuth object
   * so that it could alternatively use a user credential or an apiKey.
   */
  public OfflineAuth() {}

  /**
   * Creates offline-friendly auth object using a credential object.
   *
   * Use this method when your application has already performed the oauth
   * flow and needs to store and use the credential later in an offline
   * manner (e.g., via Google Cloud Dataflow).
   * 
   * @param credential The credential to be used for requests.
   */
  public OfflineAuth(Credential credential) {
    Preconditions.checkNotNull(credential);
    ClientParametersAuthentication clientParams =
        (ClientParametersAuthentication) credential.getClientAuthentication();
    this.clientId = clientParams.getClientId();
    this.clientSecret = clientParams.getClientSecret();
    this.refreshToken = credential.getRefreshToken();
  }

  /**
   * Creates offline-friendly auth object using an apiKey.
   *
   * Use this method when you need to store the resulting OfflineAuth for later use.
   *
   * @param apiKey The API key of the Google Cloud Platform project to be used for requests.
   */
  public OfflineAuth(String apiKey) {
    Preconditions.checkNotNull(apiKey);
    this.apiKey = apiKey;
  }

  /**
   * @return Whether an api key is stored in this OfflineAuth.
   */
  public boolean hasApiKey() {
    return null != apiKey;
  }
  
  /**
   * @return the apiKey
   */
  public String getApiKey() {
    return apiKey;
  }

  /**
   * @return Whether a credential is stored in this OfflineAuth.
   */
  public boolean hasStoredCredential() {
    return null != refreshToken;
  }

  /**
   * @return The stored clientId or null if the Application Default Credential is to be used.
   */
  public String getClientId() {
    return clientId;
  }

  /**
   * @return The stored clientSecret or null if the Application Default Credential is to be used.
   */
  public String getClientSecret() {
    return clientSecret;
  }

  /**
   * @return The stored refreshToken or null if the Application Default Credential is to be used.
   */
  public String getRefreshToken() {
    return refreshToken;
  }
  
  /**
   * Return the stored user credential, if applicable, or fall back to the Application Default Credential.
   * 
   * @return The com.google.api.client.auth.oauth2.Credential object.
   */
  public Credential getCredential() {
    if (hasStoredCredential()) {
      HttpTransport httpTransport;
      try {
        httpTransport = GoogleNetHttpTransport.newTrustedTransport();
      } catch (IOException | GeneralSecurityException e) {
        throw new RuntimeException("Could not create HTTPS transport for use in credential creation", e);
      }

      return new GoogleCredential.Builder()
      .setJsonFactory(JacksonFactory.getDefaultInstance())
      .setTransport(httpTransport)
      .setClientSecrets(getClientId(), getClientSecret())
      .build()
      .setRefreshToken(getRefreshToken());
    }
    return CredentialFactory.getApplicationDefaultCredential();
  }
  
  /**
   * Return the stored user credentials, if applicable, or fall back to the Application Default Credentials.
   * 
   * Specifically, gRPC uses the new Google OAuth library.  See https://github.com/google/google-auth-library-java
   * 
   * @return The com.google.auth.Credentials object.
   */
  public GoogleCredentials getCredentials() {
    if (hasStoredCredential()) {
      return new UserCredentials(clientId, clientSecret, refreshToken);
    }
    return CredentialFactory.getApplicationDefaultCredentials();
  }

}
