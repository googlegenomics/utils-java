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

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Arrays;
import java.util.List;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AbstractPromptReceiver;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.auth.oauth2.GoogleOAuthConstants;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.common.base.Preconditions;

/**
 * Convenience routines for obtaining credentials.
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
public class CredentialFactory {
  
  private static final String MISSING_ADC_EXCEPTION_MESSAGE =
      "Unable to get application default credentials. Please see "
      + "https://developers.google.com/identity/protocols/application-default-credentials "
      + "for details on how to specify credentials. This is dependent "
      + "on the gcloud core component version 2015.02.05 or newer to be able "
      + "to get credentials from the currently authorized user via gcloud auth.";

  private static final List<String> SCOPES = Arrays.asList(
      "https://www.googleapis.com/auth/cloud-platform");

  private static final File CREDENTIAL_STORE = new File(
      System.getProperty("user.home"), ".store");
  
  private static class PromptReceiver extends AbstractPromptReceiver {
    @Override
    public String getRedirectUri() {
      return GoogleOAuthConstants.OOB_REDIRECT_URI;
    }
  }
  
  /**
   * Obtain the Application Default com.google.api.client.auth.oauth2.Credential
   * 
   * @return the Application Default Credential
   */
  public static GoogleCredential getApplicationDefaultCredential() {
    try {
      return GoogleCredential.getApplicationDefault();
    } catch (IOException e) {
      throw new RuntimeException(MISSING_ADC_EXCEPTION_MESSAGE, e);
    }
  }
  
  /**
   * Obtain the Application Default com.google.auth.oauth2.GoogleCredentials
   * 
   * This is from the newer OAuth library https://github.com/google/google-auth-library-java
   * which is used by gRPC.
   * 
   * @return the Application Default Credentials
   */
  public static GoogleCredentials getApplicationDefaultCredentials() {
    try {
      return GoogleCredentials.getApplicationDefault();
    } catch (IOException e) {
      throw new RuntimeException(MISSING_ADC_EXCEPTION_MESSAGE, e);
    }
  }

  /**
   * Creates an OAuth2 credential from client secrets, which may require an interactive authorization prompt.
   *
   * Use this method when the Application Default Credential is not sufficient.
   *
   * @param clientSecretsFile The {@code client_secrets.json} file path.
   * @param credentialId The credentialId for use in identifying the credential in the persistent credential store.
   * @return The user credential
   */
  public static Credential getCredentialFromClientSecrets(String clientSecretsFile,
      String credentialId) {
    Preconditions.checkArgument(clientSecretsFile != null);
    Preconditions.checkArgument(credentialId != null);

    HttpTransport httpTransport;
    try {
      httpTransport = GoogleNetHttpTransport.newTrustedTransport();
    } catch (IOException | GeneralSecurityException e) {
      throw new RuntimeException("Could not create HTTPS transport for use in credential creation", e);
    }

    JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();
    GoogleClientSecrets clientSecrets;

    try {
      clientSecrets = GoogleClientSecrets.load(jsonFactory,
          new FileReader(clientSecretsFile));
    } catch (IOException e) {
      throw new RuntimeException(
          "Could not read the client secrets from file: " + clientSecretsFile,
          e);
    }

    FileDataStoreFactory dataStoreFactory;
    try {
      dataStoreFactory = new FileDataStoreFactory(CREDENTIAL_STORE);
    } catch (IOException e) {
      throw new RuntimeException("Could not create persisten credential store " + CREDENTIAL_STORE, e);
    }

    GoogleAuthorizationCodeFlow flow;
    try {
      flow = new GoogleAuthorizationCodeFlow.Builder(
          httpTransport, jsonFactory, clientSecrets, SCOPES)
          .setDataStoreFactory(dataStoreFactory)
          .build();
    } catch (IOException e) {
      throw new RuntimeException("Could not build credential authorization flow", e);
    }

    // The credentialId identifies the credential in the persistent credential store.
    Credential credential;
    try {
      credential = new AuthorizationCodeInstalledApp(flow, new PromptReceiver())
          .authorize(credentialId);
    } catch (IOException e) {
      throw new RuntimeException("Could not perform credential authorization flow", e);
    }
    return credential;
  }
}
