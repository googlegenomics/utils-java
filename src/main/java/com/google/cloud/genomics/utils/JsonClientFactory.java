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

import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.services.json.AbstractGoogleJsonClient;
import com.google.api.client.googleapis.util.Utils;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.common.base.Optional;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;

public final class JsonClientFactory<
    C extends AbstractGoogleJsonClient,
    B extends AbstractGoogleJsonClient.Builder> {

  public interface Logic<
      C extends AbstractGoogleJsonClient,
      B extends AbstractGoogleJsonClient.Builder> {

    C build(B builder);

    B newBuilder(
        HttpTransport transport,
        JsonFactory jsonFactory,
        HttpRequestInitializer requestInitializer);
  }

  private static GoogleCredential googleCredential(
      Collection<String> scopes,
      HttpTransport transport,
      JsonFactory jsonFactory) throws IOException {
    GoogleCredential credential = GoogleCredential.getApplicationDefault(transport, jsonFactory);
    return scopes.isEmpty() ? credential : credential.createScoped(scopes);
  }

  public static <C extends AbstractGoogleJsonClient, B extends AbstractGoogleJsonClient.Builder>
      JsonClientFactory<C, B> of(Logic<? extends C, B> logic) {
    return new JsonClientFactory<C, B>(logic);
  }

  private Optional<JsonFactory> jsonFactory = Optional.absent();
  private final Logic<? extends C, B> logic;
  private Optional<HttpRequestInitializer> requestInitializer = Optional.absent();
  private Optional<HttpTransport> transport = Optional.absent();

  private JsonClientFactory(Logic<? extends C, B> logic) {
    this.logic = logic;
  }

  public C create(Collection<String> scopes) throws IOException {
    HttpTransport transport = this.transport.or(Utils.getDefaultTransport());
    JsonFactory jsonFactory = this.jsonFactory.or(Utils.getDefaultJsonFactory());
    return logic.build(logic.newBuilder(
        transport,
        jsonFactory,
        this.requestInitializer.or(googleCredential(scopes, transport, jsonFactory))));
  }

  public C create(String... scopes) throws IOException {
    return create(Arrays.asList(scopes));
  }

  public JsonClientFactory<C, B> setJsonFactory(JsonFactory jsonFactory) {
    this.jsonFactory = Optional.of(jsonFactory);
    return this;
  }

  public JsonClientFactory<C, B> setRequestInitializer(HttpRequestInitializer requestInitializer) {
    this.requestInitializer = Optional.of(requestInitializer);
    return this;
  }

  public JsonClientFactory<C, B> setTransport(HttpTransport transport) {
    this.transport = Optional.of(transport);
    return this;
  }
}