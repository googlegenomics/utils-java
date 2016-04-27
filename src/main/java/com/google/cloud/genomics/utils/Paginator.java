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
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;

import com.google.api.services.genomics.Genomics;
import com.google.api.services.genomics.GenomicsRequest;
import com.google.api.services.genomics.model.Annotation;
import com.google.api.services.genomics.model.AnnotationSet;
import com.google.api.services.genomics.model.CallSet;
import com.google.api.services.genomics.model.CoverageBucket;
import com.google.api.services.genomics.model.Dataset;
import com.google.api.services.genomics.model.Job;
import com.google.api.services.genomics.model.ListBasesResponse;
import com.google.api.services.genomics.model.ListCoverageBucketsResponse;
import com.google.api.services.genomics.model.ListDatasetsResponse;
import com.google.api.services.genomics.model.Read;
import com.google.api.services.genomics.model.ReadGroupSet;
import com.google.api.services.genomics.model.Reference;
import com.google.api.services.genomics.model.ReferenceSet;
import com.google.api.services.genomics.model.SearchAnnotationSetsRequest;
import com.google.api.services.genomics.model.SearchAnnotationSetsResponse;
import com.google.api.services.genomics.model.SearchAnnotationsRequest;
import com.google.api.services.genomics.model.SearchAnnotationsResponse;
import com.google.api.services.genomics.model.SearchCallSetsRequest;
import com.google.api.services.genomics.model.SearchCallSetsResponse;
import com.google.api.services.genomics.model.SearchJobsRequest;
import com.google.api.services.genomics.model.SearchJobsResponse;
import com.google.api.services.genomics.model.SearchReadGroupSetsRequest;
import com.google.api.services.genomics.model.SearchReadGroupSetsResponse;
import com.google.api.services.genomics.model.SearchReadsRequest;
import com.google.api.services.genomics.model.SearchReadsResponse;
import com.google.api.services.genomics.model.SearchReferenceSetsRequest;
import com.google.api.services.genomics.model.SearchReferenceSetsResponse;
import com.google.api.services.genomics.model.SearchReferencesRequest;
import com.google.api.services.genomics.model.SearchReferencesResponse;
import com.google.api.services.genomics.model.SearchVariantSetsRequest;
import com.google.api.services.genomics.model.SearchVariantSetsResponse;
import com.google.api.services.genomics.model.SearchVariantsRequest;
import com.google.api.services.genomics.model.SearchVariantsResponse;
import com.google.api.services.genomics.model.Variant;
import com.google.api.services.genomics.model.VariantSet;
import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.google.common.base.Predicate;
import com.google.common.collect.AbstractSequentialIterator;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

/**
 * An abstraction that understands the {@code pageToken} / {@code nextPageToken} protocol for paging
 * results back to the user.
 *
 * <p>The {@link #search(Object, GenomicsRequestInitializer, RetryPolicy)} method can obtain an
 * {@link Iterable} to the objects returned from a search request. Although it is possible to
 * invoke this method directly, the {@code Iterable} that is returned may throw
 * {@link SearchException} during iteration. Client code is responsible for catching this exception
 * where the {@code Iterable} is consumed, unwrapping the underlying {@link IOException}, and
 * handling or rethrowing it.
 * </p>
 *
 * <p>A safer alternative for consuming the results of a search is
 * {@link #search(Object, GenomicsRequestInitializer, Callback, RetryPolicy)}.
 * This method requires the client code to pass in a {@link Callback} object that consumes the
 * search results and will unwrap and rethrow {@link IOException}s that occur during iteration
 * for you.
 * </p>
 *
 * <p>Example usage: Fetching all {@link ReadGroupSet}s in a {@link Dataset}:</p>
 * <pre>
 *{@code
 *Genomics stub = ...;
 *String datasetId = ...;
 *Paginator.Readsets searchReadsets = Paginator.Readsets.create(stub);
 *for (Readset readset =
 *    searchReadsets.search(new SearchReadsetsRequest().setDatasetId(datasetId))) &#123;
 *  // do something with readset
 *&#125;
 *}
 *</pre>
 *
 * @param <ApiT> The API type.
 * @param <RequestT> The request type.
 * @param <RequestSubT> The {@link GenomicsRequest} subtype.
 * @param <ResponseT> The response type.
 * @param <ItemT> The type of object being streamed back to the user.
 */
