package com.chrishanson.datafetcher.models.services.datasources

import com.chrishanson.datafetcher.models.data.DataItem

class ItemMutatorDataSourceWrapper(wrappedDataSource: DataSource,
    private val mutator: (DataItem) -> Unit) : DataSourceWrapper(wrappedDataSource) {

  override val items: List<DataItem>
    get() = super.items.apply { forEach{ mutator.invoke(it) } }

}
