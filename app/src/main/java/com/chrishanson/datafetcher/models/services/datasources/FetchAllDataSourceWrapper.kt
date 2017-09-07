package com.chrishanson.datafetcher.models.services.datasources

class FetchAllDataSourceWrapper(wrappedDataSource: DataSource) : DataSourceWrapper(wrappedDataSource) {

  override fun fetchNextPage(fetchListener: () -> Unit) {
    if (canFetchMore) {
      super.fetchNextPage {
        fetchNextPage(fetchListener)
      }
    } else {
      fetchListener.invoke()
    }
  }

}
