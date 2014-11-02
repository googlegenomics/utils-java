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

import com.google.api.services.genomics.Genomics;
import com.google.api.services.genomics.GenomicsRequest;
import com.google.api.services.genomics.model.CallSet;
import com.google.api.services.genomics.model.CoverageBucket;
import com.google.api.services.genomics.model.Dataset;
import com.google.api.services.genomics.model.Job;
import com.google.api.services.genomics.model.ListCoverageBucketsResponse;
import com.google.api.services.genomics.model.ListDatasetsResponse;
import com.google.api.services.genomics.model.Read;
import com.google.api.services.genomics.model.ReadGroupSet;
import com.google.api.services.genomics.model.SearchCallSetsRequest;
import com.google.api.services.genomics.model.SearchCallSetsResponse;
import com.google.api.services.genomics.model.SearchJobsRequest;
import com.google.api.services.genomics.model.SearchJobsResponse;
import com.google.api.services.genomics.model.SearchReadGroupSetsRequest;
import com.google.api.services.genomics.model.SearchReadGroupSetsResponse;
import com.google.api.services.genomics.model.SearchReadsRequest;
import com.google.api.services.genomics.model.SearchReadsResponse;
import com.google.api.services.genomics.model.SearchVariantSetsRequest;
import com.google.api.services.genomics.model.SearchVariantSetsResponse;
import com.google.api.services.genomics.model.SearchVariantsRequest;
import com.google.api.services.genomics.model.SearchVariantsResponse;
import com.google.api.services.genomics.model.Variant;
import com.google.api.services.genomics.model.VariantSet;
import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.collect.AbstractSequentialIterator;
import com.google.common.collect.FluentIterable;

import java.io.IOException;
import java.io.Serializable;
import java.util.Collections;
import java.util.Iterator;

/**
 * An abstraction that understands the {@code pageToken} / {@code nextPageToken} protocol for paging
 * results back to the user.
 *
 * <p>The {@link #search(Object, GenomicsRequestInitializer)} method can obtain an {@link Iterable}
 * to the objects returned from a search request. Although it is possible to invoke this method
 * directly, the {@code Iterable} that is returned may throw {@link SearchException} during
 * iteration. Client code is responsible for catching this exception where the {@code Iterable} is
 * consumed, unwrapping the underlying {@link IOException}, and handling or rethrowing it.
 * </p>
 *
 * <p>A safer alternative for consuming the results of a search is
 * {@link #search(Object, GenomicsRequestInitializer, Callback)}. This method requires the client
 * code to pass in a {@link Callback} object that consumes the search results and will unwrap and
 * rethrow {@link IOException}s that occur during iteration for you.
 * </p>
 *
 * <p>Example usage: Fetching all {@link ReadGroupSet}s in a {@link Dataset}:</p>
 * <pre>
 *{@code
 *Genomics stub = ...;
 *String datasetId = ...;
 *Paginator.Readsets searchReadsets = Paginator.Readsets.create(stub);
 *for (Readset readset =
 *    searchReadsets.search(new SearchReadsetsRequest().setDatasetId(datasetId))) {
 *  // do something with readset
 *}
 *}
 *</pre>
 *
 * @param <A> The API type.
 * @param <B> The request type.
 * @param <C> The {@link GenomicsRequest} subtype.
 * @param <D> The response type.
 * @param <E> The type of object being streamed back to the user.
 */
public abstract class Paginator<A, B, C extends GenomicsRequest<D>, D, E> {

