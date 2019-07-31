package com.algolia.instantsearch.helper.filter.segment

import com.algolia.instantsearch.core.connection.ConnectionImpl
import com.algolia.instantsearch.helper.filter.state.FilterGroupID
import com.algolia.instantsearch.helper.filter.state.FilterOperator
import com.algolia.instantsearch.helper.filter.state.FilterState
import com.algolia.search.model.filter.Filter


public data class FilterSegmentConnector(
    public val filterState: FilterState,
    public val viewModel: FilterSegmentViewModel = FilterSegmentViewModel(),
    public val groupID: FilterGroupID = FilterGroupID(FilterOperator.And)
) : ConnectionImpl() {

    public constructor(
        filters: Map<Int, Filter>,
        filterState: FilterState,
        selected: Int? = null,
        groupID: FilterGroupID = FilterGroupID(FilterOperator.And)
    ) : this(filterState, FilterSegmentViewModel(filters, selected), groupID)

    private val connectionFilterState = viewModel.connectFilterState(filterState, groupID)

    override fun connect() {
        super.connect()
        connectionFilterState.connect()
    }

    override fun disconnect() {
        super.disconnect()
        connectionFilterState.disconnect()
    }
}