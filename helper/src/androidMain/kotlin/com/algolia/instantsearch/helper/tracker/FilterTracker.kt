@file:Suppress("FunctionName")

package com.algolia.instantsearch.helper.tracker

import android.content.Context
import com.algolia.instantsearch.helper.searcher.SearcherMultipleIndex
import com.algolia.instantsearch.helper.searcher.SearcherSingleIndex
import com.algolia.instantsearch.insights.Insights
import com.algolia.instantsearch.insights.register
import com.algolia.search.client.ClientSearch

@JvmName("create")
public fun FilterTracker(
    context: Context,
    eventName: String,
    searcher: SearcherSingleIndex,
    client: ClientSearch // TODO: can't access the transport object from the searcher
): InsightsTracker {
    val trackableSearcher = TrackableSearcher.SingleIndex(searcher)
    val insights = Insights.register(
        context = context,
        appId = client.applicationID.raw,
        apiKey = client.apiKey.raw,
        indexName = searcher.index.indexName.raw
    )

    return FilterTracker(
        eventName = eventName,
        searcher = trackableSearcher,
        insights = insights
    )
}

@JvmName("create")
public fun FilterTracker(
    context: Context,
    eventName: String,
    searcher: SearcherMultipleIndex,
    pointer: Int,
    client: ClientSearch // TODO: can't access the transport object from the searcher
): InsightsTracker {
    val trackableSearcher = TrackableSearcher.MultiIndex(searcher = searcher, pointer = pointer)
    val insights = Insights.register(
        context = context,
        appId = client.applicationID.raw,
        apiKey = client.apiKey.raw,
        indexName = searcher.queries[pointer].indexName.raw
    )

    return FilterTracker(
        eventName = eventName,
        searcher = trackableSearcher,
        insights = insights
    )
}
