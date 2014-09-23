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

/**
 * An object describing when to retry if a request fails.
 */
public abstract class RetryPolicy<R extends GenomicsRequest<?>> {

  /**
   * An instance is instantiated each time a request is made to the API and is consulted only if
   * the request failed.
   */
  public abstract class Instance {

    /**
     * Should we retry the request or not?
     */
    protected abstract boolean shouldRetry(R request, IOException e);
  }

  public static final RetryPolicy<GenomicsRequest<?>>
      /** Retry requests indefinitely */
      ALWAYS_RETRY = constant(true),
      /** Never retry requests */
      NEVER_RETRY = constant(false);

  private static RetryPolicy<GenomicsRequest<?>> constant(final boolean retry) {
    return new RetryPolicy<GenomicsRequest<?>>() {

          private final Instance instance =
              new Instance() {
                @Override protected boolean shouldRetry(
                    GenomicsRequest<?> genomicsRequest, IOException e) {
                  return retry;
                }
              };

          @Override protected Instance createInstance() {
            return instance;
          }
        };
  }

  /**
   * Retry requests up to {@code n} times.
   */
  public static RetryPolicy<GenomicsRequest<?>> nAttempts(final int n) {
    return new RetryPolicy<GenomicsRequest<?>>() {
          @Override protected Instance createInstance() {
            return new Instance() {

              private int count = 0;

              @Override protected boolean shouldRetry(
                  GenomicsRequest<?> genomicsRequest, IOException e) {
                return count++ < n;
              }
            };
          }
        };
  }

  protected abstract Instance createInstance();
}
