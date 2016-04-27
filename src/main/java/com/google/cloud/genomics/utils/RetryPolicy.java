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

import java.io.IOException;
import java.io.Serializable;

import com.google.api.services.genomics.GenomicsRequest;

/**
 * An object describing when to retry if a request fails.
 */
public abstract class RetryPolicy implements Serializable {

  /**
   * An instance is instantiated each time a request is made to the API and is consulted only if
   * the request failed.
   */
  public abstract class Instance {

    /**
     * Should we retry the request or not?
     */
    public abstract boolean shouldRetry(GenomicsRequest request, IOException e);
  }

  public static RetryPolicy defaultPolicy() {
    return nAttempts(3);
  }

  public static RetryPolicy alwaysRetry() {
    return constant(true);
  }

  public static RetryPolicy neverRetry() {
    return constant(false);
  }

  private static RetryPolicy constant(final boolean retry) {
    return new RetryPolicy() {

      private final Instance instance =
          new Instance() {
            @Override public boolean shouldRetry(GenomicsRequest genomicsRequest, IOException e) {
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
  public static RetryPolicy nAttempts(final int n) {
    return new RetryPolicy() {
      @Override public Instance createInstance() {
        return new Instance() {

          private int count = 0;

          @Override public boolean shouldRetry(GenomicsRequest genomicsRequest, IOException e) {
            return count++ < n;
          }
        };
      }
    };
  }

  public abstract Instance createInstance();

  /**
   * Execute the given {@link GenomicsRequest} and return the response.
   *
   * This method begins by creating an {@code RetryPolicy.Instance} and then attempts to execute the
   * request by invoking {@link GenomicsRequest#execute}. If the request fails with an
   * {@link IOException}, the {@link RetryPolicy.Instance#shouldRetry} method is consulted to
   * determine if the request should be retried.
   *
   * @param request The {@code GenomicsRequest} to execute.
   * @return The response from executing the request.
   * @throws IOException if executing the request fails and {@code this} RetryPolicy decides not to
   *         retry the request.
   */
  public final <C extends GenomicsRequest<D>, D> D execute(C request) throws IOException {
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
