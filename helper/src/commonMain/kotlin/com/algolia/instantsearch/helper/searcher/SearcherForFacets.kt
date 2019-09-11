package com.algolia.instantsearch.helper.searcher

import com.algolia.instantsearch.core.searcher.Searcher
import com.algolia.instantsearch.core.searcher.Sequencer
import com.algolia.instantsearch.core.subscription.SubscriptionValue
import com.algolia.search.client.Index
import com.algolia.search.model.Attribute
import com.algolia.search.model.response.ResponseSearchForFacets
import com.algolia.search.model.search.Query
import com.algolia.search.transport.RequestOptions
import kotlinx.coroutines.*
import kotlin.jvm.JvmField
import kotlin.jvm.JvmSynthetic


public class SearcherForFacets(
    @JvmField
    public var index: Index,
    @JvmField
    public val attribute: Attribute,
    @JvmField
    public val query: Query = Query(),
    @JvmField
    public var facetQuery: String? = null,
    @JvmField
    public val requestOptions: RequestOptions? = null,
    override val coroutineScope: CoroutineScope = SearcherScope()
) : Searcher<ResponseSearchForFacets> {

    internal val sequencer = Sequencer()
        @JvmSynthetic get

    override val isLoading = SubscriptionValue(false)
    override val error = SubscriptionValue<Throwable?>(null)
    override val response = SubscriptionValue<ResponseSearchForFacets?>(null)

    private val options = requestOptions.withUserAgent()
    private val exceptionHandler = SearcherExceptionHandler(this)

    override fun setQuery(text: String?) {
        facetQuery = text
    }

    override fun searchAsync(): Job {
        return coroutineScope.launch(exceptionHandler) {
            isLoading.value = true
            response.value = withContext(Dispatchers.Default) { search() }
            isLoading.value = false
        }.also {
            sequencer.addOperation(it)
        }
    }

    override suspend fun search(): ResponseSearchForFacets {
        return index.searchForFacets(attribute, facetQuery, query, options)
    }

    override fun cancel() {
        sequencer.cancelAll()
    }
}