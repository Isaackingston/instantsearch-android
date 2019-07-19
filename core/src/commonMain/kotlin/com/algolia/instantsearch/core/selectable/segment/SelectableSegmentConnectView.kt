package com.algolia.instantsearch.core.selectable.segment

import com.algolia.instantsearch.core.Presenter
import com.algolia.instantsearch.core.connection.Connection


public fun <K, I, O> SelectableSegmentViewModel<K, I>.connectView(
    view: SelectableSegmentView<K, O>,
    presenter: Presenter<I, O>,
    connect: Boolean = true
): Connection {
    return SelectableSegmentConnectionView(this, view, presenter).apply { if (connect) connect() }
}