package com.chrishanson.datafetcher.common

import java.util.concurrent.Callable

class ListenableFutureCallable<V> : Callable<V> {

  var payload: V? = null

  override fun call(): V {
    return payload!!
  }

}
