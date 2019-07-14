package com.algolia.instantsearch.helper.filter.current

import com.algolia.instantsearch.core.observable.ObservableEvent
import com.algolia.instantsearch.core.observable.ObservableItem


public class FilterCurrentViewModel(filters: Set<FilterAndID> = setOf()) {

    val filters = ObservableItem(filters)

    val event = ObservableEvent<Set<FilterAndID>>()

    fun add(element: FilterAndID) {
        event.send(filters.get() + element)
    }

    fun remove(element: FilterAndID) {
        event.send(filters.get() - element)
    }

    fun clear() {
        event.send(setOf())
    }
}