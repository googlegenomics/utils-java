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
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpResponseException;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.LowLevelHttpRequest;
import com.google.api.client.http.LowLevelHttpResponse;
import com.google.api.client.testing.http.HttpTesting;
import com.google.api.client.testing.http.MockHttpTransport;
import com.google.api.client.testing.http.MockLowLevelHttpRequest;
import com.google.api.client.testing.http.MockLowLevelHttpResponse;
import com.google.api.client.util.store.FileDataStoreFactory;
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
  public void testOfflineAuth() throws Exception {
    GenomicsFactory genomicsFactory = GenomicsFactory.builder("test_client").build();
    GenomicsFactory.OfflineAuth auth = genomicsFactory.getOfflineAuthFromApiKey("xyz");

    GenomicsFactory authFactory = auth.getDefaultFactory();
    assertEquals(0, authFactory.initializedRequestsCount());

    try {
      auth.getGenomics(authFactory).jobs().get("123").execute();
    } catch (GoogleJsonResponseException e) {
      // Expected
    }
    assertEquals(1, authFactory.initializedRequestsCount());

    try {
      auth.getGenomics(authFactory).jobs().get("123").execute();
    } catch (GoogleJsonResponseException e) {
      // Expected
    }
    assertEquals(2, authFactory.initializedRequestsCount());
  }

  @Test
  public void testOfflineAuth_isSerializable() throws Exception {
    GenomicsFactory genomicsFactory = GenomicsFactory.builder("test_client").build();
    GenomicsFactory.OfflineAuth auth = genomicsFactory.getOfflineAuthFromApiKey("xyz");

    // This mimics the serialization flow used by pipelines
    auth.getGenomics(auth.getDefaultFactory());
    ObjectOutputStream oos = new ObjectOutputStream(new ByteArrayOutputStream());
    oos.writeObject(auth);
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
  public void testDataStoreFactoryExceptionRetries() throws Exception {

    GenomicsFactory.Builder builder = new GenomicsFactory.Builder("test") {
      int calls = 0;

      @Override
      FileDataStoreFactory makeFileDataStoreFactory() throws IOException {
        // Throw an exception during the first two calls
        // And succeed the third time
        calls++;
        if (calls < 3) {
          throw new IOException();
        } else {
          return super.makeFileDataStoreFactory();
        }
      }
    };

    assertNotNull(builder.dataStoreFactory);
    assertEquals("test", builder.applicationName);
    assertTrue(builder.userDir.getAbsolutePath().contains(".store/test"));
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

  @Test
  public void testBackendErrorRetries() throws Exception {

    HttpTransport transport = new MockHttpTransport() {
      @Override
      public LowLevelHttpRequest buildRequest(String method, String url) throws IOException {
        return new MockLowLevelHttpRequest() {
          @Override
          public LowLevelHttpResponse execute() throws IOException {
            MockLowLevelHttpResponse response = new MockLowLevelHttpResponse();
            response.setStatusCode(500);
            return response;
          }
        };
      }
    };

    GenomicsFactory genomicsFactory =
        GenomicsFactory.builder("test_client").setHttpTransport(transport).build();
    Genomics genomics = genomicsFactory.fromApiKey("xyz");

    HttpRequest request =
        genomics.getRequestFactory().buildGetRequest(HttpTesting.SIMPLE_GENERIC_URL);
    try {
      request.execute();
      fail("this request should not have succeeded");
    } catch (HttpResponseException e) {
    }

    assertEquals(1, genomicsFactory.initializedRequestsCount());
    assertEquals(6, genomicsFactory.unsuccessfulResponsesCount());
    assertEquals(0, genomicsFactory.ioExceptionsCount());
  }

  @Test
  public void testIOExceptionRetries() throws Exception {

    HttpTransport transport = new MockHttpTransport() {
      @Override
      public LowLevelHttpRequest buildRequest(String method, String url) throws IOException {
        return new MockLowLevelHttpRequest() {
          @Override
          public LowLevelHttpResponse execute() throws IOException {
            throw new IOException();
          }
        };
      }
    };

    GenomicsFactory genomicsFactory =
        GenomicsFactory.builder("test_client").setHttpTransport(transport).build();
    Genomics genomics = genomicsFactory.fromApiKey("xyz");

    HttpRequest request =
        genomics.getRequestFactory().buildGetRequest(HttpTesting.SIMPLE_GENERIC_URL);
    try {
      request.execute();
      fail("this request should not have succeeded");
    } catch (IOException e) {
    }

    assertEquals(1, genomicsFactory.initializedRequestsCount());
    assertEquals(0, genomicsFactory.unsuccessfulResponsesCount());
    assertEquals(6, genomicsFactory.ioExceptionsCount());
  }

  @Test
  public void testUserErrorRetries() throws Exception {

    HttpTransport transport = new MockHttpTransport() {
      @Override
      public LowLevelHttpRequest buildRequest(String method, String url) throws IOException {
        return new MockLowLevelHttpRequest() {
          @Override
          public LowLevelHttpResponse execute() throws IOException {
            MockLowLevelHttpResponse response = new MockLowLevelHttpResponse();
            response.setStatusCode(404);
            return response;
          }
        };
      }
    };

    GenomicsFactory genomicsFactory =
        GenomicsFactory.builder("test_client").setHttpTransport(transport).build();
    Genomics genomics = genomicsFactory.fromApiKey("xyz");

    HttpRequest request =
        genomics.getRequestFactory().buildGetRequest(HttpTesting.SIMPLE_GENERIC_URL);
    try {
      request.execute();
      fail("this request should not have succeeded");
    } catch (HttpResponseException e) {
    }

    assertEquals(1, genomicsFactory.initializedRequestsCount());
    assertEquals(1, genomicsFactory.unsuccessfulResponsesCount());
    assertEquals(0, genomicsFactory.ioExceptionsCount());
  }
  
}
