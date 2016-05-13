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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;

import org.hamcrest.CoreMatchers;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.io.ByteArrayOutputStream;
import java.io.ObjectOutputStream;

@RunWith(JUnit4.class)
public class OfflineAuthTest {

  @Test
  public void testOfflineAuthFromApiKey() throws Exception {
    OfflineAuth auth = new OfflineAuth("xyz");
    assertTrue(auth.hasApiKey());
    assertFalse(auth.hasStoredCredential());
    assertNull(auth.getClientId());
    assertNull(auth.getClientSecret());
    assertNull(auth.getRefreshToken());

    // Depending upon the environment in which these unit tests are run, the
    // Application Default Credentials may or may not be available.
    try {
      Credential cred = auth.getCredential();
      assertNotNull(cred);
      assertNotNull(auth.getCredentials());
    } catch (Exception e) {
      assertThat(e.getMessage(),
          CoreMatchers.containsString("Unable to get application default credentials."));
    }
  }

  @Test
  public void testOfflineAuth_apiKeyIsSerializable() throws Exception {
    OfflineAuth auth = new OfflineAuth("xyz");

    // This mimics the serialization flow used by Dataflow pipelines.
    ObjectOutputStream oos = new ObjectOutputStream(new ByteArrayOutputStream());
    oos.writeObject(auth);
  }

  @Test
  public void testOfflineAuth_credentialIsSerializable() throws Exception {
    Credential credential = new GoogleCredential.Builder()
      .setJsonFactory(JacksonFactory.getDefaultInstance())
      .setTransport(GoogleNetHttpTransport.newTrustedTransport())
      .setClientSecrets("theClientId", "theClientSecret")
      .build()
      .setRefreshToken("theRefreshToken");
    OfflineAuth auth = new OfflineAuth(credential);

    // This mimics the serialization flow used by Dataflow pipelines.
    ObjectOutputStream oos = new ObjectOutputStream(new ByteArrayOutputStream());
    oos.writeObject(auth);
  }
}
