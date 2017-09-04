package com.chrishanson.datafetcher.models.services.datasources

import com.chrishanson.datafetcher.models.data.DataItem

interface DataSource {

  val items: List<DataItem>

  val error: Throwable?

  val title: String?

  val canFetchMore: Boolean

  fun fetchNextPage(fetchListener: () -> Unit)

  fun refresh(refreshListener: () -> Unit)

  fun reset()

}
