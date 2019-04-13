package search

import MainDispatcher
import com.algolia.search.client.ClientSearch
import com.algolia.search.model.multipleindex.IndexQuery
import com.algolia.search.model.multipleindex.MultipleQueriesStrategy
import com.algolia.search.model.response.ResponseSearches
import com.algolia.search.transport.RequestOptions
import kotlinx.coroutines.*
import searcher.Searcher
import searcher.Sequencer
import kotlin.properties.Delegates


public class SearcherMultipleIndices(
    public val client: ClientSearch,
    public val indexQueries: List<IndexQuery>,
    public val strategy: MultipleQueriesStrategy? = null,
    public val requestOptions: RequestOptions? = null
) : Searcher, CoroutineScope {

    private val sequencer = Sequencer()
    override val coroutineContext = Job()

    public val responseListeners = mutableListOf<(ResponseSearches) -> Unit>()

    public var response by Delegates.observable<ResponseSearches?>(null) { _, _, newValue ->
        if (newValue != null) {
            responseListeners.forEach { it(newValue) }
        }
    }

    override fun search(): Job {
        val job = launch {
            val responseSearches = client.multipleQueries(indexQueries, strategy, requestOptions)

            withContext(MainDispatcher) { response = responseSearches }
        }
        sequencer.addOperation(job)
        job.invokeOnCompletion { sequencer.operationCompleted(this) }
        return job
    }

    override fun cancel() {
        sequencer.cancelAll()
        coroutineContext.cancelChildren()
    }
}
