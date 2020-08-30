package com.github.search.ui

import android.os.Bundle
import android.view.Menu
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import com.github.search.R
import com.github.search.application.subscribeSingleState
import com.github.search.data.viewmodel.SearchViewModel
import com.github.search.util.onQueryTextSubmitted
import com.github.search.util.onScrolledToLastChild
import kotlinx.android.synthetic.main.activity_search.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class SearchActivity : AppCompatActivity() {

    private val searchViewModel: SearchViewModel by viewModel()
    private val searchAdapter by lazy { SearchAdapter() }
    private var searchView: SearchView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)
        setSupportActionBar(toolbar)
        rv_user?.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(this@SearchActivity, LinearLayoutManager.VERTICAL, false)
            adapter = searchAdapter
            onScrolledToLastChild {
                searchViewModel.callEvent(SearchViewModel.Event.LoadMoreResult)
            }
        }

        observeData()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_search, menu)
        val searchItem = menu?.findItem(R.id.search)
        searchView = searchItem?.actionView as SearchView?
        searchView?.apply {
            maxWidth = Integer.MAX_VALUE
            onQueryTextSubmitted {
                tv_empty_result?.visibility = View.GONE
                searchViewModel.callEvent(SearchViewModel.Event.SubmitSearch(it))
            }
        }

        return super.onCreateOptionsMenu(menu)
    }

    private fun observeData() {
        subscribeSingleState(searchViewModel.state) {
            when (it) {
                is SearchViewModel.State.ShowResult -> searchAdapter.items = it.items.toMutableList()
                is SearchViewModel.State.AddResult -> searchAdapter.addItems(it.items)
                is SearchViewModel.State.ShowError -> Toast.makeText(this, it.errorMessage, Toast.LENGTH_LONG).show()
                is SearchViewModel.State.ShowEmptyResult -> {
                    searchAdapter.items = mutableListOf()
                    tv_empty_result?.visibility = View.VISIBLE
                }
            }
        }
    }
}