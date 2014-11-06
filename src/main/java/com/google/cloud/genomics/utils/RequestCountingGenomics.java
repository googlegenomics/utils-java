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

import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.JsonObjectParser;
import com.google.api.services.genomics.Genomics;

public final class RequestCountingGenomics extends Genomics {

  public static RequestCountingGenomics of(Genomics genomics) {
    HttpRequestFactory requestFactory = genomics.getRequestFactory();
    RequestCountingGenomics requestCountingGenomics = new RequestCountingGenomics(
        requestFactory.getTransport(),
        genomics.getJsonFactory(),
        requestFactory.getInitializer());
    RequestCounter<Genomics, Genomics.Builder> requestCounter =
        new RequestCounter<Genomics, Genomics.Builder>(genomics) {

          @Override protected Genomics build(Builder builder) {
            return builder.build();
          }

          @Override protected Builder newClientBuilder(
              HttpTransport transport,
              JsonFactory jsonFactory,
              HttpRequestInitializer initializer) {
            return new Genomics.Builder(transport, jsonFactory, initializer);
          }
        };
      requestCountingGenomics.delegate = requestCounter.client();
      requestCountingGenomics.requestCounter = requestCounter;
      return requestCountingGenomics;
  }

  private Genomics delegate;
  private RequestCounter<Genomics, Genomics.Builder> requestCounter;

  private RequestCountingGenomics(
      HttpTransport transport,
      JsonFactory jsonFactory,
      HttpRequestInitializer httpRequestInitializer) {
    super(transport, jsonFactory, httpRequestInitializer);
  }

  @Override public Callsets callsets() {
    return delegate.callsets();
  }

  @Override public Datasets datasets() {
    return delegate.datasets();
  }

  @Override public Experimental experimental() {
    return delegate.experimental();
  }

  @Override public JsonObjectParser getObjectParser() {
    return delegate.getObjectParser();
  }

  public int initializedRequestsCount() {
    return requestCounter.initializedRequestsCount();
  }

  @Override public Jobs jobs() {
    return delegate.jobs();
  }

  @Override public Readgroupsets readgroupsets() {
    return delegate.readgroupsets();
  }

  @Override public Reads reads() {
    return delegate.reads();
  }

  @Override public References references() {
    return delegate.references();
  }

  @Override public Referencesets referencesets() {
    return delegate.referencesets();
  }

  public int unsuccessfulRequestsCount() {
    return requestCounter.unsuccessfulRequestsCount();
  }

  @Override public Variants variants() {
    return delegate.variants();
  }

  @Override public Variantsets variantsets() {
    return delegate.variantsets();
  }
}