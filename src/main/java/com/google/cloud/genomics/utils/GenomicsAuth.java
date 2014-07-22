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

import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;

import java.io.*;

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

}