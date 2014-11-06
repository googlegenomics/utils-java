/*
 * Copyright (C) 2014 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package com.google.cloud.genomics.utils;

import com.google.api.client.googleapis.services.json.AbstractGoogleJsonClient;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.HttpResponse;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.HttpUnsuccessfulResponseHandler;
import com.google.api.client.json.JsonFactory;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * A utility for counting the number of requests that were initialized, successful and unsuccessful
 * for a given instance of {@link AbstractGoogleJsonClient}.
 */
public abstract class RequestCounter<
    Client extends AbstractGoogleJsonClient,
    ClientBuilder extends AbstractGoogleJsonClient.Builder> {

  private final Client client;
  private final AtomicInteger initializedRequestsCount = new AtomicInteger();
  private final AtomicInteger unsuccessfulRequestsCount = new AtomicInteger();

  protected RequestCounter(Client client) {
    final HttpRequestFactory requestFactory = client.getRequestFactory();
    ClientBuilder builder = newClientBuilder(
        requestFactory.getTransport(),
        client.getJsonFactory(),
        new HttpRequestInitializer() {

          private final HttpRequestInitializer initializer = requestFactory.getInitializer();

          @Override public void initialize(final HttpRequest request) throws IOException {
            initializedRequestsCount.incrementAndGet();
            initializer.initialize(request);
            request.setUnsuccessfulResponseHandler(
                new HttpUnsuccessfulResponseHandler() {

                  private final HttpUnsuccessfulResponseHandler unsuccessfulResponseHandler =
                      request.getUnsuccessfulResponseHandler();

                  @Override public boolean handleResponse(HttpRequest request,
                      HttpResponse response, boolean supportsRetry) throws IOException {
                    unsuccessfulRequestsCount.incrementAndGet();
                    return unsuccessfulResponseHandler
                        .handleResponse(request, response, supportsRetry);
                  }
                });
          }
        });
    builder.setApplicationName(client.getApplicationName());
    builder.setGoogleClientRequestInitializer(client.getGoogleClientRequestInitializer());
    builder.setRootUrl(client.getRootUrl());
    builder.setServicePath(client.getServicePath());
    builder.setSuppressPatternChecks(client.getSuppressPatternChecks());
    builder.setSuppressRequiredParameterChecks(client.getSuppressRequiredParameterChecks());
    this.client = build(builder);
  }

  protected abstract Client build(ClientBuilder builder);

  public final Client client() {
    return client;
  }

  public final int initializedRequestsCount() {
    return initializedRequestsCount.get();
  }

  protected abstract ClientBuilder newClientBuilder(HttpTransport transport,
      JsonFactory jsonFactory, HttpRequestInitializer httpRequestInitializer);

  public final int successfulRequestsCount() {
    return initializedRequestsCount() - unsuccessfulRequestsCount();
  }

  public final int unsuccessfulRequestsCount() {
    return unsuccessfulRequestsCount.get();
  }
}