/*
 * Copyright (C) 2015 Google Inc.
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
package com.google.cloud.genomics.utils.grpc;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.api.client.util.BackOff;
import com.google.api.client.util.ExponentialBackOff;
import com.google.cloud.genomics.utils.GenomicsFactory;
import com.google.cloud.genomics.utils.ShardBoundary;
import com.google.common.base.Predicate;
import com.google.common.collect.ForwardingIterator;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

/**
 * An iterator for streaming genomic data via gRPC with shard boundary semantics.
 * 
 * Includes complex retry logic to upon failure resume the stream at the last known good start
 * position without returning duplicate data.
 * 
 * @param <A> Streaming request type.
 * @param <B> Streaming response type.
 * @param <C> Genomic data type returned by stream.
 * @param <D> Blocking stub type.
 */
public abstract class GenomicsStreamIterator<A, B, C, D extends io.grpc.stub.AbstractStub<D>> extends ForwardingIterator<B> {
  private static final Logger LOG = Logger.getLogger(GenomicsStreamIterator.class.getName());
  private final ExponentialBackOff backoff;
  protected final D stub;
  protected final GenomicsChannel genomicsChannel;
  protected final A originalRequest;

  // For client-side enforcement of a strict shard boundary.
  protected Predicate<C> shardPredicate;

  // Stateful members used to facilitate complex retry behavior for gRPC streams.
  private Iterator<B> delegate;
  private C lastSuccessfulDataItem;
  private String idSentinel;

  /**
   * Create a stream iterator that can enforce shard boundary semantics and perform retries.
   * 
   * @param request The request for the shard of data.
   * @param auth The OfflineAuth to use for the request.
   * @param shardBoundary The shard boundary semantics to enforce.
   * @param fields Which fields to include in a partial response or null for all. NOT YET
   *        IMPLEMENTED.
   * @throws IOException
   * @throws GeneralSecurityException
   */
  public GenomicsStreamIterator(A request, GenomicsFactory.OfflineAuth auth,
      ShardBoundary.Requirement shardBoundary, String fields) throws IOException,
      GeneralSecurityException {
    this.originalRequest = request;
    genomicsChannel = GenomicsChannel.fromOfflineAuth(auth);
    stub = createStub(genomicsChannel);

    // Using default backoff settings. For details, see
    // https://developers.google.com/api-client-library/java/google-http-java-client/reference/1.19.0/com/google/api/client/util/ExponentialBackOff
    backoff = new ExponentialBackOff.Builder().build();

    // RETRY STATE: Initialize settings.
    delegate = createIterator(originalRequest);
    lastSuccessfulDataItem = null;
    idSentinel = null;
  }

  abstract D createStub(GenomicsChannel genomicsChannel);

  abstract Iterator<B> createIteratorFromStub(A request);

  abstract long getRequestStart(A streamRequest);

  abstract long getDataItemStart(C dataItem);

  abstract String getDataItemId(C dataItem);

  abstract A getRevisedRequest(long updatedStart);

  abstract List<C> getDataList(B response);

  abstract B setDataList(B response, Iterable<C> dataList);

  private Iterator<B> createIterator(A request) {
    while (true) {
      try {
        return createIteratorFromStub(request);
      } catch (Exception e) {
        if (shouldRetryNow()) {
          LOG.log(Level.WARNING, "Retrying after failure to create iterator", e);
        } else {
          LOG.log(Level.WARNING, "All retries to create iterator consumed, re-throwing exception",
              e);
          throw e;
        }
      }
    }
  }

  private boolean shouldRetryNow() {
    long backOffMillis;
    try {
      backOffMillis = backoff.nextBackOffMillis();
    } catch (IOException e1) {
      // Something strange happened, just give up.
      backOffMillis = BackOff.STOP;
    }

    if (backOffMillis == BackOff.STOP) {
      backoff.reset();
      return false;
    }

    try {
      Thread.sleep(backOffMillis);
    } catch (InterruptedException e) {
      LOG.log(Level.WARNING, "Backoff sleep interrupted", e);
    }
    return true;
  }

  @Override
  protected Iterator<B> delegate() {
    return delegate;
  }

  /**
   * @see java.util.Iterator#hasNext()
   */
  public boolean hasNext() {
    boolean hasNext;
    while (true) {
      try {
        hasNext = delegate.hasNext();
        break;
      } catch (Exception e) {
        if (shouldRetryNow()) {
          LOG.log(Level.WARNING, "Retrying after failing to get next item from stream: ", e);
          setStreamStateForRetry();
        } else {
          LOG.log(Level.WARNING, "All retries to get next item from stream consumed, throwing: ", e);
          genomicsChannel.shutdownNow();
          throw e;
        }
      }
    }
    if (!hasNext) {
      genomicsChannel.shutdownNow();
    }
    return hasNext;
  }

  private void setStreamStateForRetry() {
    if (null == lastSuccessfulDataItem) {
      // We have never returned any data. No need to set up state needed to filter previously
      // returned results.
      return;
    }

    if (getRequestStart(originalRequest) < getDataItemStart(lastSuccessfulDataItem)) {
      // Create a new iterator at the revised start position.
      delegate = createIterator(getRevisedRequest(getDataItemStart(lastSuccessfulDataItem)));
    } else {
      // The point at which the retry occurred was still within data overlapping the start of our
      // original request but not beyond it yet.
      delegate = createIterator(originalRequest);
    }

    // RETRY STATE: Enable the filtering of repeated data in next().
    idSentinel = getDataItemId(lastSuccessfulDataItem);
  }

  /**
   * @see java.util.Iterator#next()
   */
  public B next() {
    B response = delegate.next();
    // TODO: Its more clean conceptually to do the same thing for all responses, but this could be a
    // place where we're wasting a lot of time rebuilding response objects when nothing has actually
    // changed.
    return setDataList(response, enforceShardPredicate(removeRepeatedData(getDataList(response))));
  }

  private List<C> removeRepeatedData(List<C> dataList) {
    List<C> filteredDataList = null;
    if (null == idSentinel) {
      filteredDataList = dataList;
    } else {
      // Filter out previously returned data items.
      filteredDataList = Lists.newArrayList();
      boolean sentinelFound = false;
      for (C dataItem : dataList) {
        if (sentinelFound) {
          filteredDataList.add(dataItem);
        } else {
          if (idSentinel.equals(getDataItemId(dataItem))) {
            // RETRY STATE: We're at the end of the repeated data. Unset the sentinel and proceed.
            idSentinel = null;
            sentinelFound = true;
          }
        }
      }
    }
    // RETRY STATE: Keep our last successfully returned data item in memory, just in case we need to
    // retry.
    if (filteredDataList.size() > 0) {
      lastSuccessfulDataItem = filteredDataList.get(filteredDataList.size() - 1);
    }
    return filteredDataList;
  }

  private Iterable<C> enforceShardPredicate(Iterable<C> dataList) {
    if (null == shardPredicate) {
      return dataList;
    }
    // DEV NOTE: right now there is only one possible predicate and it only matters for
    // the beginning of the shard. An optimization would be to get rid of the predicate once
    // we are beyond that boundary. BUT this predicate could be something more complicated in
    // the future (e.g., enforce a shard boundary AND only return SNPs, etc...).
    return Iterables.filter(dataList, shardPredicate);
  }
}
