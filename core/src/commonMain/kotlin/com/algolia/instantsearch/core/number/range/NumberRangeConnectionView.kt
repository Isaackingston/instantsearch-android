package com.algolia.instantsearch.core.number.range

import com.algolia.instantsearch.core.connection.ConnectionImpl


internal class NumberRangeConnectionView<T>(
    private val viewModel: NumberRangeViewModel<T>,
    private val view: NumberRangeView<T>
) : ConnectionImpl() where T : Number, T : Comparable<T> {

    private val updateBounds: (Range<T>?) -> Unit = { bounds ->
        view.setBounds(bounds)
    }
    private val updateRange: (Range<T>?) -> Unit = { range ->
        view.setRange(range)
    }

    override fun connect() {
        super.connect()
        viewModel.bounds.subscribePast(updateBounds)
        viewModel.range.subscribePast(updateRange)
        view.onRangeChanged = (viewModel.eventRange::send)
    }

    override fun disconnect() {
        super.disconnect()
        viewModel.bounds.unsubscribe(updateBounds)
        viewModel.bounds.unsubscribe(updateRange)
        view.onRangeChanged = null
    }
}