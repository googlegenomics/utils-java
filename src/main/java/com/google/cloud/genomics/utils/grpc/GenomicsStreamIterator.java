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

import io.grpc.ManagedChannel;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.api.client.util.BackOff;
import com.google.api.client.util.ExponentialBackOff;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

/**
 * An iterator for streaming genomic data via gRPC with support for retries.
 * 
 * Includes complex retry logic to upon failure resume the stream at the last known good start
 * position without returning duplicate data.
 * 
 * TODO: refactor this further to simplify the generic signature.
 * 
 * @param <RequestT> Streaming request type.
 * @param <ResponseT> Streaming response type.
 * @param <ItemT> Genomic data type returned by stream.
 * @param <StubT> Blocking stub type.
 */
public abstract class GenomicsStreamIterator<RequestT, ResponseT, ItemT, StubT extends io.grpc.stub.AbstractStub<StubT>>
    implements Iterator<ResponseT> {
  private static final Logger LOG = Logger.getLogger(GenomicsStreamIterator.class.getName());

  protected final ManagedChannel genomicsChannel;
  protected final Predicate<ItemT> shardPredicate;
  protected final StubT stub;
  protected final RequestT originalRequest;

  protected ExponentialBackOff backoff;

  // Stateful members used to facilitate complex retry behavior for gRPC streams.
  private Iterator<ResponseT> delegate;
  private ItemT lastSuccessfulDataItem;
  private String idSentinel;

  /**
   * Create a stream iterator that will filter shard data using the predicate, if supplied.
   * 
   * @param channel The channel.
   * @param request The request for the shard of data.
   * @param fields Which fields to include in a partial response or null for all. NOT YET
   *        IMPLEMENTED.
   * @param shardPredicate A predicate used to client-side filter results returned (e.g., enforce a
   *        shard boundary and/or limit to SNPs only) or null for no filtering.
   */

  protected GenomicsStreamIterator(ManagedChannel channel, RequestT request, String fields,
      Predicate<ItemT> shardPredicate) {
    this.originalRequest = request;
    this.shardPredicate = shardPredicate;
    this.genomicsChannel = channel;
    stub = createStub(genomicsChannel);

    // Using default backoff settings. For details, see
    // https://developers.google.com/api-client-library/java/google-http-java-client/reference/1.19.0/com/google/api/client/util/ExponentialBackOff
    backoff = new ExponentialBackOff.Builder().build();

    // RETRY STATE: Initialize settings.
    delegate = createIterator(originalRequest);
    lastSuccessfulDataItem = null;
    idSentinel = null;
  }

  abstract StubT createStub(ManagedChannel genomicsChannel);

  abstract Iterator<ResponseT> createIteratorFromStub(RequestT request);

  abstract long getRequestStart(RequestT streamRequest);

  abstract long getDataItemStart(ItemT dataItem);

  abstract String getDataItemId(ItemT dataItem);

  abstract RequestT getRevisedRequest(long updatedStart);

  abstract List<ItemT> getDataList(ResponseT response);

  abstract ResponseT buildResponse(ResponseT response, Iterable<ItemT> dataList);

  private Iterator<ResponseT> createIterator(RequestT request) {
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
      delegate = createIterator(originalRequest);
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
  public ResponseT next() {
    ResponseT response = delegate.next();
    // TODO: Its more clean conceptually to do the same thing for all responses, but this could be a
    // place where we're wasting a lot of time rebuilding response objects when nothing has actually
    // changed.
    return buildResponse(response, enforceShardPredicate(removeRepeatedData(getDataList(response))));
  }

  private List<ItemT> removeRepeatedData(List<ItemT> dataList) {
    List<ItemT> filteredDataList = null;
    if (null == idSentinel) {
      filteredDataList = dataList;
    } else {
      // Filter out previously returned data items.
      filteredDataList = Lists.newArrayList();
      boolean sentinelFound = false;
      for (ItemT dataItem : dataList) {
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

  private Iterable<ItemT> enforceShardPredicate(Iterable<ItemT> dataList) {
    if (null == shardPredicate) {
      return dataList;
    }
    return Iterables.filter(dataList, shardPredicate);
  }

  /**
   * @see java.util.Iterator#remove()
   */
  @Override
  public void remove() {
    delegate.remove();
  }
}
