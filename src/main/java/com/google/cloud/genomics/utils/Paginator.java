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

import com.google.api.services.genomics.Genomics;
import com.google.api.services.genomics.GenomicsRequest;
import com.google.api.services.genomics.model.CallSet;
import com.google.api.services.genomics.model.Dataset;
import com.google.api.services.genomics.model.Job;
import com.google.api.services.genomics.model.Read;
import com.google.api.services.genomics.model.Readset;
import com.google.api.services.genomics.model.SearchCallSetsRequest;
import com.google.api.services.genomics.model.SearchCallSetsResponse;
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
import com.google.common.collect.AbstractSequentialIterator;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.Lists;

import java.io.IOException;
import java.io.Serializable;
import java.util.Iterator;

/**
 * An abstraction that understands the {@code pageToken} / {@code nextPageToken} protocol for paging
 * results back to the user.
 *
 * <p>The {@link #search(Object, GenomicsRequestInitializer)} method can obtain an
 * {@link Iterable} to the objects returned from a search request. Although it is possible to invoke
 * this method directly, the {@code Iterable} that is returned may throw {@link SearchException}
 * during iteration. Client code is responsible for catching this exception where the
 * {@code Iterable} is consumed, unwrapping the underlying {@link IOException}, and handling or
 * rethrowing it.</p>
 *
 * <p>A safer alternative for consuming the results of a search is
 * {@link #search(Object, GenomicsRequestInitializer, Callback)}. This method requires the
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
 * @param <B> The request type. One of {@link SearchCallSetsRequest}, {@link SearchJobsRequest},
 *        {@link SearchReadsRequest}, {@link SearchReadsetsRequest}, or
 *        {@link SearchVariantsRequest}.
 * @param <C> The {@link GenomicsRequest} type. One of
 *        {@link com.google.api.services.genomics.Genomics.Callsets.Search},
 *        {@link com.google.api.services.genomics.Genomics.Jobs.Search},
 *        {@link com.google.api.services.genomics.Genomics.Reads.Search},
 *        {@link com.google.api.services.genomics.Genomics.Readsets.Search}, or
 *        {@link com.google.api.services.genomics.Genomics.Variants.Search}.
 * @param <D> The response type. One of {@link SearchCallSetsResponse}, {@link SearchJobsResponse},
 *        {@link SearchReadsResponse}, {@link SearchReadsetsResponse}, or
 *        {@link SearchVariantsResponse}.
 * @param <E> The type of object being streamed back to the user. One of {@link CallSet},
 *        {@link Job}, {@link Read}, {@link Readset}, or {@link Variant}.
 */
public abstract class Paginator<A, B, C extends GenomicsRequest<D>, D, E> {

  /**
   * A callback object for {@link #search(Object, GenomicsRequestInitializer, Callback)} that
   * can consume all or part of the results from a search request and accumulate a value, which
   * becomes the value returned from the
   * {@link #search(Object, GenomicsRequestInitializer, Callback)} method.
   *
   * @param <E> The type of objects returned from a search
   * @param <F> The type of object to accumulate when consuming search results.
   */
  public interface Callback<E, F> {

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
      SearchCallSetsRequest,
      Genomics.Callsets.Search,
      SearchCallSetsResponse,
      CallSet> {

    /**
     * Static factory method.
     *
     * @param genomics The {@link Genomics} stub.
     * @return the new paginator.
     */
    public static Callsets create(Genomics genomics) {
      return create(genomics, RetryPolicy.NEVER_RETRY);
    }

    /**
     * Static factory method.
     *
     * @param genomics The {@link Genomics} stub.
     * @param retryPolicy A retry policy specifying behavior when a request fails.
     * @return the new paginator.
     */
    public static Callsets create(Genomics genomics, RetryPolicy<? super Genomics.Callsets.Search> retryPolicy) {
      return new Callsets(genomics, retryPolicy);
    }

    private Callsets(Genomics genomics, RetryPolicy<? super Genomics.Callsets.Search> retryPolicy) {
      super(genomics, retryPolicy);
    }

    @Override Genomics.Callsets.Search createSearch(
        Genomics.Callsets api,
        SearchCallSetsRequest request) throws IOException {
      return api.search(request);
    }

    @Override Genomics.Callsets getApi(Genomics genomics) {
      return genomics.callsets();
    }

    @Override String getNextPageToken(SearchCallSetsResponse response) {
      return response.getNextPageToken();
    }

    @Override Iterable<CallSet> getResponses(SearchCallSetsResponse response) {
      return response.getCallSets();
    }

    @Override void setPageToken(SearchCallSetsRequest request, String pageToken) {
      request.setPageToken(pageToken);
    }
  }

  /**
   * A factory interface for paginators.
   *
   * @param <P> The paginator type.
   */
  public interface Factory<P extends Paginator<?, ?, ?, ?, ?>> extends Serializable {

    /**
     * Create the paginator from the given {@link Genomics} stub.
     */
    P createPaginator(Genomics genomics);
  }

  /**
   * A hook for customizing {@link GenomicsRequest} request objects before they are sent to the
   * server. Typically, users will use this to call {@link GenomicsRequest#setFields} to specify
   * which fields of the response object should be set.
   *
   * @param <C> The type of {@code GenomicsRequest} to initialize.
   */
  public interface GenomicsRequestInitializer<C extends GenomicsRequest<?>> {

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
      return create(genomics, RetryPolicy.NEVER_RETRY);
    }

