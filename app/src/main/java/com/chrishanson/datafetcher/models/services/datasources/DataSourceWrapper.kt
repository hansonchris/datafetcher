package com.chrishanson.datafetcher.models.services.datasources

import com.chrishanson.datafetcher.models.data.DataItem

/**
 * Provides a default implementation for all properties and functions. Concrete implementations are
 * free to override as needed.
 */
abstract class DataSourceWrapper(private val wrappedDataSource: DataSource) : DataSource {

  override val items: List<DataItem>
    get() = wrappedDataSource.items

  override val error: Throwable?
    get() = wrappedDataSource.error

  override val title: String?
    get() = wrappedDataSource.title

  override val canFetchMore: Boolean
    get() = wrappedDataSource.canFetchMore

  override fun fetchNextPage(fetchListener: () -> Unit) {
    wrappedDataSource.fetchNextPage(fetchListener)
  }

  override fun refresh(refreshListener: () -> Unit) {
    wrappedDataSource.refresh(refreshListener)
  }

  override fun reset() {
    wrappedDataSource.reset()
  }

}
