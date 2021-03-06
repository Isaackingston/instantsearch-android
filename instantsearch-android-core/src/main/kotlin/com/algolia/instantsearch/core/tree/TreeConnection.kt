package com.algolia.instantsearch.core.tree

import com.algolia.instantsearch.core.connection.Connection
import com.algolia.instantsearch.core.tree.internal.TreeConnectionView

public fun <K, I, O> TreeViewModel<K, I>.connectView(
    view: TreeView<K, O>,
    presenter: TreePresenter<I, O>
): Connection {
    return TreeConnectionView(this, view, presenter)
}
