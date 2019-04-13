package search

import MainDispatcher
import com.algolia.search.client.Index
import com.algolia.search.model.filter.FilterGroupConverter
import com.algolia.search.model.response.ResponseSearch
import com.algolia.search.model.search.Query
import com.algolia.search.transport.RequestOptions
import filter.MutableFilterState
import filter.toFilterGroups
import kotlinx.coroutines.*
import searcher.Searcher
import searcher.Sequencer
import kotlin.properties.Delegates


public class SearcherSingleIndex(
    public val index: Index,
    public val query: Query = Query(),
    public val filterState: MutableFilterState = MutableFilterState(),
    public val requestOptions: RequestOptions? = null
) : Searcher, CoroutineScope {

    private val sequencer = Sequencer()
    override val coroutineContext = Job()

    public val responseListeners = mutableListOf<(ResponseSearch) -> Unit>()

    public var response by Delegates.observable<ResponseSearch?>(null) { _, _, newValue ->
        if (newValue != null) {
            responseListeners.forEach { it(newValue) }
        }
    }

    override fun search(): Job {
        query.filters = FilterGroupConverter.SQL(filterState.get().toFilterGroups())
        val job = launch {
            val responseSearch = index.search(query, requestOptions)

            withContext(MainDispatcher) { response = responseSearch }
        }
        sequencer.addOperation(job)
        job.invokeOnCompletion { sequencer.operationCompleted(job) }
        return job
    }

    override fun cancel() {
        sequencer.cancelAll()
        coroutineContext.cancelChildren()
    }
}

