package com.chrishanson.datafetcher.models.services.datasources

import com.chrishanson.datafetcher.models.services.fetchers.FetchResult

class MultiDataSourceFetchHelper(vararg val dataSources: DataSource) {

  private val dataSourcesFetched = mutableSetOf<DataSource>()

  fun fetchNextPage(callback: (FetchResult?) -> Unit) {
    if (dataSources.isEmpty()) {
      callback.invoke(null)
    } else {
      fetchNextPage(callback, mutableListOf())
    }
  }

  fun reset() {
    dataSourcesFetched.clear()
  }

  // Original has a getter for browse data sources fetched. What is this used for? If we need it, return immutable list

  private fun fetchNextPage(callback: (FetchResult?) -> Unit,
      dataSourcesToExclude: MutableList<DataSource>) {
    if (dataSourcesToExclude.containsAll(dataSources.toList())) {
      callback.invoke(null)

      return
    }

    var fetched = false

    dataSources.forEach { dataSource ->
      dataSourcesFetched += dataSource

      if (!dataSource.canFetchMore || dataSourcesToExclude.contains(dataSource)) return // continues the loop

      val itemsBeforeFetch = dataSource.items
      fetched = true
      dataSource.fetchNextPage {
        val itemsAfterFetch = dataSource.items.toMutableList()
        if (itemsBeforeFetch.size == itemsAfterFetch.size) {
          // This data source thought that it could fetch more, but was unable to actually get more,
          // so add it to the list of exclusions and make a recursive call.
          dataSourcesToExclude += dataSource
          fetchNextPage(callback, dataSourcesToExclude)
        } else {
          // This data source was able to fetch items, so we're done.
          itemsAfterFetch.removeAll(itemsBeforeFetch)
          callback.invoke(FetchResult(dataSource, itemsAfterFetch, !dataSource.canFetchMore))
        }
      }

      return@forEach
    }

    if (!fetched) callback.invoke(null)
  }

}
