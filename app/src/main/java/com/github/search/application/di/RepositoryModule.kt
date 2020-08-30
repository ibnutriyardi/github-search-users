package com.github.search.application.di

import com.github.search.data.repository.SearchRepository
import org.koin.dsl.module

val repositoryModule = module {
    single { SearchRepository(get()) }
}