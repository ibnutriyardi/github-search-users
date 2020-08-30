package com.github.search.network.datasource

import com.github.search.data.model.SearchResult
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query
import retrofit2.http.Url

interface SearchRemoteSource {

    @GET("search/users")
    suspend fun requestSearch(
        @Query("q") keyword: String,
        @Query("per_page") limit: Int = 10
    ): Response<SearchResult>

    @GET
    suspend fun requestSearchMore(
        @Url href: String
    ): Response<SearchResult>
}