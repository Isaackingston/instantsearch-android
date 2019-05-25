package com.algolia.instantsearch.demo.home

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.algolia.instantsearch.core.searchbox.SearchBoxViewModel
import com.algolia.instantsearch.core.searchbox.connectView
import com.algolia.instantsearch.demo.*
import com.algolia.instantsearch.helper.android.searchbox.SearchBoxViewAppCompat
import com.algolia.instantsearch.helper.searchbox.connectSearcher
import com.algolia.instantsearch.helper.searcher.SearcherSingleIndex
import com.algolia.search.helper.deserialize
import com.algolia.search.model.IndexName
import kotlinx.android.synthetic.main.demo_home.*
import kotlinx.android.synthetic.main.include_toolbar_search.*


class HomeDemo : AppCompatActivity() {

    private val index = client.initIndex(IndexName("mobile_demo_home"))
    private val searcher = SearcherSingleIndex(index)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.demo_home)

        val hintIcon = ContextCompat.getDrawable(this, R.drawable.ic_search_hint)!!
        val hintText = getString(R.string.search_demos)

        val searchBoxViewModel = SearchBoxViewModel()
        val searchBoxView = SearchBoxViewAppCompat(searchView)

        searchBoxViewModel.connectView(searchBoxView)
        searchBoxViewModel.connectSearcher(searcher)
        searchView.also {
            it.isSubmitButtonEnabled = false
            it.isFocusable = true
            it.setIconifiedByDefault(false)
            it.setOnQueryTextFocusChangeListener { _, hasFocus ->
                searchView.showQueryHintIcon(!hasFocus, hintIcon, hintText)
            }
        }

        val adapter = HomeAdapter()

        setSupportActionBar(toolbar)
        configureRecyclerView(list, adapter)
        configureSearchView(searchView, getString(R.string.search_demos))

        searcher.onResponseChanged += { response ->
            val hits = response.hits.deserialize(HomeHit.serializer())
                .groupBy { it.type }
                .flatMap { (key, value) ->
                    listOf(HomeItem.Header(key)) + value.map { HomeItem.Item(it) }.sortedBy { it.hit.objectID.raw }
                }

            adapter.submitList(hits)
        }
        searcher.search()
    }

    override fun onDestroy() {
        super.onDestroy()
        searcher.cancel()
    }
}