    /**
     * Static factory method.
     *
     * @param genomics The {@link Genomics} stub.
     * @param retryPolicy A retry policy specifying behavior when a request fails.
     * @return the new paginator.
     */
    public static Jobs create(Genomics genomics, RetryPolicy<? super Genomics.Jobs.Search> retryPolicy) {
      return new Jobs(genomics, retryPolicy);
    }

    private Jobs(Genomics genomics, RetryPolicy<? super Genomics.Jobs.Search> retryPolicy) {
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
      return create(genomics, RetryPolicy.NEVER_RETRY);
    }

    /**
     * Static factory method.
     *
     * @param genomics The {@link Genomics} stub.
     * @param retryPolicy A retry policy specifying behavior when a request fails.
     * @return the new paginator.
     */
    public static Reads create(Genomics genomics, RetryPolicy<? super Genomics.Reads.Search> retryPolicy) {
      return new Reads(genomics, retryPolicy);
    }

    private Reads(Genomics genomics, RetryPolicy<? super Genomics.Reads.Search> retryPolicy) {
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
      return create(genomics, RetryPolicy.NEVER_RETRY);
    }

    /**
     * Static factory method.
     *
     * @param genomics The {@link Genomics} stub.
     * @param retryPolicy A retry policy specifying behavior when a request fails.
     * @return the new paginator.
     */
    public static Readsets create(Genomics genomics, RetryPolicy<? super Genomics.Readsets.Search> retryPolicy) {
      return new Readsets(genomics, retryPolicy);
    }

    private Readsets(Genomics genomics, RetryPolicy<? super Genomics.Readsets.Search> retryPolicy) {
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
      return create(genomics, RetryPolicy.NEVER_RETRY);
    }

    /**
     * Static factory method.
     *
     * @param genomics The {@link Genomics} stub.
     * @param retryPolicy A retry policy specifying behavior when a request fails.
     * @return the new paginator.
     */
    public static Variants create(Genomics genomics, RetryPolicy<? super Genomics.Variants.Search> retryPolicy) {
      return new Variants(genomics, retryPolicy);
    }

    private Variants(Genomics genomics, RetryPolicy<? super Genomics.Variants.Search> retryPolicy) {
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
   * Callsets factory
   */
  public static final Factory<Callsets> CALLSETS =
      new Factory<Callsets>() {
        @Override public Callsets createPaginator(Genomics genomics) {
          return Callsets.create(genomics);
        }
      };

  /**
   * Jobs factory
   */
  public static final Factory<Jobs> JOBS =
      new Factory<Jobs>() {
        @Override public Jobs createPaginator(Genomics genomics) {
          return Jobs.create(genomics);
        }
      };

  /**
   * Reads factory
   */
  public static final Factory<Reads> READS =
      new Factory<Reads>() {
        @Override public Reads createPaginator(Genomics genomics) {
          return Reads.create(genomics);
        }
      };

  /**
   * Readsets factory
   */
  public static final Factory<Readsets> READSETS =
      new Factory<Readsets>() {
        @Override public Readsets createPaginator(Genomics genomics) {
          return Readsets.create(genomics);
        }
      };

  /**
   * Variants factory
   */
  public static final Factory<Variants> VARIANTS =
      new Factory<Variants>() {
        @Override public Variants createPaginator(Genomics genomics) {
          return Variants.create(genomics);
        }
      };

  private final Genomics genomics;
  private final RetryPolicy<? super C> retryPolicy;

  public Paginator(Genomics genomics, RetryPolicy<? super C> retryPolicy) {
    this.genomics = genomics;
    this.retryPolicy = retryPolicy;
  }

  abstract C createSearch(A api, B request) throws IOException;

  abstract A getApi(Genomics genomics);

  abstract String getNextPageToken(D response);

  abstract Iterable<E> getResponses(D response);

  /**
   * Search for objects.
   *
   * @param request The search request.
   * @return the stream of search results.
   */
  public final Iterable<E> search(final B request) {
    return search(
        request,
        new GenomicsRequestInitializer<C>() {
          @Override public void initialize(C search) {}
        });
  }

  /**
   * Search for objects. Warning: the returned {@link Iterable} may throw {@link SearchException}
   * during iteration; users are encouraged to call 
   * {@link #search(Object, GenomicsRequestInitializer, Callback)} instead.
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
                                    for (RetryPolicy<? super C>.Instance policy =
                                        retryPolicy.createInstance(); true;) {
                                      C search = null;
                                      try {
                                        initializer.initialize(
                                            search = Paginator.this.createSearch(api, request));
                                        return search.execute();
                                      } catch (IOException e) {
                                        if (!policy.shouldRetry(search, e)) {
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
                Iterable<E> responses = getResponses(response);
                if (responses == null) {
                  return Lists.newArrayList();
                }
                return responses;
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
   * Search for objects with a partial response.
   *
   * A convenience method that allows the client code to specify the value of
   * {@link GenomicsRequest#setFields} using a {@link GenomicsRequestInitializer}.
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
            if (fields != null && !fields.isEmpty()) {
              search.setFields(fields);
            }
          }
        });
  }

  abstract void setPageToken(B request, String pageToken);
}
