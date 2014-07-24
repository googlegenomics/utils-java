package com.google.cloud.genomics.utils;

import com.google.api.client.googleapis.services.AbstractGoogleClientRequest;
import com.google.api.services.genomics.Genomics;
import com.google.api.services.genomics.Genomics.Callsets;
import com.google.api.services.genomics.Genomics.Jobs;
import com.google.api.services.genomics.Genomics.Reads;
import com.google.api.services.genomics.Genomics.Readsets;
import com.google.api.services.genomics.Genomics.Variants;
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
import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.base.Supplier;
import com.google.common.collect.AbstractSequentialIterator;
import com.google.common.collect.Iterators;

import java.io.IOException;
import java.util.Iterator;

/**
 * A pagination abstraction that understands the {@code pageToken} / {@code nextPageToken} protocol
 * that the genomics API uses.

 * @param <Request> The request object type
 * @param <Response> The response object type
 */
public abstract class Pager<Request, Response> {

  private abstract static class AbstractPager<Api, Request, Response>
      extends Pager<Request, Response> {

    private class Pair {

      final Request request;
      final Response response;

      Pair(Request request, Response response) {
        this.request = request;
        this.response = response;
      }
    }

    private final Api api;

    private final Function<Request, Pair> createPair =
        new Function<Request, Pair>() {

          @Override public Pair apply(final Request request) {
            Response response = send(request);
            return new Pair(
                Optional.fromNullable(getNextPageToken(response))
                    .transform(
                        new Function<String, Request>() {
                          @Override public Request apply(String pageToken) {
                            setPageToken(request, pageToken);
                            return request;
                          }
                        })
                    .orNull(),
                response);
          }

          private Response send(Request request) {
            for (
                RetryPolicy<? super Request> retryPolicy = AbstractPager.this.retryPolicy.get();
                true;) {
              try {
                return search(api, request).execute();
              } catch (IOException e) {
                if (!retryPolicy.retryRequest(request, e)) {
                  throw new RuntimeIOException(e);
                }
              }
            }
          }
        };

    private final Function<Pair, Response> getResponse =
        new Function<Pair, Response>() {
          @Override public Response apply(Pair pair) {
            return pair.response;
          }
        };

    private final Supplier<? extends RetryPolicy<? super Request>> retryPolicy;

    AbstractPager(Api api, Supplier<? extends RetryPolicy<? super Request>> retryPolicy) {
      this.api = api;
      this.retryPolicy = retryPolicy;
    }

    @Override public final Iterator<Response> fetchAll(Request request) {
      Iterator<Pair> iterator =
          new AbstractSequentialIterator<Pair>(new Pair(request, null)) {
            @Override protected Pair computeNext(Pair pair) {
              return Optional.fromNullable(pair.request).transform(createPair).orNull();
            }
          };
      Iterators.advance(iterator, 1);
      return Iterators.transform(iterator, getResponse);
    }

    abstract String getNextPageToken(Response response);

    abstract AbstractGoogleClientRequest<Response> search(Api api, Request request)
        throws IOException;

    abstract void setPageToken(Request request, String pageToken);
  }

  /**
   * A {@link RuntimeException} that wraps {@link IOException}s that are thrown while the
   * pager lazily iterates over the stream of responses from the API.
   */
  public static class RuntimeIOException extends RuntimeException {

    RuntimeIOException(IOException cause) {
      super(cause);
    }

    @Override
    public synchronized IOException getCause() {
      return (IOException) super.getCause();
    }
  }

  /**
   * A {@code Pager} for the {@code searchCallsets} API
   *
   * @param genomics The {@link Genomics} stub.
   * @param retryPolicy The {@link RetryPolicy} to use.
   * @return The new {@code Pager}
   */
  public static final Pager<SearchCallsetsRequest, SearchCallsetsResponse> searchCallsets(
      Genomics genomics,
      Supplier<? extends RetryPolicy<? super SearchCallsetsRequest>> retryPolicy) {
    return new AbstractPager<Callsets, SearchCallsetsRequest, SearchCallsetsResponse>(
            genomics.callsets(), retryPolicy) {

          @Override String getNextPageToken(SearchCallsetsResponse response) {
            return response.getNextPageToken();
          }

          @Override AbstractGoogleClientRequest<SearchCallsetsResponse> search(
              Callsets callsets, SearchCallsetsRequest request) throws IOException {
            return callsets.search(request);
          }

          @Override void setPageToken(SearchCallsetsRequest request, String pageToken) {
            request.setPageToken(pageToken);
          }
        };
  }

