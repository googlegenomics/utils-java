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

import com.google.api.services.genomics.GenomicsRequest;

import java.io.IOException;
import java.io.Serializable;

/**
 * An object describing when to retry if a request fails.
 */
public abstract class RetryPolicy<C extends GenomicsRequest<D>, D>
    implements Serializable {

  /**
   * An instance is instantiated each time a request is made to the API and is consulted only if
   * the request failed.
   */
  public abstract class Instance {

    /**
     * Should we retry the request or not?
     */
    public abstract boolean shouldRetry(C request, IOException e);
  }

  public static <C extends GenomicsRequest<D>, D> RetryPolicy<C, D> alwaysRetry() {
    return constant(true);
  }

  public static <C extends GenomicsRequest<D>, D> RetryPolicy<C, D> defaultPolicy() {
    return neverRetry();
  }

  public static <C extends GenomicsRequest<D>, D> RetryPolicy<C, D> neverRetry() {
    return constant(false);
  }

  private static <C extends GenomicsRequest<D>, D> RetryPolicy<C, D> constant(final boolean retry) {
    return new RetryPolicy<C, D>() {

          private final Instance instance =
              new Instance() {
                @Override public boolean shouldRetry(C genomicsRequest, IOException e) {
                  return retry;
                }
              };

          @Override public Instance createInstance() {
            return instance;
          }
        };
  }

  /**
   * Retry requests up to {@code n} times.
   */
  public static <C extends GenomicsRequest<D>, D> RetryPolicy<C, D> nAttempts(final int n) {
    return new RetryPolicy<C, D>() {
          @Override public Instance createInstance() {
            return new Instance() {

              private int count = 0;

              @Override public boolean shouldRetry(C genomicsRequest, IOException e) {
                return count++ < n;
              }
            };
          }
        };
  }

  public abstract Instance createInstance();

  public final D execute(C request) throws IOException {
    for (Instance instance = createInstance(); true;) {
      try {
        return request.execute();
      } catch (IOException e) {
        if (!instance.shouldRetry(request, e)) {
          throw e;
        }
      }
    }
  }
}
