package com.chrishanson.datafetcher.models.services.fetchers

import com.google.common.util.concurrent.ListenableFuture

interface Fetcher {

  // TODO research how to do Javadoc in Kotlin. For example, make {@link} work.

  /**
   * Fetches the next page if the fetcher supports pagination, otherwise fetches all results in a single page.
   *
   * @return a listenable future which contains the {@link FetchResult}
   */
  fun fetchPage(): ListenableFuture<FetchResult>

  /**
   * Redoes any previous {@link #fetchPage()} requests. If no previous {@link #fetchPage()} requests were made, or if
   * this fetcher was {@link #reset()}, then the {@link FetchResult} will be empty.
   *
   * <p>
   *  Example 1: given the following:
   *  <ul>
   *    <li>This fetcher supports pagination</li>
   *    <li>A single call to {@link #fetchPage()} was made previously</li>
   *    <li>The limit was 20 items</li>
   *    <li>20 items were returned in that request</li>
   *  </ul>
   * This will redo that request, returning the first 20 items. If the data has not changed, then this should be the same
   * as the previous result from {@link #fetchPage()}.
   * </p>
   *
   * <p>
   *  Example 2: given the following:
   *  <ul>
   *    <li>This fetcher supports pagination</li>
   *    <li>A single call to {@link #fetchPage()} was made previously</li>
   *    <li>The limit was 20 items</li>
   *    <li>Less than 20 items were returned in that request</li>
   *  </ul>
   * This will redo that request, returning at most the first 20 items. If the data has not changed, then this should be
   * the same as the previous result from {@link #fetchPage()}.
   * </p>
   *
   * <p>
   *  Example 3: given the following:
   *  <ul>
   *    <li>This fetcher supports pagination</li>
   *    <li>Multiple calls to {@link #fetchPage()} were made previously</li>
   *    <li>The limit was 20 items per request</li>
   *  </ul>
   * This will make a request for the total number of items that were fetched across all previous {@link #fetchPage()}
   * requests. If the data has not changed, then this should be the same as the combination of the previous results from
   * {@link #fetchPage()}.
   * </p>
   *
   * <p>
   *  Example 4: given the following:
   *  <ul>
   *    <li>This fetcher <i>does not</i> support pagination</li>
   *    <li>One or more calls to {@link #fetchPage()} were made previously</li>
   *  </ul>
   * This will make a request for all items. If the data has not changed, then this should be the same as the first call
   * to {@link #fetchPage()}.
   * </p>
   *
   * <p>
   *  Example 5: given the following:
   *  <ul>
   *    <li>Zero calls to {@link #fetchPage()} were made previously</li>
   *  </ul>
   * This will return an empty {@link FetchResult}.
   * </p>
   *
   * @return a listenable future which contains the {@link FetchResult}
   */
  fun refresh(): ListenableFuture<FetchResult>

  /**
   * Resets any internal state, including whether a previous {@link #fetchPage()} request was made.
   */
  fun reset()

}
