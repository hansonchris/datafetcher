package com.chrishanson.datafetcher.models.services.datasources

/**
 * Returns a title determined by the title delegate during after fetch or refresh of items.
 */
class DynamicTitleDataSourceWrapper(wrappedDataSource: DataSource,
    private val titleDelegate: () -> String?) : DataSourceWrapper(wrappedDataSource) {

  override val title: String?
    get() = titleDelegate.invoke()

  override fun fetchNextPage(fetchListener: () -> Unit) {
    super.fetchNextPage {
      // TODO The original Java version notifies a property change. Should DataSource be Observable?
      fetchListener.invoke()
    }
  }

  override fun refresh(refreshListener: () -> Unit) {
    super.refresh {
      // TODO The original Java version notifies a property change. Should DataSource be Observable?
      refreshListener.invoke()
    }
  }

}
