package com.algolia.instantsearch.helper.searchbox

import com.algolia.instantsearch.core.searchbox.SearchBoxViewModel
import com.algolia.instantsearch.core.searcher.Debouncer
import com.algolia.instantsearch.helper.searcher.Searcher
import com.algolia.instantsearch.helper.searcher.SearcherSingleIndex


public fun SearchBoxViewModel.connectSearcher(
    searcher: Searcher,
    searchAsYouType: Boolean = true,
    debouncer: Debouncer = Debouncer(100)
) {
    if (searchAsYouType) {
        onQueryChanged += {
            searcher.setQuery(it)
            debouncer.debounce(searcher.coroutineScope) { searcher.search() }
        }
    } else {
        onQuerySubmitted += {
            searcher.setQuery(it)
            searcher.search()
        }
    }
    if (searcher is SearcherSingleIndex ) {
        searcher.onQueryChanged += {
            query = it
        }
    }
}