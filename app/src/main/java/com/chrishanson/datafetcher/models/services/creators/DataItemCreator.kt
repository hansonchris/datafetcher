package com.chrishanson.datafetcher.models.services.creators

import com.chrishanson.datafetcher.models.data.DataItem
import com.google.common.util.concurrent.ListenableFuture

interface DataItemCreator<T> {

  fun create(source: T): ListenableFuture<DataItem>

}