  private abstract static class AbstractDatasets<B> extends Paginator<
      Genomics.Datasets,
      B,
      Genomics.Datasets.List,
      ListDatasetsResponse,
      Dataset> {

    AbstractDatasets(Genomics genomics) {
      super(genomics);
    }

    @Override final Genomics.Datasets.List createSearch(Genomics.Datasets api, B request,
        Optional<String> pageToken) throws IOException {
      final Genomics.Datasets.List list = api.list();
      setRequest(list, request);
      return pageToken
          .transform(
              new Function<String, Genomics.Datasets.List>() {
                @Override public Genomics.Datasets.List apply(String pageToken) {
                  return list.setPageToken(pageToken);
                }
              })
          .or(list);
    }

    @Override final Genomics.Datasets getApi(Genomics genomics) {
      return genomics.datasets();
    }

    @Override final String getNextPageToken(ListDatasetsResponse response) {
      return response.getNextPageToken();
    }

    @Override final Iterable<Dataset> getResponses(ListDatasetsResponse response) {
      return response.getDatasets();
    }

    abstract void setRequest(Genomics.Datasets.List list, B request);
  }

  /**
   * A callback object for {@link #search(Object, GenomicsRequestInitializer, Callback)} that can
   * consume all or part of the results from a search request and accumulate a value, which becomes
   * the value returned from the {@link #search(Object, GenomicsRequestInitializer, Callback)}
   * method.
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
      return create(genomics);
    }

    private Callsets(Genomics genomics) {
      super(genomics);
    }

    @Override Genomics.Callsets.Search createSearch(Genomics.Callsets api,
        final SearchCallSetsRequest request, Optional<String> pageToken) throws IOException {
      return api.search(pageToken
          .transform(
              new Function<String, SearchCallSetsRequest>() {
                @Override public SearchCallSetsRequest apply(String pageToken) {
                  return request.setPageToken(pageToken);
                }
              })
          .or(request));
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
  }

  /**
   * A {@link Paginator} for the {@code searchDatasets()} API.
   */
  public static class Datasets extends AbstractDatasets<Long> {

    /**
     * Static factory method.
     *
     * @param genomics The {@link Genomics} stub.
     * @return the new paginator.
     */
    public static Datasets create(Genomics genomics) {
      return new Datasets(genomics);
    }

    private Datasets(Genomics genomics) {
      super(genomics);
    }

    @Override void setRequest(Genomics.Datasets.List list, Long projectId) {
      list.setProjectNumber(projectId);
    }
  }

  public interface Factory<P extends Paginator<?, ?, C, D, ?>, C extends GenomicsRequest<D>, D>
      extends Serializable {

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
      return create(genomics);
    }

    private Jobs(Genomics genomics) {
      super(genomics);
    }

    @Override Genomics.Jobs.Search createSearch(Genomics.Jobs api, final SearchJobsRequest request,
        Optional<String> pageToken) throws IOException {
      return api.search(pageToken
          .transform(
              new Function<String, SearchJobsRequest>() {
                @Override public SearchJobsRequest apply(String pageToken) {
                  return request.setPageToken(pageToken);
                }
              })
          .or(request));
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
  }

  private class Pair {

    final C request;
    final D response;

    Pair(C request, D response) {
      this.request = request;
      this.response = response;
    }
  }

  /**
   * A {@link Paginator} for the {@code searchDatasets()} API that returns doesn't require a project
   * ID and returns only public datasets.
   */
  public static class PublicDatasets extends AbstractDatasets<Void> {

    /**
     * Static factory method.
     *
     * @param genomics The {@link Genomics} stub.
     * @return the new paginator.
     */
    public static PublicDatasets create(Genomics genomics) {
      return create(genomics);
    }

    private PublicDatasets(Genomics genomics) {
      super(genomics);
    }

    public Iterable<Dataset> search() {
      return search((Void) null);
    }

    public <F> F search(Callback<Dataset, ? extends F> callback) throws IOException {
      return search((Void) null, callback);
    }

    public Iterable<Dataset> search(
        GenomicsRequestInitializer<? super Genomics.Datasets.List> initializer) {
      return search(null, initializer);
    }

    public <F> F search(
        GenomicsRequestInitializer<? super Genomics.Datasets.List> initializer,
        Callback<Dataset, ? extends F> callback) throws IOException {
      return search(null, initializer, callback);
    }

    public Iterable<Dataset> search(String fields) {
      return search(null, fields);
    }

    public <F> F search(String fields, Callback<Dataset, ? extends F> callback)
        throws IOException {
      return search(null, fields, callback);
    }

