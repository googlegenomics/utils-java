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

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.testing.http.HttpTesting;
import com.google.api.services.genomics.Genomics;
import com.google.api.services.genomics.GenomicsScopes;
import com.google.api.services.storage.Storage;
import com.google.api.services.storage.StorageScopes;
import com.google.common.collect.Lists;

@RunWith(JUnit4.class)
public class GenomicsFactoryTest {

  @Test
  public void testBasic() throws Exception {
    GenomicsFactory genomicsFactory = GenomicsFactory.builder("test_client").build();

    Genomics genomics = genomicsFactory.fromApiKey("xyz");
    assertEquals(0, genomicsFactory.initializedRequestsCount());

    // TODO: Mock out more of this test if it becomes a problem
    try {
      genomics.jobs().get("123").execute();
    } catch (GoogleJsonResponseException e) {
      // Expected
    }
    assertEquals(1, genomicsFactory.initializedRequestsCount());
    assertEquals(1, genomicsFactory.unsuccessfulResponsesCount());
    assertEquals(0, genomicsFactory.ioExceptionsCount());

    try {
      genomics.readgroupsets().get("123").execute();
    } catch (GoogleJsonResponseException e) {
      // Expected
    }
    assertEquals(2, genomicsFactory.initializedRequestsCount());
    assertEquals(2, genomicsFactory.unsuccessfulResponsesCount());
    assertEquals(0, genomicsFactory.ioExceptionsCount());
  }

  @Test
  public void testFromOfflineAuth() throws Exception {
    GenomicsFactory genomicsFactory = GenomicsFactory.builder("test_client").build();
    OfflineAuth auth = new OfflineAuth("xyz");
    Genomics genomics = genomicsFactory.fromOfflineAuth(auth);

    assertEquals(0, genomicsFactory.initializedRequestsCount());

    try {
      genomics.jobs().get("123").execute();
    } catch (GoogleJsonResponseException e) {
      // Expected
    }
    assertEquals(1, genomicsFactory.initializedRequestsCount());

    try {
      genomics.jobs().get("123").execute();
    } catch (GoogleJsonResponseException e) {
      // Expected
    }
    assertEquals(2, genomicsFactory.initializedRequestsCount());
  }

  @Test
  public void testCustomBuilder() throws Exception {
    GenomicsFactory factory = GenomicsFactory.builder("test_client")
        .setScopes(Lists.newArrayList(StorageScopes.DEVSTORAGE_READ_ONLY, GenomicsScopes.GENOMICS))
        .build();

    Storage storage = factory.fromApiKey(new Storage.Builder(
        factory.getHttpTransport(), factory.getJsonFactory(), null), "xyz").build();
    assertEquals(0, factory.initializedRequestsCount());

    try {
      storage.buckets().get("123").execute();
    } catch (GoogleJsonResponseException e) {
      // Expected
    }
    assertEquals(1, factory.initializedRequestsCount());

    Genomics genomics = factory.fromApiKey("abc");
    try {
      genomics.jobs().get("123").execute();
    } catch (GoogleJsonResponseException e) {
      // Expected
    }
    assertEquals(2, factory.initializedRequestsCount());
  }

  @Test
  public void testDefaultTimeoutConfiguration() throws Exception {
    GenomicsFactory genomicsFactory = GenomicsFactory.builder("test_client").build();

    Genomics genomics = genomicsFactory.fromApiKey("xyz");
    HttpRequest request =
        genomics.getRequestFactory().buildGetRequest(HttpTesting.SIMPLE_GENERIC_URL);
    assertEquals(20000, request.getConnectTimeout());
    assertEquals(20000, request.getReadTimeout());
    assertEquals(5, request.getNumberOfRetries());
  }

  @Test
  public void testTimeoutConfiguration() throws Exception {
    GenomicsFactory genomicsFactory =
        GenomicsFactory.builder("test_client").setConnectTimeout(42).setReadTimeout(7)
            .setNumberOfRetries(9).build();

    Genomics genomics = genomicsFactory.fromApiKey("xyz");
    HttpRequest request =
        genomics.getRequestFactory().buildGetRequest(HttpTesting.SIMPLE_GENERIC_URL);
    assertEquals(42, request.getConnectTimeout());
    assertEquals(7, request.getReadTimeout());
    assertEquals(9, request.getNumberOfRetries());
  }
}
