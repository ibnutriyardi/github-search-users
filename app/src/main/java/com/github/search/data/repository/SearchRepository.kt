package com.github.search.data.repository

import com.github.search.application.ResultCall
import com.github.search.application.callAwait
import com.github.search.application.mapTo
import com.github.search.data.model.SearchItem
import com.github.search.data.model.SearchResult
import com.github.search.network.datasource.SearchRemoteSource

class SearchRepository(private val searchRemoteSource: SearchRemoteSource) {

    suspend fun requestSearch(keyword: String): ResultCall<List<SearchItem>> {
        return searchRemoteSource.requestSearch(keyword).callAwait().mapTo(::mapSearchResult)
    }

    suspend fun requestSearchMore(href: String): ResultCall<List<SearchItem>> {
        return searchRemoteSource.requestSearchMore(href).callAwait().mapTo(::mapSearchResult)
    }

    private fun mapSearchResult(searchResult: SearchResult): List<SearchItem> {
        return searchResult.items?.map {
            SearchItem(
                avatarUrl = it.avatarUrl ?: "",
                login = it.login ?: ""
            )
        } ?: listOf()
    }
}