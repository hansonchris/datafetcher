package com.chrishanson.datafetcher.models.services.datasources

/**
 * Allows forcing a data source to have a different title. This differs from the
 * DynamicTitleDataSourceWrapper, because this wrapper doesn't attempt to calculate the title on
 * demand.
 *
 * This wrapper is useful in cases where the client creates a data source that ends up being used in
 * multiple separate wrappers.
 */
class TitleOverrideDataSourceWrapper(wrappedDataSource: DataSource, override val title: String?) :
    DataSourceWrapper(wrappedDataSource)
