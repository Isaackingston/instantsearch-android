package com.algolia.instantsearch.helper.loading

import com.algolia.instantsearch.core.connection.ConnectionImpl
import com.algolia.instantsearch.core.loading.LoadingViewModel
import com.algolia.instantsearch.core.searcher.Debouncer
import com.algolia.instantsearch.core.searcher.Searcher


internal class LoadingConnectionSearcher<R>(
    private val viewModel: LoadingViewModel,
    private val searcher: Searcher<R>,
    private val debouncer: Debouncer
) : ConnectionImpl() {

    private val eventReload: (Unit) -> Unit = {
        searcher.searchAsync()
    }
    private val updateIsLoading: (Boolean) -> Unit = {
        debouncer.debounce(searcher) {
            viewModel.isLoading.value = it
        }
    }

    override fun connect() {
        super.connect()
        viewModel.isLoading.value = searcher.isLoading.value
        viewModel.eventReload.subscribe(eventReload)
        searcher.isLoading.subscribePast(updateIsLoading)
    }

    override fun disconnect() {
        super.disconnect()
        viewModel.eventReload.unsubscribe(eventReload)
        searcher.isLoading.unsubscribe(updateIsLoading)
    }
}