public abstract class Paginator<ApiT, RequestT, RequestSubT extends GenomicsRequest<ResponseT>, ResponseT, ItemT> {

  /**
   * A callback object for
   * {@link #search(Object, GenomicsRequestInitializer, Callback, RetryPolicy)} that can
   * consume all or part of the results from a search request and accumulate a value, which becomes
   * the value returned from the
   * {@link #search(Object, GenomicsRequestInitializer, Callback, RetryPolicy)} method.
   *
   * @param <EntityT> The type of objects returned from a search
   * @param <AccumulatedT> The type of object to accumulate when consuming search results.
   */
  public interface Callback<EntityT, AccumulatedT> {

    /**
     * Consume the search results and accumulate an object to return.
     *
     * @param responses The search results to consume.
     * @return the accumulated summary value.
     * @throws IOException
     */
    AccumulatedT consumeResponses(Iterable<EntityT> responses) throws IOException;
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
      return new Callsets(genomics);
    }

    private Callsets(Genomics genomics) {
      super(genomics);
    }

    @Override Genomics.Callsets.Search createSearch(Genomics.Callsets api,
        final SearchCallSetsRequest request, Optional<String> pageToken) throws IOException {
      return api.search(pageToken
          .transform(
              new Function<String, SearchCallSetsRequest>() {
                @Override public SearchCallSetsRequest apply(String token) {
                  return request.setPageToken(token);
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
  public static class Datasets extends Paginator<
      Genomics.Datasets,
      Long,
      Genomics.Datasets.List,
      ListDatasetsResponse,
      Dataset> {

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

    @Override Genomics.Datasets.List createSearch(Genomics.Datasets api, Long request,
        Optional<String> pageToken) throws IOException {
      final Genomics.Datasets.List list = api.list().setProjectNumber(request);
      return pageToken
          .transform(
              new Function<String, Genomics.Datasets.List>() {
                @Override public Genomics.Datasets.List apply(String token) {
                  return list.setPageToken(token);
                }
              })
          .or(list);
    }

    @Override Genomics.Datasets getApi(Genomics genomics) {
      return genomics.datasets();
    }

    @Override String getNextPageToken(ListDatasetsResponse response) {
      return response.getNextPageToken();
    }

    @Override Iterable<Dataset> getResponses(ListDatasetsResponse response) {
      return response.getDatasets();
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
      return new Jobs(genomics);
    }

    private Jobs(Genomics genomics) {
      super(genomics);
    }

    @Override Genomics.Jobs.Search createSearch(Genomics.Jobs api, final SearchJobsRequest request,
        Optional<String> pageToken) throws IOException {
      return api.search(pageToken
          .transform(
              new Function<String, SearchJobsRequest>() {
                @Override public SearchJobsRequest apply(String token) {
                  return request.setPageToken(token);
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

    final RequestSubT request;
    final ResponseT response;

    Pair(RequestSubT request, ResponseT response) {
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

    // TODO: When this is supported server-side, these additional fields are no longer required
    private static ImmutableMap<String,String> REQUIRED_STRICT_SHARD_FIELDS =
        ImmutableMap.<String, String>builder()
        .putAll(REQUIRED_FIELDS)
        .put("alignment", ".*\\p{Punct}alignment\\p{Punct}.*")
        .put("position", ".*\\p{Punct}position\\p{Punct}.*")
        .build();
    private final ShardBoundary.Requirement shardBoundary;
    private Predicate<Read> shardPredicate = null;

    /**
     * Static factory method.
     *
     * The reads search API by default returns all reads that overlap the specified range.
     * Ranges are half-open 0-based, so [start,end)
     *
     * Cluster compute jobs attempting to shard the data will see any records that span a shard
     * boundary in both shards. In some cases this might be okay, in others it is not.
     *
     * @param genomics The {@link Genomics} stub.
     * @param shardBoundary Use ShardBoundary.OVERLAPS for the default behavior or use
     *        ShardBoundary.STRICT to ensure that a record does not appear in more than one shard.
     * @return the new paginator.
     */
    public static Reads create(Genomics genomics, ShardBoundary.Requirement shardBoundary) {
      return new Reads(genomics, shardBoundary);
    }

    private Reads(Genomics genomics, ShardBoundary.Requirement shardBoundary) {
      super(genomics);
      this.shardBoundary = shardBoundary;
    }

    @Override Genomics.Reads.Search createSearch(Genomics.Reads api, final SearchReadsRequest request,
        Optional<String> pageToken) throws IOException {

      if(shardBoundary == ShardBoundary.Requirement.STRICT) {
        // TODO: When this is supported server-side, instead verify that request.getIntersectionType
        // will yield a strict shard.
        shardPredicate = new Predicate<Read>() {
          @Override
          public boolean apply(Read read) {
            return read.getAlignment().getPosition().getPosition() >= request.getStart();
          }
        };
      }

      return api.search(pageToken
          .transform(
              new Function<String, SearchReadsRequest>() {
                @Override public SearchReadsRequest apply(String token) {
                  return request.setPageToken(token);
                }
              })
          .or(request));
    }

    @Override
    public GenomicsRequestInitializer<GenomicsRequest<?>> setFieldsInitializer(final String fields) {
      if(shardBoundary == ShardBoundary.Requirement.STRICT) {
        return new GenomicsSearchFieldRequestInitializer(fields, REQUIRED_STRICT_SHARD_FIELDS);
      }
      return new GenomicsSearchFieldRequestInitializer(fields, REQUIRED_FIELDS);
    }

    @Override Genomics.Reads getApi(Genomics genomics) {
      return genomics.reads();
    }

    @Override String getNextPageToken(SearchReadsResponse response) {
      return response.getNextPageToken();
    }

    @Override Iterable<Read> getResponses(SearchReadsResponse response) {
      Iterable<Read> responses = response.getAlignments();
      if(shardPredicate != null && responses != null) {
        return Iterables.filter(responses, shardPredicate);
      }
      return responses;
    }
  }

  /**
   * A {@link Paginator} for the {@code searchAnnotations()} API.
   */
  public static class Annotations extends Paginator<
      Genomics.Annotations,
      SearchAnnotationsRequest,
      Genomics.Annotations.Search,
      SearchAnnotationsResponse,
      Annotation> {

    // TODO: When this is supported server-side, these additional fields are no longer required
    private static ImmutableMap<String,String> REQUIRED_STRICT_SHARD_FIELDS =
        ImmutableMap.<String, String>builder()
        .putAll(REQUIRED_FIELDS)
        .put("position", ".*\\p{Punct}position\\p{Punct}.*")
        .put("start", ".*\\p{Punct}start\\p{Punct}.*")
        .build();
    private final ShardBoundary.Requirement shardBoundary;
    private Predicate<Annotation> shardPredicate = null;

    /**
     * Static factory method.
     *
     * The annotations search API by default returns all annotations that overlap the specified
     * range. Ranges are half-open 0-based, so [start,end)
     *
     * Cluster compute jobs attempting to shard the data will see any records that span a shard
     * boundary in both shards. In some cases this might be okay, in others it is not.
     *
     * @param genomics The {@link Genomics} stub.
     * @param shardBoundary Use ShardBoundary.OVERLAPS for the default behavior or use
     *        ShardBoundary.STRICT to ensure that a record does not appear in more than one shard.
     * @return the new paginator.
     */
    public static Annotations create(Genomics genomics, ShardBoundary.Requirement shardBoundary) {
      return new Annotations(genomics, shardBoundary);
    }

    private Annotations(Genomics genomics, ShardBoundary.Requirement shardBoundary) {
      super(genomics);
      this.shardBoundary = shardBoundary;
    }

    @Override Genomics.Annotations.Search createSearch(Genomics.Annotations api,
        final SearchAnnotationsRequest request, Optional<String> pageToken) throws IOException {

      if(shardBoundary == ShardBoundary.Requirement.STRICT) {
        // TODO: When this is supported server-side, instead verify that request.getIntersectionType
        // will yield a strict shard.
        shardPredicate = new Predicate<Annotation>() {
          @Override
          public boolean apply(Annotation anno) {
            return anno.getPosition().getStart() >= request.getRange().getStart();
          }
        };
      }
      return api.search(pageToken
          .transform(
              new Function<String, SearchAnnotationsRequest>() {
                @Override public SearchAnnotationsRequest apply(String token) {
                  return request.setPageToken(token);
                }
              })
          .or(request));
    }

    @Override
    public GenomicsRequestInitializer<GenomicsRequest<?>> setFieldsInitializer(final String fields) {
      if(shardBoundary == ShardBoundary.Requirement.STRICT) {
        return new GenomicsSearchFieldRequestInitializer(fields, REQUIRED_STRICT_SHARD_FIELDS);
      }
      return new GenomicsSearchFieldRequestInitializer(fields, REQUIRED_FIELDS);
    }

    @Override Genomics.Annotations getApi(Genomics genomics) {
      return genomics.annotations();
    }

    @Override String getNextPageToken(SearchAnnotationsResponse response) {
      return response.getNextPageToken();
    }

    @Override Iterable<Annotation> getResponses(SearchAnnotationsResponse response) {
      Iterable<Annotation> responses = response.getAnnotations();
      if(shardPredicate != null && responses != null) {
        return Iterables.filter(responses, shardPredicate);
      }
      return responses;
    }
  }

  /**
   * A {@link Paginator} for the {@code searchAnnotationSets()} API.
   */
  public static class AnnotationSets extends Paginator<
      Genomics.AnnotationSets,
      SearchAnnotationSetsRequest,
      Genomics.AnnotationSets.Search,
      SearchAnnotationSetsResponse,
      AnnotationSet> {

    /**
     * @param genomics The {@link Genomics} stub.
     * @return the new paginator.
     */
    public static AnnotationSets create(Genomics genomics) {
      return new AnnotationSets(genomics);
    }

    private AnnotationSets(Genomics genomics) {
      super(genomics);
    }

    @Override Genomics.AnnotationSets.Search createSearch(Genomics.AnnotationSets api,
        final SearchAnnotationSetsRequest request, Optional<String> pageToken) throws IOException {
      return api.search(pageToken
          .transform(
              new Function<String, SearchAnnotationSetsRequest>() {
                @Override public SearchAnnotationSetsRequest apply(String token) {
                  return request.setPageToken(token);
                }
              })
          .or(request));
    }

    @Override Genomics.AnnotationSets getApi(Genomics genomics) {
      return genomics.annotationSets();
    }

    @Override String getNextPageToken(SearchAnnotationSetsResponse response) {
      return response.getNextPageToken();
    }

    @Override Iterable<AnnotationSet> getResponses(SearchAnnotationSetsResponse response) {
      return response.getAnnotationSets();
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
        return new Coveragebuckets(genomics);
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
                      String token) {
                    return list.setPageToken(token);
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
                @Override public SearchReadGroupSetsRequest apply(String token) {
                  return request.setPageToken(token);
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

  /**
   * A {@link Paginator} for the {@code searchReferenceSets()} API.
   */
  public static class ReferenceSets extends Paginator<
      Genomics.Referencesets,
      SearchReferenceSetsRequest,
      Genomics.Referencesets.Search,
      SearchReferenceSetsResponse,
      ReferenceSet> {

    /**
     * Static factory method.
     *
     * @param genomics The {@link Genomics} stub.
     * @return the new paginator.
     */
    public static ReferenceSets create(Genomics genomics) {
      return new ReferenceSets(genomics);
    }

    private ReferenceSets(Genomics genomics) {
      super(genomics);
    }

    @Override Genomics.Referencesets.Search createSearch(Genomics.Referencesets api,
        final SearchReferenceSetsRequest request, Optional<String> pageToken) throws IOException {
      return api.search(pageToken
          .transform(
              new Function<String, SearchReferenceSetsRequest>() {
                @Override public SearchReferenceSetsRequest apply(String token) {
                  return request.setPageToken(token);
                }
              })
          .or(request));
    }

    @Override Genomics.Referencesets getApi(Genomics genomics) {
      return genomics.referencesets();
    }

    @Override String getNextPageToken(SearchReferenceSetsResponse response) {
      return response.getNextPageToken();
    }

    @Override Iterable<ReferenceSet> getResponses(SearchReferenceSetsResponse response) {
      return response.getReferenceSets();
    }
  }

  /**
   * A {@link Paginator} for the {@code searchReferences()} API.
   */
  public static class References extends Paginator<
      Genomics.References,
      SearchReferencesRequest,
      Genomics.References.Search,
      SearchReferencesResponse,
      Reference> {

    /**
     * A {@link Paginator} for the {@code bases()} API.
     */
    public static class Bases extends Paginator<
        Genomics.References.Bases,
        String,
        Genomics.References.Bases.List,
        ListBasesResponse,
        String> {

      /**
       * Static factory method.
       *
       * @param genomics The {@link Genomics} stub.
       * @return the new paginator.
       */
      public static Bases create(Genomics genomics) {
        return new Bases(genomics);
      }

      private Bases(Genomics genomics) {
        super(genomics);
      }

      @Override Genomics.References.Bases.List createSearch(
          Genomics.References.Bases api,
          String request, Optional<String> pageToken) throws IOException {
        final Genomics.References.Bases.List list = api.list(request);
        return pageToken
            .transform(
                new Function<String, Genomics.References.Bases.List>() {
                  @Override public Genomics.References.Bases.List apply(
                      String token) {
                    return list.setPageToken(token);
                  }
                })
            .or(list);
      }

      @Override Genomics.References.Bases getApi(Genomics genomics) {
        return genomics.references().bases();
      }

      @Override String getNextPageToken(ListBasesResponse response) {
        return response.getNextPageToken();
      }

      @Override Iterable<String> getResponses(ListBasesResponse response) {
        return Lists.newArrayList(response.getSequence());
      }
    }

    /**
     * Static factory method.
     *
     * @param genomics The {@link Genomics} stub.
     * @return the new paginator.
     */
    public static References create(Genomics genomics) {
      return new References(genomics);
    }

    private References(Genomics genomics) {
      super(genomics);
    }

    @Override Genomics.References.Search createSearch(Genomics.References api,
        final SearchReferencesRequest request, Optional<String> pageToken) throws IOException {
      return api.search(pageToken
          .transform(
              new Function<String, SearchReferencesRequest>() {
                @Override public SearchReferencesRequest apply(String token) {
                  return request.setPageToken(token);
                }
              })
          .or(request));
    }

    @Override Genomics.References getApi(Genomics genomics) {
      return genomics.references();
    }

    @Override String getNextPageToken(SearchReferencesResponse response) {
      return response.getNextPageToken();
    }

    @Override Iterable<Reference> getResponses(SearchReferencesResponse response) {
      return response.getReferences();
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

    // TODO: When this is supported server-side, these additional fields are no longer required
    private static ImmutableMap<String,String> REQUIRED_STRICT_SHARD_FIELDS =
        ImmutableMap.<String, String>builder()
        .putAll(REQUIRED_FIELDS)
        .put("start", ".*\\p{Punct}start\\p{Punct}.*")
        .build();
    private final ShardBoundary.Requirement shardBoundary;
    private Predicate<Variant> shardPredicate = null;

    /**
     * Static factory method.
     *
     * The variants search API by default returns all variants that overlap the specified range.
     * Ranges are half-open 0-based, so [start,end)
     *
     * Cluster compute jobs attempting to shard the data will see any records that span a shard
     * boundary in both shards. In some cases this might be okay, in others it is not.
     *
     * @param genomics The {@link Genomics} stub.
     * @param shardBoundary Use ShardBoundary.OVERLAPS for the default behavior or use
     *        ShardBoundary.STRICT to ensure that a record does not appear in more than one shard.
     * @return the new paginator
     */
    public static Variants create(Genomics genomics, ShardBoundary.Requirement shardBoundary) {
      return new Variants(genomics, shardBoundary);
    }

    private Variants(Genomics genomics, ShardBoundary.Requirement shardBoundary) {
      super(genomics);
      this.shardBoundary = shardBoundary;
    }

    @Override Genomics.Variants.Search createSearch(Genomics.Variants api,
        final SearchVariantsRequest request, Optional<String> pageToken) throws IOException {

      if(shardBoundary == ShardBoundary.Requirement.STRICT) {
        // TODO: When this is supported server-side, instead verify that request.getIntersectionType
        // will yield a strict shard.
        shardPredicate = new Predicate<Variant>() {
          @Override
          public boolean apply(Variant variant) {
            return variant.getStart() >= request.getStart();
          }
        };
      }

      return api.search(pageToken
          .transform(
              new Function<String, SearchVariantsRequest>() {
                @Override public SearchVariantsRequest apply(String token) {
                  return request.setPageToken(token);
                }
              })
          .or(request));
    }

    @Override
    public GenomicsRequestInitializer<GenomicsRequest<?>> setFieldsInitializer(final String fields) {
      if(shardBoundary == ShardBoundary.Requirement.STRICT) {
        return new GenomicsSearchFieldRequestInitializer(fields, REQUIRED_STRICT_SHARD_FIELDS);
      }
      return new GenomicsSearchFieldRequestInitializer(fields, REQUIRED_FIELDS);
    }

    @Override Genomics.Variants getApi(Genomics genomics) {
      return genomics.variants();
    }

    @Override String getNextPageToken(SearchVariantsResponse response) {
      return response.getNextPageToken();
    }

    @Override Iterable<Variant> getResponses(SearchVariantsResponse response) {
      Iterable<Variant> responses = response.getVariants();
      if(shardPredicate != null && responses != null) {
        return Iterables.filter(responses, shardPredicate);
      }
      return responses;
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
      return new Variantsets(genomics);
    }

    private Variantsets(Genomics genomics) {
      super(genomics);
    }

    @Override Genomics.Variantsets.Search createSearch(Genomics.Variantsets api,
        final SearchVariantSetsRequest request, Optional<String> pageToken) throws IOException {
      return api.search(pageToken
          .transform(
              new Function<String, SearchVariantSetsRequest>() {
                @Override public SearchVariantSetsRequest apply(String token) {
                  return request.setPageToken(token);
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

  public static final GenomicsRequestInitializer<GenomicsRequest<?>> DEFAULT_INITIALIZER =
      new GenomicsRequestInitializer<GenomicsRequest<?>>() {
        @Override public void initialize(GenomicsRequest<?> search) {}
      };

  protected static final ImmutableMap<String, String> REQUIRED_FIELDS = ImmutableMap
      .<String, String>builder()
      .put("nextPageToken", "(.+\\p{Punct})?nextPageToken(\\p{Punct}.+)?").
      build();

  public GenomicsRequestInitializer<GenomicsRequest<?>> setFieldsInitializer(final String fields) {
    return new GenomicsSearchFieldRequestInitializer(fields, REQUIRED_FIELDS);
  }

  public static class GenomicsSearchFieldRequestInitializer implements
      GenomicsRequestInitializer<GenomicsRequest<?>> {
    private final String fields;
    private final ImmutableMap<String, String> requiredFields;

    public GenomicsSearchFieldRequestInitializer(String fields,
        ImmutableMap<String, String> requiredFields) {
      this.fields = fields;
      this.requiredFields = requiredFields;
    }

    @Override
    public void initialize(GenomicsRequest<?> search) {
      if (null != fields) {
        Map<String, String> missingFields =
            Maps.filterValues(requiredFields, new Predicate<String>() {
              @Override
              public boolean apply(String input) {
                return !fields.matches(input);
              }
            });
        Preconditions
            .checkArgument(
                missingFields.isEmpty(),
                "Required field(s) missing: '%s' Add this field to the list of fields to be returned in the partial response.",
                Joiner.on(",").join(missingFields.keySet()));
        search.setFields(fields);
      }
    }
  }

  private final Genomics genomics;

  public Paginator(Genomics genomics) {
    this.genomics = genomics;
  }

  abstract RequestSubT createSearch(ApiT api, RequestT request, Optional<String> pageToken) throws IOException;

  abstract ApiT getApi(Genomics genomicsInstance);

  abstract String getNextPageToken(ResponseT response);

  abstract Iterable<ItemT> getResponses(ResponseT response);

  /**
   * Search for objects.
   *
   * @param request The search request.
   * @return the stream of search results.
   */
  public final Iterable<ItemT> search(final RequestT request) {
    return search(request, DEFAULT_INITIALIZER, RetryPolicy.defaultPolicy());
  }

  public final <AccumulatedT> AccumulatedT search(RequestT request, Callback<ItemT, ? extends AccumulatedT> callback) throws IOException {
    return search(request, DEFAULT_INITIALIZER, callback, RetryPolicy.defaultPolicy());
  }

  /**
   * Search for objects. Warning: the returned {@link Iterable} may throw {@link SearchException}
   * during iteration; users are encouraged to call
   * {@link #search(Object, GenomicsRequestInitializer, Callback, RetryPolicy)} instead.
   *
   * @param request The search request.
   * @param initializer The {@link GenomicsRequestInitializer} to initialize requests with.
   * @param retryPolicy A retry policy specifying behavior when a request fails
   *     (usually due to SocketTimeoutExceptions)
   * @return A lazy stream of search results.
   */
  public final Iterable<ItemT> search(
      final RequestT request,
      final GenomicsRequestInitializer<? super RequestSubT> initializer,
      final RetryPolicy retryPolicy) {
    final ApiT api = getApi(genomics);
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
                                  new Function<RequestSubT, Pair>() {
                                    @Override public Pair apply(RequestSubT search) {
                                      try {
                                        initializer.initialize(search);
                                        ResponseT response = retryPolicy.execute(search);
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
            new Function<Pair, ResponseT>() {
              @Override public ResponseT apply(Pair pair) {
                return pair.response;
              }
            })
        .transformAndConcat(
            new Function<ResponseT, Iterable<ItemT>>() {
              @Override public Iterable<ItemT> apply(ResponseT response) {
                return Optional.fromNullable(getResponses(response)).or(Collections.<ItemT>emptyList());
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
   * @param retryPolicy A retry policy specifying behavior when a request fails
   *     (usually due to SocketTimeoutExceptions)
   * @return whatever value {@link Callback#consumeResponses} returned.
   * @throws IOException if an IOException occurred while consuming search results.
   */
  public final <F> F search(
      RequestT request,
      GenomicsRequestInitializer<? super RequestSubT> initializer,
      Callback<ItemT, ? extends F> callback,
      RetryPolicy retryPolicy) throws IOException {
    try {
      return callback.consumeResponses(search(request, initializer, retryPolicy));
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
  public final Iterable<ItemT> search(final RequestT request, final String fields) {
    return search(request, setFieldsInitializer(fields), RetryPolicy.defaultPolicy());
  }

  public final <F> F search(RequestT request, final String fields, Callback<ItemT, ? extends F> callback)
      throws IOException {
    return search(request, setFieldsInitializer(fields), callback, RetryPolicy.defaultPolicy());
  }
}
