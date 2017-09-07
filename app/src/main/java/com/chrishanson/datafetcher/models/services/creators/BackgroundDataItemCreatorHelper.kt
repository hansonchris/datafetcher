package com.chrishanson.datafetcher.models.services.creators

import com.chrishanson.datafetcher.common.ListenableFutureCallable
import com.chrishanson.datafetcher.extensions.addCallback
import com.chrishanson.datafetcher.models.data.DataItem
import com.google.common.util.concurrent.ListenableFuture
import com.google.common.util.concurrent.ListenableFutureTask
import java.util.concurrent.atomic.AtomicInteger

class BackgroundDataItemCreatorHelper<T> {

  fun create(creator: DataItemCreator<T>, sources: List<T>): ListenableFuture<List<DataItem>> {
    val callable = ListenableFutureCallable<List<DataItem>>()
    val future = ListenableFutureTask.create(callable)

    if (sources.isEmpty()) {
      callable.payload = emptyList()
      future.run()
    }

    val operationCountdown = AtomicInteger(sources.size)
    val sourceToItemMap = mutableMapOf<T, DataItem>()

    fun checkCountdown() {
      if (!future.isCancelled) {
        if (operationCountdown.decrementAndGet() == 0) {
          val items = mutableListOf<DataItem>()

          // This uses a map to contain the items as they're created, but then we defer back to the
          // list of sources so that we maintain original ordering. This is necessary because the
          // operations were asynchronous, so we can't rely on them completing in the desired order.
          sources.forEach { sourceToItemMap[it]?.let { items += it } }

          callable.payload = items
          future.run()
        }
      }
    }

    sources.forEach { source ->
      creator.create(source).addCallback(
          onSuccess = { result ->
            result?.let { sourceToItemMap.put(source, it) }
            checkCountdown()
          },
          onFailure = { throwable -> checkCountdown() }
      )
    }

    return future
  }

}
