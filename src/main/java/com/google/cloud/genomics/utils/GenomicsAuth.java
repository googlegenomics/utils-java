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
import com.google.api.client.googleapis.extensions.java6.auth.oauth2.GooglePromptReceiver;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.Lists;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.genomics.Genomics;

import java.io.*;
import java.security.GeneralSecurityException;
import java.util.List;

public class GenomicsAuth {
  /**
   * This is the main Genomics API scope that is required by nearly all API calls.
   */
  public static final String GENOMICS_SCOPE = "https://www.googleapis.com/auth/genomics";

  /**
   * The BigQuery API scope is needed to export variants to BigQuery.
   */
  public static final String BIGQUERY_SCOPE = "https://www.googleapis.com/auth/bigquery";

  /**
   * The Devstorage API scope is needed to import from or export to Google Cloud Storage.
   */
  public static final String DEVSTORAGE_SCOPE =
      "https://www.googleapis.com/auth/devstorage.read_write";

  private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();

  /**
   * Loads a client secrets file into an GoogleClientSecrets object that can be used to
   * send authorized requests to the Google Genomics API.
   *
   * @param clientSecretsFilename The path to the client secrets file that will be loaded.
   * @return A GoogleClientSecrets object that can be used with a GoogleAuthorizationCodeFlow.
   * @throws java.io.FileNotFoundException if the client secrets file doesn't exist.
   * @throws java.io.IOException if the file couldn't be parsed into a GoogleClientSecrets object.
   */
  public static GoogleClientSecrets loadClientSecrets(String clientSecretsFilename)
      throws IOException {
    InputStream inputStream = new FileInputStream(new File(clientSecretsFilename));
    return GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(inputStream));
  }

  /**
   * Creates an authorized Genomics service from a client secrets file.
   * It uses a local server for OAuth and asks for access to the one main Genomics API scope.
   * See the other buildService method for more options.
   *
   * @param applicationName The name of your application. This will be used to store authorization
   *     credentials and will show up in UserAgent headers. Make sure it is unique to your
   *     application.
   * @param clientSecretsFilename The path to the client secrets file that will be loaded.
   * @return An authorized genomics service object that can call the Google Genomics API.
   * @throws GeneralSecurityException
   * @throws IOException
   */
  public static Genomics buildService(String applicationName, String clientSecretsFilename)
      throws GeneralSecurityException, IOException {
    List<String> scopes = Lists.newArrayList();
    scopes.add(GENOMICS_SCOPE);
    return buildService(applicationName, scopes, clientSecretsFilename, false);
  }

  /**
   * Creates an authorized Genomics service from a client secrets file.
   * This uses reasonable defaults for the http transport, json factory, main data store directory.
   * Use Genomics.Builder for custom setups.
   *
   * @param applicationName The name of your application. This will be used to store authorization
   *     credentials and will show up in UserAgent headers. Make sure it is unique to your
   *     application.
   * @param scopes The scopes that will be required by the Google Genomics API calls you are using.
   *     See the constants in this file for useful scope values.
   * @param clientSecretsFilename The path to the client secrets file that will be loaded.
   * @param noLocalServer If true, a command line prompt will be used to ask the user for OAuth
   *     access. If false, a local server will be started and a browser window will be
   *     automatically opened for the user. False creates a better user experience, but will not
   *     work in all environments.
   * @return An authorized genomics service object that can call the Google Genomics API.
   * @throws GeneralSecurityException
   * @throws IOException
   */
  public static Genomics buildService(String applicationName, List<String> scopes,
      String clientSecretsFilename, boolean noLocalServer)
      throws GeneralSecurityException, IOException {

    GoogleClientSecrets clientSecrets = loadClientSecrets(clientSecretsFilename);
    NetHttpTransport httpTransport = GoogleNetHttpTransport.newTrustedTransport();
    File dataStoreDir = new File(System.getProperty("user.home"), ".store/" + applicationName);
    FileDataStoreFactory dataStoreFactory = new FileDataStoreFactory(dataStoreDir);

    GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
        httpTransport, JacksonFactory.getDefaultInstance(), clientSecrets, scopes)
        .setDataStoreFactory(dataStoreFactory).build();
    VerificationCodeReceiver receiver = noLocalServer ? new GooglePromptReceiver() :
        new LocalServerReceiver();
    final Credential credential = new AuthorizationCodeInstalledApp(flow, receiver)
        .authorize(applicationName);

    return new Genomics.Builder(httpTransport, JSON_FACTORY, credential)
        .setApplicationName(applicationName)
        .setHttpRequestInitializer(new HttpRequestInitializer() {
          @Override
          public void initialize(HttpRequest httpRequest) throws IOException {
            credential.initialize(httpRequest);
            httpRequest.setReadTimeout(60000); // 60 seconds
          }
        }).build();
  }

}