  /**
   * A {@code Pager} for the {@code searchJobs} API
   *
   * @param genomics The {@link Genomics} stub.
   * @param retryPolicy The {@link RetryPolicy} to use.
   * @return The new {@code Pager}
   */
  public static final Pager<SearchJobsRequest, SearchJobsResponse> searchJobs(
      Genomics genomics,
      Supplier<? extends RetryPolicy<? super SearchJobsRequest>> retryPolicy) {
    return new AbstractPager<Jobs, SearchJobsRequest, SearchJobsResponse>(
            genomics.jobs(), retryPolicy) {

          @Override String getNextPageToken(SearchJobsResponse response) {
            return response.getNextPageToken();
          }

          @Override AbstractGoogleClientRequest<SearchJobsResponse> search(
              Jobs jobs, SearchJobsRequest request) throws IOException {
            return jobs.search(request);
          }

          @Override void setPageToken(SearchJobsRequest request, String pageToken) {
            request.setPageToken(pageToken);
          }
        };
  }

  /**
   * A {@code Pager} for the {@code searchReads} API
   *
   * @param genomics The {@link Genomics} stub.
   * @param retryPolicy The {@link RetryPolicy} to use.
   * @return The new {@code Pager}
   */
  public static final Pager<SearchReadsRequest, SearchReadsResponse> searchReads(
      Genomics genomics,
      Supplier<? extends RetryPolicy<? super SearchReadsRequest>> retryPolicy) {
    return new AbstractPager<Reads, SearchReadsRequest, SearchReadsResponse>(
            genomics.reads(), retryPolicy) {

          @Override String getNextPageToken(SearchReadsResponse response) {
            return response.getNextPageToken();
          }

          @Override AbstractGoogleClientRequest<SearchReadsResponse> search(
              Reads reads, SearchReadsRequest request) throws IOException {
            return reads.search(request);
          }

          @Override void setPageToken(SearchReadsRequest request, String pageToken) {
            request.setPageToken(pageToken);
          }
        };
  }

  /**
   * A {@code Pager} for the {@code searchReadsets} API
   *
   * @param genomics The {@link Genomics} stub.
   * @param retryPolicy The {@link RetryPolicy} to use.
   * @return The new {@code Pager}
   */
  public static final Pager<SearchReadsetsRequest, SearchReadsetsResponse> searchReadsets(
      Genomics genomics,
      Supplier<? extends RetryPolicy<? super SearchReadsetsRequest>> retryPolicy) {
    return new AbstractPager<Readsets, SearchReadsetsRequest, SearchReadsetsResponse>(
            genomics.readsets(), retryPolicy) {

          @Override String getNextPageToken(SearchReadsetsResponse response) {
            return response.getNextPageToken();
          }

          @Override AbstractGoogleClientRequest<SearchReadsetsResponse> search(
              Readsets readsets, SearchReadsetsRequest request) throws IOException {
            return readsets.search(request);
          }

          @Override void setPageToken(SearchReadsetsRequest request, String pageToken) {
            request.setPageToken(pageToken);
          }
        };
  }

  /**
   * A {@code Pager} for the {@code searchVariants} API
   *
   * @param genomics The {@link Genomics} stub.
   * @param retryPolicy The {@link RetryPolicy} to use.
   * @return The new {@code Pager}
   */
  public static final Pager<SearchVariantsRequest, SearchVariantsResponse> searchVariants(
      Genomics genomics,
      Supplier<? extends RetryPolicy<? super SearchVariantsRequest>> retryPolicy) {
    return new AbstractPager<Variants, SearchVariantsRequest, SearchVariantsResponse>(
            genomics.variants(), retryPolicy) {

          @Override String getNextPageToken(SearchVariantsResponse response) {
            return response.getNextPageToken();
          }

          @Override AbstractGoogleClientRequest<SearchVariantsResponse> search(
              Variants variants, SearchVariantsRequest request) throws IOException {
            return variants.search(request);
          }

          @Override void setPageToken(SearchVariantsRequest request, String pageToken) {
            request.setPageToken(pageToken);
          }
        };
  }

  /**
   * Return a lazy stream of responses for the given initial request. Note: if an
   * {@link IOException} is thrown during iteration and the request is not retried, then it will
   * manifest itself as a {@link RuntimeIOException}. The code that consumes the iterator should
   * catch {@code RuntimeIOException} and unwrap it to retrieve the underlying {@link IOException},
   * and handle that exception, or rethrow it. It is a bug to all {@link RuntimeIOException} to
   * propagate.
   *
   * @param request The inital request that starts the stream of responses
   * @return An {@link Iterator} to the stream of responses
   */
  public abstract Iterator<Response> fetchAll(Request request);
}