    @Override void setRequest(Genomics.Datasets.List list, Void ignored) {}
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
      return create(genomics);
    }

    private Reads(Genomics genomics) {
      super(genomics);
    }

    @Override Genomics.Reads.Search createSearch(Genomics.Reads api, final SearchReadsRequest request,
        Optional<String> pageToken) throws IOException {
      return api.search(pageToken
          .transform(
              new Function<String, SearchReadsRequest>() {
                @Override public SearchReadsRequest apply(String pageToken) {
                  return request.setPageToken(pageToken);
                }
              })
          .or(request));
    }

    @Override Genomics.Reads getApi(Genomics genomics) {
      return genomics.reads();
    }

    @Override String getNextPageToken(SearchReadsResponse response) {
      return response.getNextPageToken();
    }

    @Override Iterable<Read> getResponses(SearchReadsResponse response) {
      return response.getAlignments();
    }
  }

  /**
   * A {@link Paginator} for the {@code searchReadGroupSets()} API.
   */
  public static class ReadGroupSets extends Paginator<Genomics.Readgroupsets,
      SearchReadGroupSetsRequest, Genomics.Readgroupsets.Search, SearchReadGroupSetsResponse,
      ReadGroupSet> {

    /**
     * A {@link Paginator} for the {@code coveragebuckets()} API.
     */
    public static class Coveragebuckets extends Paginator<
        Genomics.Readgroupsets.Coveragebuckets,
        String,
        Genomics.Readgroupsets.Coveragebuckets.List,
        ListCoverageBucketsResponse,
        CoverageBucket> {

      /**
       * Static factory method.
       *
       * @param genomics The {@link Genomics} stub.
       * @return the new paginator.
       */
      public static Coveragebuckets create(Genomics genomics) {
        return create(genomics);
      }

      private Coveragebuckets(Genomics genomics) {
        super(genomics);
      }

      @Override Genomics.Readgroupsets.Coveragebuckets.List createSearch(
          Genomics.Readgroupsets.Coveragebuckets api,
          String request, Optional<String> pageToken) throws IOException {
        final Genomics.Readgroupsets.Coveragebuckets.List list = api.list(request);
        return pageToken
            .transform(
                new Function<String, Genomics.Readgroupsets.Coveragebuckets.List>() {
                  @Override public Genomics.Readgroupsets.Coveragebuckets.List apply(
                      String pageToken) {
                    return list.setPageToken(pageToken);
                  }
                })
            .or(list);
      }

      @Override Genomics.Readgroupsets.Coveragebuckets getApi(Genomics genomics) {
        return genomics.readgroupsets().coveragebuckets();
      }

      @Override String getNextPageToken(ListCoverageBucketsResponse response) {
        return response.getNextPageToken();
      }

      @Override Iterable<CoverageBucket> getResponses(ListCoverageBucketsResponse response) {
        return response.getCoverageBuckets();
      }
    }

    /**
     * Static factory method.
     *
     * @param genomics The {@link Genomics} stub.
     * @return the new paginator.
     */
    public static ReadGroupSets create(Genomics genomics) {
      return new ReadGroupSets(genomics);
    }

    private ReadGroupSets(Genomics genomics) {
      super(genomics);
    }

    @Override Genomics.Readgroupsets.Search createSearch(Genomics.Readgroupsets api,
        final SearchReadGroupSetsRequest request, Optional<String> pageToken) throws IOException {
      return api.search(pageToken
          .transform(
              new Function<String, SearchReadGroupSetsRequest>() {
                @Override public SearchReadGroupSetsRequest apply(String pageToken) {
                  return request.setPageToken(pageToken);
                }
              })
          .or(request));
    }

    @Override Genomics.Readgroupsets getApi(Genomics genomics) {
      return genomics.readgroupsets();
    }

    @Override String getNextPageToken(SearchReadGroupSetsResponse response) {
      return response.getNextPageToken();
    }

    @Override Iterable<ReadGroupSet> getResponses(SearchReadGroupSetsResponse response) {
      return response.getReadGroupSets();
    }
  }

  public abstract static class ReadGroupSetsFactory implements Factory<ReadGroupSets,
      Genomics.Readgroupsets.Search, SearchReadGroupSetsResponse> {

    public final Factory<ReadGroupSets.Coveragebuckets, Genomics.Readgroupsets.Coveragebuckets.List,
        ListCoverageBucketsResponse> COVERAGEBUCKETS = new Factory<ReadGroupSets.Coveragebuckets,
        Genomics.Readgroupsets.Coveragebuckets.List, ListCoverageBucketsResponse>() {
      @Override public ReadGroupSets.Coveragebuckets createPaginator(Genomics genomics) {
        return ReadGroupSets.Coveragebuckets.create(genomics);
      }
    };

    private ReadGroupSetsFactory() {}
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
      return create(genomics);
    }

    private Variants(Genomics genomics) {
      super(genomics);
    }

    @Override Genomics.Variants.Search createSearch(Genomics.Variants api,
        final SearchVariantsRequest request, Optional<String> pageToken) throws IOException {
      return api.search(pageToken
          .transform(
              new Function<String, SearchVariantsRequest>() {
                @Override public SearchVariantsRequest apply(String pageToken) {
                  return request.setPageToken(pageToken);
                }
              })
          .or(request));
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
  }

  /**
   * A {@link Paginator} for the {@code searchVariantsets()} API.
   */
  public static class Variantsets extends Paginator<
      Genomics.Variantsets,
      SearchVariantSetsRequest,
      Genomics.Variantsets.Search,
      SearchVariantSetsResponse,
      VariantSet> {

    /**
     * Static factory method.
     *
     * @param genomics The {@link Genomics} stub.
     * @return the new paginator.
     */
    public static Variantsets create(Genomics genomics) {
      return create(genomics);
    }

    private Variantsets(Genomics genomics) {
      super(genomics);
    }

    @Override Genomics.Variantsets.Search createSearch(Genomics.Variantsets api,
        final SearchVariantSetsRequest request, Optional<String> pageToken) throws IOException {
      return api.search(pageToken
          .transform(
              new Function<String, SearchVariantSetsRequest>() {
                @Override public SearchVariantSetsRequest apply(String pageToken) {
                  return request.setPageToken(pageToken);
                }
              })
          .or(request));
    }

    @Override Genomics.Variantsets getApi(Genomics genomics) {
      return genomics.variantsets();
    }

    @Override String getNextPageToken(SearchVariantSetsResponse response) {
      return response.getNextPageToken();
    }

    @Override Iterable<VariantSet> getResponses(SearchVariantSetsResponse response) {
      return response.getVariantSets();
    }
  }

  public static final Factory<Callsets, Genomics.Callsets.Search, SearchCallSetsResponse> CALLSETS =
      new Factory<Callsets, Genomics.Callsets.Search, SearchCallSetsResponse>() {
        @Override public Callsets createPaginator(Genomics genomics) {
          return Callsets.create(genomics);
        }
      };

  public static final Factory<Datasets, Genomics.Datasets.List, ListDatasetsResponse> DATASETS =
      new Factory<Datasets, Genomics.Datasets.List, ListDatasetsResponse>() {
        @Override public Datasets createPaginator(Genomics genomics) {
          return Datasets.create(genomics);
        }
      };

  private static final GenomicsRequestInitializer<GenomicsRequest<?>> DEFAULT_INITIALIZER =
      new GenomicsRequestInitializer<GenomicsRequest<?>>() {
        @Override public void initialize(GenomicsRequest<?> search) {}
      };

  public static final Factory<Jobs, Genomics.Jobs.Search, SearchJobsResponse> JOBS =
      new Factory<Jobs, Genomics.Jobs.Search, SearchJobsResponse>() {
        @Override public Jobs createPaginator(Genomics genomics) {
          return Jobs.create(genomics);
        }
      };

  public static final Factory<PublicDatasets, Genomics.Datasets.List, ListDatasetsResponse>
      PUBLIC_DATASETS =
      new Factory<PublicDatasets, Genomics.Datasets.List, ListDatasetsResponse>() {
        @Override public PublicDatasets createPaginator(Genomics genomics) {
          return PublicDatasets.create(genomics);
        }
      };

  public static final Factory<Reads, Genomics.Reads.Search, SearchReadsResponse> READS =
      new Factory<Reads, Genomics.Reads.Search, SearchReadsResponse>() {
        @Override public Reads createPaginator(Genomics genomics) {
          return Reads.create(genomics);
        }
      };

  public static final ReadGroupSetsFactory READGROUPSETS =
      new ReadGroupSetsFactory() {
        @Override public ReadGroupSets createPaginator(Genomics genomics) {
          return ReadGroupSets.create(genomics);
        }
      };

  public static final Factory<Variants, Genomics.Variants.Search, SearchVariantsResponse> VARIANTS =
      new Factory<Variants, Genomics.Variants.Search, SearchVariantsResponse>() {
        @Override public Variants createPaginator(Genomics genomics) {
          return Variants.create(genomics);
        }
      };

  public static final
      Factory<Variantsets, Genomics.Variantsets.Search, SearchVariantSetsResponse> VARIANTSETS =
          new Factory<Variantsets, Genomics.Variantsets.Search, SearchVariantSetsResponse>() {
            @Override public Variantsets createPaginator(Genomics genomics) {
              return Variantsets.create(genomics);
            }
          };

  private static GenomicsRequestInitializer<GenomicsRequest<?>> setFieldsInitializer(
      final String fields) {
    return new GenomicsRequestInitializer<GenomicsRequest<?>>() {
          @Override public void initialize(GenomicsRequest<?> search) {
            if (null != fields) {
              search.setFields(fields);
            }
          }
        };
  }

  private final Genomics genomics;

  private long successfulRequests = 0;
  private long attemptedRequests = 0;

  public Paginator(Genomics genomics) {
    this.genomics = genomics;
  }

  abstract C createSearch(A api, B request, Optional<String> pageToken) throws IOException;

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
    return search(request, DEFAULT_INITIALIZER);
  }

  public final <F> F search(B request, Callback<E, ? extends F> callback) throws IOException {
    return search(request, DEFAULT_INITIALIZER, callback);
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
    final A api = getApi(genomics);
    return FluentIterable
        .from(
            new Iterable<Pair>() {
              @Override public Iterator<Pair> iterator() {
                try {
                  return new AbstractSequentialIterator<Pair>(
                          new Pair(createSearch(api, request, Optional.<String>absent()), null)) {
                        @Override protected Pair computeNext(Pair pair) {
                          return Optional.fromNullable(pair.request)
                              .transform(
                                  new Function<C, Pair>() {
                                    @Override public Pair apply(C search) {
                                      try {
                                        D response = search.execute();
                                        successfulRequests += 1;
                                        Optional<String> pageToken =
                                            Optional.fromNullable(getNextPageToken(response));
                                        return new Pair(
                                            pageToken.isPresent()
                                                ? createSearch(api, request, pageToken)
                                                : null,
                                            response);
                                      } catch (IOException e) {
                                        throw new SearchException(e);
                                      }
                                    }
                                  })
                              .orNull();
                        }
                      };
                } catch (IOException e) {
                  throw new SearchException(e);
                }
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
                return Optional.fromNullable(getResponses(response)).or(Collections.<E>emptyList());
              }
            });
  }

  /**
   * An exception safe way of consuming search results. Client code supplies a callback which is
   * used to consume the result stream and accumulate a value, which becomes the value returned from
   * this method.
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
    return search(request, setFieldsInitializer(fields));
  }

  public final <F> F search(B request, final String fields, Callback<E, ? extends F> callback)
      throws IOException {
    return search(request, setFieldsInitializer(fields), callback);
  }

  /**
   * Get the number of successful requests to the API on this paginator.
   * @return the number of successful requests.
   */
  public final long getSuccessfulRequestsCount() {
    return successfulRequests;
  }
}
