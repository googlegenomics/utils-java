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
import static org.junit.Assert.fail;

import java.io.IOException;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpResponseException;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.LowLevelHttpRequest;
import com.google.api.client.http.LowLevelHttpResponse;
import com.google.api.client.testing.http.HttpTesting;
import com.google.api.client.testing.http.MockHttpTransport;
import com.google.api.client.testing.http.MockLowLevelHttpRequest;
import com.google.api.client.testing.http.MockLowLevelHttpResponse;
import com.google.api.services.genomics.Genomics;

@RunWith(JUnit4.class)
public class GenomicsFactoryITCase {

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
