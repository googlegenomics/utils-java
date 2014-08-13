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

import com.google.api.client.json.GenericJson;
import com.google.api.services.genomics.Genomics;
import com.google.api.services.genomics.GenomicsRequest;
import com.google.api.services.genomics.model.Callset;
import com.google.api.services.genomics.model.Dataset;
import com.google.api.services.genomics.model.Job;
import com.google.api.services.genomics.model.Read;
import com.google.api.services.genomics.model.Readset;
import com.google.api.services.genomics.model.SearchCallsetsRequest;
import com.google.api.services.genomics.model.SearchCallsetsResponse;
import com.google.api.services.genomics.model.SearchJobsRequest;
import com.google.api.services.genomics.model.SearchJobsResponse;
import com.google.api.services.genomics.model.SearchReadsRequest;
import com.google.api.services.genomics.model.SearchReadsResponse;
import com.google.api.services.genomics.model.SearchReadsetsRequest;
import com.google.api.services.genomics.model.SearchReadsetsResponse;
import com.google.api.services.genomics.model.SearchVariantsRequest;
import com.google.api.services.genomics.model.SearchVariantsResponse;
import com.google.api.services.genomics.model.Variant;
import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import com.google.common.collect.AbstractSequentialIterator;
import com.google.common.collect.FluentIterable;

import java.io.IOException;
import java.util.Iterator;

/**
 * An abstraction that understands the {@code pageToken} / {@code nextPageToken} protocol for paging
 * results back to the user.
 *
 * <p>The {@link #search(GenericJson, GenomicsRequestInitializer)} method can obtain an
 * {@link Iterable} to the objects returned from a search request. Although it is possible to invoke
 * this method directly, the {@code Iterable} that is returned may throw {@link SearchException}
 * during iteration. Client code is responsible for catching this exception where the
 * {@code Iterable} is consumed, unwrapping the underlying {@link IOException}, and handling or
 * rethrowing it.</p>
 *
 * <p>A safer alternative for consuming the results of a search is
 * {@link #search(GenericJson, GenomicsRequestInitializer, Callback)}. This method requires the
 * client code to pass in a {@link Callback} object that consumes the search results and will unwrap
 * and rethrow {@link IOException}s that occur during iteration for you.</p>
 *
 * <p>Example usage: Fetching all {@link Readset}s in a {@link Dataset}:</p>
 * <pre>
 * {@code
 * Genomics stub = ...;
 * String datasetId = ...;
 * Paginator.Readsets searchReadsets = Paginator.Readsets.create(stub);
 * for (Readset readset =
 *     searchReadsets.search(new SearchReadsetsRequest().setDatasetId(datasetId))) {
 *   // do something with readset
 * }
 * }
 * </pre>
 *
 * @param <A> The API type. One of {@link com.google.api.services.genomics.Genomics.Callsets},
 *        {@link com.google.api.services.genomics.Genomics.Jobs},
 *        {@link com.google.api.services.genomics.Genomics.Reads},
 *        {@link com.google.api.services.genomics.Genomics.Readsets}, or
 *        {@link com.google.api.services.genomics.Genomics.Variants}.
 * @param <B> The request type. One of {@link SearchCallsetsRequest}, {@link SearchJobsRequest},
 *        {@link SearchReadsRequest}, {@link SearchReadsetsRequest}, or
 *        {@link SearchVariantsRequest}.
 * @param <C> The {@link GenomicsRequest} type. One of
 *        {@link com.google.api.services.genomics.Genomics.Callsets.Search},
 *        {@link com.google.api.services.genomics.Genomics.Jobs.Search},
 *        {@link com.google.api.services.genomics.Genomics.Reads.Search},
 *        {@link com.google.api.services.genomics.Genomics.Readsets.Search}, or
 *        {@link com.google.api.services.genomics.Genomics.Variants.Search}.
 * @param <D> The response type. One of {@link SearchCallsetsResponse}, {@link SearchJobsResponse},
 *        {@link SearchReadsResponse}, {@link SearchReadsetsResponse}, or
 *        {@link SearchVariantsResponse}.
 * @param <E> The type of object being streamed back to the user. One of {@link Callset},
 *        {@link Job}, {@link Read}, {@link Readset}, or {@link Variant}.
 */
