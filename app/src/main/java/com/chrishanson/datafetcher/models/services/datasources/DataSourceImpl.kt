package com.chrishanson.datafetcher.models.services.datasources

import com.chrishanson.datafetcher.extensions.addCallback
import com.chrishanson.datafetcher.models.data.DataItem
import com.chrishanson.datafetcher.models.services.fetchers.FetchResult
import com.chrishanson.datafetcher.models.services.fetchers.Fetcher
import com.google.common.util.concurrent.ListenableFuture
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class DataSourceImpl(private val fetcher: Fetcher, override val title: String?) : DataSource {

  private val _items = linkedSetOf<DataItem>()
  override val items = _items.toList()

  override var error: Throwable? = null
    private set

  override var canFetchMore: Boolean = false
    private set

  private var fetchingNextPage = false

  private var refreshing = false

  private val fetchListeners = mutableListOf<() -> Unit>()

  private val refreshListeners = mutableListOf<() -> Unit>()

  private var currentFetchFuture: ListenableFuture<FetchResult>? = null

  private var currentRefreshFuture: ListenableFuture<FetchResult>? = null

  private val executor: ExecutorService
    get() = Executors.newSingleThreadExecutor()

  @Synchronized
  override fun fetchNextPage(fetchListener: () -> Unit) {
    // If we're currently refreshing, we can't fetch another page, so add this listener to the
    // refresh listeners so that it's called when the refresh completes.
    if (refreshing) {
      refresh(fetchListener)

      return
    }

    fetchListeners += fetchListener

    fun onDone() {
      synchronized(this@DataSourceImpl) {
        currentFetchFuture = null
        fetchingNextPage = false
        callFetchListeners()
      }
    }

    if (!fetchingNextPage) {
      fetchingNextPage = true

      fetcher.fetchPage().let { future ->
        currentFetchFuture = future
        future.addCallback(
            onSuccess = { result ->
              handleFetchResult(result)
              onDone()
            },
            onFailure = { throwable ->
              error = throwable
              onDone()
            }
        )
      }
    }
  }

  @Synchronized
  override fun refresh(refreshListener: () -> Unit) {
    // If we're currently fetching, cancel that operation and call those listeners
    if (cancelFetchFuture()) callFetchListeners()

    refreshListeners += refreshListener

    fun onDone() {
      synchronized(this@DataSourceImpl) {
        currentRefreshFuture = null
        refreshing = false
        callRefreshListeners()
      }
    }

    if (!refreshing) {
      refreshing = true

      fetcher.refresh().let { future ->
        currentRefreshFuture = future
        future.addCallback(
            onSuccess = { result ->
              canFetchMore = true
              _items.clear()
              handleFetchResult(result)
              onDone()
            },
            onFailure = { throwable ->
              error = throwable
              onDone()
            }
        )
      }
    }
  }

  @Synchronized
  override fun reset() {
    if (cancelFetchFuture()) callFetchListeners()
    if (cancelRefreshFuture()) callRefreshListeners()
    canFetchMore = true
    _items.clear()
    refreshing = false
    fetchingNextPage = false
    refreshListeners.clear()
    fetchListeners.clear()
    fetcher.reset()
  }

  private fun handleFetchResult(fetchResult: FetchResult?) {
    fetchResult?.let {
      _items += it.dataItems
      error = null
      canFetchMore = !it.isLastPage
    }
  }

  @Synchronized
  private fun callFetchListeners() {
    fetchListeners.forEach { executor.submit(it) }
    fetchListeners.clear()
  }

  @Synchronized
  private fun callRefreshListeners() {
    refreshListeners.forEach{ executor.submit(it) }
    refreshListeners.clear()
  }

  private fun cancelFetchFuture(): Boolean {
    currentFetchFuture?.let {
      it.cancel(true)
      currentFetchFuture = null

      return true
    } ?: return false
  }

  private fun cancelRefreshFuture(): Boolean {
    currentRefreshFuture?.let {
      it.cancel(true)
      currentRefreshFuture = null

      return true
    } ?: return false
  }

}
