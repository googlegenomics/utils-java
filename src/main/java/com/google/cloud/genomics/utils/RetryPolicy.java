package com.google.cloud.genomics.utils;

import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;

import java.io.IOException;

/**
 * An object that decides if a genomics API request should be retried or not if an
 * {@link IOException} is thrown.
 *
 * @param <Request> The request type.
 */
public abstract class RetryPolicy<Request> {

  public static final Supplier<RetryPolicy<?>>

      /**
       * A retry policy that infinitely retries requests.
       */
      ALWAYS_RETRY = Suppliers.<RetryPolicy<?>>ofInstance(
          new RetryPolicy<Object>() {
            @Override protected boolean retryRequest(Object request, IOException exception) {
              return true;
            }
          }),

      /**
       * A retry policy that never attempts to retry a request.
       */
      NEVER_RETRY = Suppliers.<RetryPolicy<?>>ofInstance(
          new RetryPolicy<Object>() {
            @Override protected boolean retryRequest(Object request, IOException exception) {
              return false;
            }
          });

  /**
   * Create a retry policy that will retry a request up to {@code n} times before giving up.
   *
   * @param n The number of attempts to retry the request before giving up.
   * @return a {@link Supplier} of the retry policy.
   */
  public static Supplier<RetryPolicy<?>> retryNTimes(final int n) {
    return new Supplier<RetryPolicy<?>>() {
          @Override public RetryPolicy<?> get() {
            return new RetryPolicy<Object>() {

                  private int i = 0;

                  @Override protected boolean retryRequest(Object request, IOException exception) {
                    return i++ < n;
                  }
                };
          }
        };
  }

  /**
   * Should the given request be retried or not?
   *
   * @param request The request that was attempted.
   * @param exception The last {@link IOException} that was thrown.
   * @return {@code true} if the pager should attempt to retry the request.
   */
  protected abstract boolean retryRequest(Request request, IOException exception);
}
