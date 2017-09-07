package com.chrishanson.datafetcher.models.services.fetchers

import com.chrishanson.datafetcher.models.data.DataItem
import com.chrishanson.datafetcher.models.services.datasources.DataSource

class FetchResult(val dataSource: DataSource, val dataItems: List<DataItem>, val isLastPage: Boolean)
// TODO the Java version has two constructors, one of which does not take a data source - not sure if this is needed
