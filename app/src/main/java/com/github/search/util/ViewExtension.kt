package com.github.search.util

import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

fun SearchView.onQueryTextSubmitted(onSubmitted: (String) -> Unit) {
    setOnQueryTextListener(object : SearchView.OnQueryTextListener {

        override fun onQueryTextChange(newText: String?): Boolean {
            return true
        }

        override fun onQueryTextSubmit(query: String?): Boolean {
            clearFocus()
            onSubmitted.invoke(query ?: "")
            return true
        }
    })
}

fun RecyclerView.onScrolledToLastChild(scrolledToLastChild: () -> Unit) {

    addOnScrollListener(object : RecyclerView.OnScrollListener() {

        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            val itemCount = recyclerView.adapter?.itemCount ?: 0
            val layoutManager = recyclerView.layoutManager as LinearLayoutManager
            val lastItemPosition = layoutManager.findLastCompletelyVisibleItemPosition().plus(1)
            if (dy > 0 && lastItemPosition == itemCount) {
                scrolledToLastChild.invoke()
            }
        }
    })
}