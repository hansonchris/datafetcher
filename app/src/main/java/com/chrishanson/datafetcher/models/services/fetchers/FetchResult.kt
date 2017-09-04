package com.chrishanson.datafetcher.models.services.fetchers

import com.chrishanson.datafetcher.models.data.DataItem

class FetchResult(val dataItems: List<DataItem>, val isLastPage: Boolean)
// TODO the Java version has two constructors, one of which takes a data source - not sure if this is needed