public abstract class Paginator<
    A,
    B extends GenericJson,
    C extends GenomicsRequest<D>,
    D extends GenericJson,
    E extends GenericJson> {

  /**
   * A callback object for {@link #search(GenericJson, GenomicsRequestInitializer, Callback)} that
   * can consume all or part of the results from a search request and accumulate a value, which
   * becomes the value returned from the
   * {@link #search(GenericJson, GenomicsRequestInitializer, Callback)} method.
   *
   * @param <E> The type of objects returned from a search
   * @param <F> The type of object to accumulate when consuming search results.
   */
  public interface Callback<E extends GenericJson, F> {

    /**
     * Consume the search results and accumulate an object to return.
     *
     * @param responses The search results to consume.
     * @return the accumulated summary value.
     * @throws IOException
     */
    F consumeResponses(Iterable<E> responses) throws IOException;
  }

  /**
   * A {@link Paginator} for the {@code searchCallsets()} API.
   */
  public static class Callsets extends Paginator<
      Genomics.Callsets,
      SearchCallsetsRequest,
      Genomics.Callsets.Search,
      SearchCallsetsResponse,
      Callset> {

    /**
     * Static factory method.
     *
     * @param genomics The {@link Genomics} stub.
     * @return the new paginator.
     */
    public static Callsets create(Genomics genomics) {
      return create(genomics, neverRetry());
    }

    /**
     * Static factory method.
     *
     * @param genomics The {@link Genomics} stub.
     * @param retryPolicy A retry policy specifying behavior when a request fails.
     * @return the new paginator.
     */
    public static Callsets create(
        Genomics genomics,
        Supplier<? extends RetryPolicy<? super SearchCallsetsRequest>> retryPolicy) {
      return new Callsets(genomics, retryPolicy);
    }

    private Callsets(
        Genomics genomics,
        Supplier<? extends RetryPolicy<? super SearchCallsetsRequest>> retryPolicy) {
      super(genomics, retryPolicy);
    }

    @Override Genomics.Callsets.Search createSearch(
        Genomics.Callsets api,
        SearchCallsetsRequest request) throws IOException {
      return api.search(request);
    }

    @Override Genomics.Callsets getApi(Genomics genomics) {
      return genomics.callsets();
    }

    @Override String getNextPageToken(SearchCallsetsResponse response) {
      return response.getNextPageToken();
    }

    @Override Iterable<Callset> getResponses(SearchCallsetsResponse response) {
      return response.getCallsets();
    }

    @Override void setPageToken(SearchCallsetsRequest request, String pageToken) {
      request.setPageToken(pageToken);
    }
  }

  /**
   * A hook for customizing {@link GenomicsRequest} request objects before they are sent to the
   * server. Typically, users will use this to call {@link GenomicsRequest#setFields} to specify
   * which fields of the response object should be set.
   *
   * @param <C> The type of {@code GenomicsRequest} to initialize.
   */
  public interface GenomicsRequestInitializer<C extends GenomicsRequest<? extends GenericJson>> {

    /**
     * Initialize the given {@link GenomicsRequest} object.
     *
     * @param search The object to initialize.
     */
    void initialize(C search);
  }

  /**
   * A {@link Paginator} for the {@code searchJobs()} API.
   */
  public static class Jobs extends Paginator<
      Genomics.Jobs,
      SearchJobsRequest,
      Genomics.Jobs.Search,
      SearchJobsResponse,
      Job> {

    /**
     * Static factory method.
     *
     * @param genomics The {@link Genomics} stub.
     * @return the new paginator.
     */
    public static Jobs create(Genomics genomics) {
      return create(genomics, neverRetry());
    }

    /**
     * Static factory method.
     *
     * @param genomics The {@link Genomics} stub.
     * @param retryPolicy A retry policy specifying behavior when a request fails.
     * @return the new paginator.
     */
    public static Jobs create(
        Genomics genomics,
        Supplier<? extends RetryPolicy<? super SearchJobsRequest>> retryPolicy) {
      return new Jobs(genomics, retryPolicy);
    }

    private Jobs(
        Genomics genomics,
        Supplier<? extends RetryPolicy<? super SearchJobsRequest>> retryPolicy) {
      super(genomics, retryPolicy);
    }

    @Override Genomics.Jobs.Search createSearch(
        Genomics.Jobs api,
        SearchJobsRequest request) throws IOException {
      return api.search(request);
    }

    @Override Genomics.Jobs getApi(Genomics genomics) {
      return genomics.jobs();
    }

    @Override String getNextPageToken(SearchJobsResponse response) {
      return response.getNextPageToken();
    }

    @Override Iterable<Job> getResponses(SearchJobsResponse response) {
      return response.getJobs();
    }

    @Override void setPageToken(SearchJobsRequest request, String pageToken) {
      request.setPageToken(pageToken);
    }
  }

  /**
   * A {@link Paginator} for the {@code searchReads()} API.
   */
  public static class Reads extends Paginator<
      Genomics.Reads,
      SearchReadsRequest,
      Genomics.Reads.Search,
      SearchReadsResponse,
      Read> {

    /**
     * Static factory method.
     *
     * @param genomics The {@link Genomics} stub.
     * @return the new paginator.
     */
    public static Reads create(Genomics genomics) {
      return create(genomics, neverRetry());
    }

    /**
     * Static factory method.
     *
     * @param genomics The {@link Genomics} stub.
     * @param retryPolicy A retry policy specifying behavior when a request fails.
     * @return the new paginator.
     */
    public static Reads create(
        Genomics genomics,
        Supplier<? extends RetryPolicy<? super SearchReadsRequest>> retryPolicy) {
      return new Reads(genomics, retryPolicy);
    }

    private Reads(
        Genomics genomics,
        Supplier<? extends RetryPolicy<? super SearchReadsRequest>> retryPolicy) {
      super(genomics, retryPolicy);
    }

    @Override Genomics.Reads.Search createSearch(
        Genomics.Reads api,
        SearchReadsRequest request) throws IOException {
      return api.search(request);
    }

    @Override Genomics.Reads getApi(Genomics genomics) {
      return genomics.reads();
    }

    @Override String getNextPageToken(SearchReadsResponse response) {
      return response.getNextPageToken();
    }

    @Override Iterable<Read> getResponses(SearchReadsResponse response) {
      return response.getReads();
    }

    @Override void setPageToken(SearchReadsRequest request, String pageToken) {
      request.setPageToken(pageToken);
    }
  }

  /**
   * A {@link Paginator} for the {@code searchReadsets()} API.
   */
  public static class Readsets extends Paginator<
      Genomics.Readsets,
      SearchReadsetsRequest,
      Genomics.Readsets.Search,
      SearchReadsetsResponse,
      Readset> {

    /**
     * Static factory method.
     *
     * @param genomics The {@link Genomics} stub.
     * @return the new paginator.
     */
    public static Readsets create(Genomics genomics) {
      return create(genomics, neverRetry());
    }

    /**
     * Static factory method.
     *
     * @param genomics The {@link Genomics} stub.
     * @param retryPolicy A retry policy specifying behavior when a request fails.
     * @return the new paginator.
     */
    public static Readsets create(
        Genomics genomics,
        Supplier<? extends RetryPolicy<? super SearchReadsetsRequest>> retryPolicy) {
      return new Readsets(genomics, retryPolicy);
    }

    private Readsets(
        Genomics genomics,
        Supplier<? extends RetryPolicy<? super SearchReadsetsRequest>> retryPolicy) {
      super(genomics, retryPolicy);
    }

    @Override Genomics.Readsets.Search createSearch(
        Genomics.Readsets api,
        SearchReadsetsRequest request) throws IOException {
      return api.search(request);
    }

    @Override Genomics.Readsets getApi(Genomics genomics) {
      return genomics.readsets();
    }

    @Override String getNextPageToken(SearchReadsetsResponse response) {
      return response.getNextPageToken();
    }

    @Override Iterable<Readset> getResponses(SearchReadsetsResponse response) {
      return response.getReadsets();
    }

    @Override void setPageToken(SearchReadsetsRequest request, String pageToken) {
      request.setPageToken(pageToken);
    }
  }

  /**
   * An object describing when to retry requests when a request fails.
   *
   * @param <B> The request type.
   */
  public interface RetryPolicy<B extends GenericJson> {

    /**
     * Should we retry the request?
     *
     * @param request The request that failed.
     * @param exception The last {@link IOException} that was thrown.
     * @return {@code true} if the request should be retried, {@code false} otherwise.
     */
    boolean retryRequest(B request, IOException exception);
  }

  /**
   * A {@link RuntimeException} for wrapping {@link IOException}s that occur during lazy consumption
   * of search results.
   */
  public static class SearchException extends RuntimeException {

    private static final long serialVersionUID = -72047056528032423L;

    SearchException(IOException cause) {
      super(cause);
    }

    @Override public synchronized IOException getCause() {
      return (IOException) super.getCause();
    }
  }

  /**
   * A {@link Paginator} for the {@code searchVariants()} API.
   */
  public static class Variants extends Paginator<
      Genomics.Variants,
      SearchVariantsRequest,
      Genomics.Variants.Search,
      SearchVariantsResponse,
      Variant> {

    /**
     * Static factory method.
     *
     * @param genomics The {@link Genomics} stub.
     * @return the new paginator.
     */
    public static Variants create(Genomics genomics) {
      return create(genomics, neverRetry());
    }

    /**
     * Static factory method.
     *
     * @param genomics The {@link Genomics} stub.
     * @param retryPolicy A retry policy specifying behavior when a request fails.
     * @return the new paginator.
     */
    public static Variants create(
        Genomics genomics,
        Supplier<? extends RetryPolicy<? super SearchVariantsRequest>> retryPolicy) {
      return new Variants(genomics, retryPolicy);
    }

    private Variants(
        Genomics genomics,
        Supplier<? extends RetryPolicy<? super SearchVariantsRequest>> retryPolicy) {
      super(genomics, retryPolicy);
    }

    @Override Genomics.Variants.Search createSearch(
        Genomics.Variants api,
        SearchVariantsRequest request) throws IOException {
      return api.search(request);
    }

    @Override Genomics.Variants getApi(Genomics genomics) {
      return genomics.variants();
    }

    @Override String getNextPageToken(SearchVariantsResponse response) {
      return response.getNextPageToken();
    }

    @Override Iterable<Variant> getResponses(SearchVariantsResponse response) {
      return response.getVariants();
    }

    @Override void setPageToken(SearchVariantsRequest request, String pageToken) {
      request.setPageToken(pageToken);
    }
  }

  /**
   * A static factory method for a {@link RetryPolicy} that retries requests indefinitely.
   *
   * @return a supplier of the retry policy.
   */
  public static <B extends GenericJson> Supplier<RetryPolicy<B>> alwaysRetry() {
    return Suppliers.<RetryPolicy<B>>ofInstance(
        new RetryPolicy<B>() {
          @Override public boolean retryRequest(B request, IOException exception) {
            return true;
          }
        });
  }

  /**
   * A static factory method for a {@link RetryPolicy} that never retries requests.
   *
   * @return a supplier of the retry policy.
   */
  public static <B extends GenericJson> Supplier<RetryPolicy<B>> neverRetry() {
    return Suppliers.<RetryPolicy<B>>ofInstance(
        new RetryPolicy<B>() {
          @Override public boolean retryRequest(B request, IOException exception) {
            return false;
          }
        });
  }

  /**
   * A static factory method for a {@link RetryPolicy} that retries up to {@code n} times before
   * giving up.
   *
   * @param n The number of times to retry.
   * @return a supplier of the retry policy.
   */
  public static <B extends GenericJson> Supplier<RetryPolicy<B>> retryNTimes(final int n) {
    return new Supplier<RetryPolicy<B>>() {
          @Override public RetryPolicy<B> get() {
            return new RetryPolicy<B>() {

                  private int i = 0;

                  @Override public boolean retryRequest(B request, IOException exception) {
                    return i++ < n;
                  }
                };
          }
        };
  }

  private final Genomics genomics;
  private final Supplier<? extends RetryPolicy<? super B>> retryPolicy;

  Paginator(Genomics genomics, Supplier<? extends RetryPolicy<? super B>> retryPolicy) {
    this.genomics = genomics;
    this.retryPolicy = retryPolicy;
  }

  abstract C createSearch(A api, B request) throws IOException;

  abstract A getApi(Genomics genomics);

  abstract String getNextPageToken(D response);

  abstract Iterable<E> getResponses(D response);

  private class Pair {
    @SuppressWarnings("hiding")
    final B request;
    final D response;

    Pair(B request, D response) {
      this.request = request;
      this.response = response;
    }
  }

  /**
   * Search for objects. Warning: the returned {@link Iterable} may throw {@link SearchException}
   * during iteration; users are encouraged to call 
   * {@link #search(GenericJson, GenomicsRequestInitializer, Callback)} instead.
   *
   * @param request The search request.
   * @param initializer The {@link GenomicsRequestInitializer} to initialize requests with.
   * @return A lazy stream of search results.
   */
  public final Iterable<E> search(
      final B request,
      final GenomicsRequestInitializer<? super C> initializer) {
    return FluentIterable
        .from(new Iterable<Pair>() {
              @Override public Iterator<Pair> iterator() {
                return new AbstractSequentialIterator<Pair>(new Pair(request, null)) {
                      @Override protected Pair computeNext(Pair pair) {
                        return Optional.fromNullable(pair.request)
                            .transform(
                                new Function<B, Pair>() {

                                  @Override public Pair apply(final B request) {
                                    D response = createSearch(getApi(genomics), request);
                                    return new Pair(
                                        Optional.fromNullable(getNextPageToken(response))
                                            .transform(
                                                new Function<String, B>() {
                                                  @Override public B apply(String pageToken) {
                                                    setPageToken(request, pageToken);
                                                    return request;
                                                  }
                                                })
                                            .orNull(),
                                        response);
                                  }

                                  private D createSearch(A api, B request) {
                                    for (RetryPolicy<? super B> policy = retryPolicy.get(); true;) {
                                      try {
                                        C search = Paginator.this.createSearch(api, request);
                                        initializer.initialize(search);
                                        return search.execute();
                                      } catch (IOException e) {
                                        if (!policy.retryRequest(request, e)) {
                                          throw new SearchException(e);
                                        }
                                      }
                                    }
                                  }
                                })
                            .orNull();
                      }
                    };
              }
            })
        .skip(1)
        .transform(
            new Function<Pair, D>() {
              @Override public D apply(Pair pair) {
                return pair.response;
              }
            })
        .transformAndConcat(
            new Function<D, Iterable<E>>() {
              @Override public Iterable<E> apply(D response) {
                return getResponses(response);
              }
            });
  }

  /**
   * An exception safe way of consuming search results. Client code supplies a callback which is
   * used to consume the result stream and accumulate a value, which becomes the value returned
   * from this method.
   *
   * @param request The search request.
   * @param initializer The {@link GenomicsRequestInitializer} with which to initialize requests.
   * @param callback The {@link Callback} used to consume search results.
   * @return whatever value {@link Callback#consumeResponses} returned.
   * @throws IOException if an IOException occurred while consuming search results.
   */
  public final <F> F search(
      B request,
      GenomicsRequestInitializer<? super C> initializer,
      Callback<E, ? extends F> callback) throws IOException {
    try {
      return callback.consumeResponses(search(request, initializer));
    } catch (SearchException e) {
      throw e.getCause();
    }
  }

  /**
   * A convenience method that allows the client code to specify the value of
   * {@link GenomicsRequest#setFields} with using a {@link GenomicsRequestInitializer}.
   *
   * @param request The search request.
   * @param fields The fields to set.
   * @return the stream of search results.
   */
  public final Iterable<E> search(final B request, final String fields) {
    return search(
        request,
        new GenomicsRequestInitializer<C>() {
          @Override public void initialize(C search) {
            if (fields != null) {
              search.setFields(fields);
            }
          }
        });
  }

  abstract void setPageToken(B request, String pageToken);
}
