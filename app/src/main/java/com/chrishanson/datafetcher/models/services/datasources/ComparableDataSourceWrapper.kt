package com.chrishanson.datafetcher.models.services.datasources

import com.chrishanson.datafetcher.models.data.DataItem

class ComparableDataSourceWrapper(wrappedDataSource: DataSource,
    private val comparator: Comparator<DataItem>) : DataSourceWrapper(wrappedDataSource) {

  override val items: List<DataItem>
    get() = sortedSetOf(comparator, *super.items.toTypedArray()).toList()

}
