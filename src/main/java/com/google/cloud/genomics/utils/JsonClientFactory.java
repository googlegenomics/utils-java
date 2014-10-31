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
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.googleapis.services.json.AbstractGoogleJsonClient;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.common.base.Optional;
import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import com.google.common.base.Throwables;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Arrays;
import java.util.Collection;

public final class JsonClientFactory<
    C extends AbstractGoogleJsonClient,
    B extends AbstractGoogleJsonClient.Builder> {

  public static final class Builder<
      C extends AbstractGoogleJsonClient,
      B extends AbstractGoogleJsonClient.Builder> {

    private Optional<HttpTransport> httpTransport = Optional.absent();
    private Optional<JsonFactory> jsonFactory = Optional.absent();
    private final Logic<? extends C, B> logic;
    private Optional<HttpRequestInitializer> requestInitializer = Optional.absent();

    Builder(Logic<? extends C, B> logic) {
      this.logic = logic;
    }

    public JsonClientFactory<C, B> build() {
      final Supplier<HttpTransport> httpTransport = this.httpTransport.isPresent()
          ? supplier(this.httpTransport)
          : Suppliers.memoize(
              new Supplier<HttpTransport>() {
                @Override public HttpTransport get() {
                  try {
                    return GoogleNetHttpTransport.newTrustedTransport();
                  } catch (GeneralSecurityException e) {
                    throw ExceptionWrapper.wrap(e);
                  } catch (IOException e) {
                    throw ExceptionWrapper.wrap(e);
                  }
                }
              });
      final Supplier<JsonFactory> jsonFactory = this.jsonFactory.isPresent()
          ? supplier(this.jsonFactory)
          : Suppliers.<JsonFactory>ofInstance(JacksonFactory.getDefaultInstance());
      final boolean requestInitializerIsPresent = this.requestInitializer.isPresent();
      return new JsonClientFactory<C, B>(
          requestInitializerIsPresent
              ? this.<GoogleCredential>illegalStateException(
                  "Expected requestInitializerIsPresent == false")
              : Suppliers.memoize(
                  new Supplier<GoogleCredential>() {
                    @Override public GoogleCredential get() {
                      try {
                        return GoogleCredential.getApplicationDefault(
                            httpTransport.get(),
                            jsonFactory.get());
                      } catch (IOException e) {
                        throw ExceptionWrapper.wrap(e);
                      }
                    }
                  }),
          httpTransport,
          jsonFactory,
          logic,
          requestInitializerIsPresent
              ? supplier(this.requestInitializer)
              : this.<HttpRequestInitializer>illegalStateException(
                  "Expected requestInitializerIsPresent == true"),
          requestInitializerIsPresent);
    }

    private <T> Supplier<T> illegalStateException(final String message) {
      return new Supplier<T>() {
            @Override public T get() {
              throw new IllegalStateException(message);
            }
          };
    }

    public final Builder<C, B> setHttpTransport(HttpTransport httpTransport) {
      this.httpTransport = Optional.of(httpTransport);
      return this;
    }

    public final Builder<C, B> setJsonFactory(JsonFactory jsonFactory) {
      this.jsonFactory = Optional.of(jsonFactory);
      return this;
    }

    public final Builder<C, B> setRequestInitializer(HttpRequestInitializer requestInitializer) {
      this.requestInitializer = Optional.of(requestInitializer);
      return this;
    }

    private <T> Supplier<T> supplier(final Optional<? extends T> optional) {
      return new Supplier<T>() {
            @Override public T get() {
              return optional.get();
            }
          };
    }
  }

  private static class ExceptionWrapper extends RuntimeException {

    static ExceptionWrapper wrap(GeneralSecurityException cause) {
      return new ExceptionWrapper(cause);
    }

    static ExceptionWrapper wrap(IOException cause) {
      return new ExceptionWrapper(cause);
    }

    private ExceptionWrapper(Exception cause) {
      super(cause);
    }

    IllegalStateException unwrap() throws GeneralSecurityException, IOException {
      Throwable cause = getCause();
      if (cause instanceof GeneralSecurityException) {
        throw (GeneralSecurityException) cause;
      }
      if (cause instanceof IOException) {
        throw (IOException) cause;
      }
      return new IllegalStateException(
          String.format("Unexpected cause type: %s", cause.getClass()));
    }
  }

  public interface Logic<
      C extends AbstractGoogleJsonClient,
      B extends AbstractGoogleJsonClient.Builder> {

    C build(B builder) throws GeneralSecurityException, IOException;

    B newBuilder(HttpTransport httpTransport, JsonFactory jsonFactory,
        HttpRequestInitializer requestInitializer) throws GeneralSecurityException, IOException;
  }

  public static <C extends AbstractGoogleJsonClient, B extends AbstractGoogleJsonClient.Builder>
      Builder<C, B> builder(Logic<? extends C, B> logic) {
    return new Builder<C, B>(logic);
  }

  private final Supplier<GoogleCredential> credential;
  private final Supplier<HttpTransport> httpTransport;
  private final Supplier<JsonFactory> jsonFactory;
  private final Logic<? extends C, B> logic;
  private final Supplier<HttpRequestInitializer> requestInitializer;
  private final boolean requestInitializerIsPresent;

  private JsonClientFactory(
      Supplier<GoogleCredential> credential,
      Supplier<HttpTransport> httpTransport,
      Supplier<JsonFactory> jsonFactory,
      Logic<? extends C, B> logic,
      Supplier<HttpRequestInitializer> requestInitializer,
      boolean requestInitializerIsPresent) {
    this.credential = credential;
    this.httpTransport = httpTransport;
    this.jsonFactory = jsonFactory;
    this.logic = logic;
    this.requestInitializer = requestInitializer;
    this.requestInitializerIsPresent = requestInitializerIsPresent;
  }

  public C create(Collection<String> scopes) throws GeneralSecurityException, IOException {
    try {
      return logic.build(logic.newBuilder(
          httpTransport.get(),
          jsonFactory.get(),
          requestInitializerIsPresent ? requestInitializer.get() : credential(scopes)));
    } catch (ExceptionWrapper e) {
      throw e.unwrap();
    }
  }

  public C create(String... scopes) throws GeneralSecurityException, IOException {
    return create(Arrays.asList(scopes));
  }

  public C createUnchecked(Collection<String> scopes) {
    try {
      return create(scopes);
    } catch (Exception e) {
      throw Throwables.propagate(e);
    }
  }

  public C createUnchecked(String... scopes) {
    return createUnchecked(Arrays.asList(scopes));
  }

  private GoogleCredential credential(Collection<String> scopes) {
    GoogleCredential credential = this.credential.get();
    return scopes.isEmpty() ? credential : credential.createScoped(scopes);
  }
}