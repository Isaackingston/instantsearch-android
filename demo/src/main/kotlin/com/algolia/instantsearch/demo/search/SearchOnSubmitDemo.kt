package com.algolia.instantsearch.demo.search

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.algolia.instantsearch.core.searchbox.SearchBoxViewModel
import com.algolia.instantsearch.core.searchbox.connectView
import com.algolia.instantsearch.demo.*
import com.algolia.instantsearch.demo.list.Movie
import com.algolia.instantsearch.demo.list.MovieAdapter
import com.algolia.instantsearch.helper.android.searchbox.SearchBoxViewAppCompat
import com.algolia.instantsearch.helper.searchbox.connectSearcher
import com.algolia.instantsearch.helper.searcher.SearcherSingleIndex
import com.algolia.search.helper.deserialize
import kotlinx.android.synthetic.main.demo_search.*
import kotlinx.android.synthetic.main.include_toolbar_search.*


class SearchOnSubmitDemo : AppCompatActivity() {

    private val searcher = SearcherSingleIndex(stubIndex)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.demo_search)

        searcher.index = client.initIndex(intent.indexName)

        val adapter = MovieAdapter()
        val searchBoxView = SearchBoxViewAppCompat(searchView)
        val searchBoxViewModel = SearchBoxViewModel()

        searchBoxViewModel.connectView(searchBoxView)
        searchBoxViewModel.connectSearcher(searcher, searchAsYouType = false)

        searcher.onResponseChanged += {
            adapter.submitList(it.hits.deserialize(Movie.serializer()))
        }

        setSupportActionBar(toolbar)
        configureRecyclerView(list, adapter)
        configureSearchView(searchView, getString(R.string.search_movies))

        searcher.search()
    }

    override fun onDestroy() {
        super.onDestroy()
        searcher.cancel()
    }
}
