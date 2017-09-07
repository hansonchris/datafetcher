package com.chrishanson.datafetcher.models.services.datasources

import com.chrishanson.datafetcher.models.data.DataItem

class FixedSizeDataSourceWrapper(wrappedDataSource: DataSource, private val maxSize: Int) :
    DataSourceWrapper(wrappedDataSource) {

  override val items: List<DataItem>
    get() = super.items.let { if (it.size > maxSize) it.subList(0, maxSize) else it }

  override val canFetchMore: Boolean
    get() = if (items.size == maxSize) false else super.canFetchMore

}
