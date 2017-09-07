package com.chrishanson.datafetcher.models.services.datasources

import com.chrishanson.datafetcher.models.data.DataItem
import java.util.concurrent.atomic.AtomicInteger

class CompositeDataSourceWrapper(override val title: String?, vararg val dataSources: DataSource):
    DataSource {

  override val items: List<DataItem>
    get() = linkedSetOf<DataItem>().apply { dataSources.forEach { this += it.items } }.toList()

  override val error: Throwable?
    get() = dataSources.firstOrNull { it.error != null }?.error

  override val canFetchMore: Boolean
    get() = dataSources.count { it.canFetchMore } > 0

  override fun fetchNextPage(fetchListener: () -> Unit) {
    MultiDataSourceFetchHelper(*dataSources).fetchNextPage { fetchListener.invoke() }
  }

  override fun refresh(refreshListener: () -> Unit) {
    if (dataSources.isEmpty()) {
      refreshListener.invoke()

      return
    }

    val operationCountdown = AtomicInteger(dataSources.size)

    fun checkCountdown() {
      if (operationCountdown.decrementAndGet() == 0) refreshListener.invoke()
    }

    dataSources.forEach { it.refresh(::checkCountdown) }
  }

  override fun reset() {
    dataSources.forEach { it.reset() }
  }

}
