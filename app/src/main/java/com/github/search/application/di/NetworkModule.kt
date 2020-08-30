package com.github.search.application.di

import com.github.search.application.createApi
import com.github.search.application.createOkHttpClient
import com.github.search.network.datasource.SearchRemoteSource
import org.koin.dsl.module

val networkModule = module {
    single { createOkHttpClient() }
    single { createApi<SearchRemoteSource>(get()) }
}