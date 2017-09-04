package com.chrishanson.datafetcher.extensions

import com.google.common.util.concurrent.FutureCallback
import com.google.common.util.concurrent.Futures
import com.google.common.util.concurrent.ListenableFuture
import java.util.concurrent.Executors

fun <V> ListenableFuture<V>.addCallback(onSuccess: (V?) -> Unit, onFailure: (Throwable?) -> Unit) {
  Futures.addCallback(this, object : FutureCallback<V> {
    override fun onSuccess(result: V?) {
      onSuccess.invoke(result)
    }

    override fun onFailure(t: Throwable?) {
      onFailure.invoke(t)
    }

  }, Executors.newSingleThreadExecutor())
}
