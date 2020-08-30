package com.github.search.application.di

import com.github.search.util.PreferencesManager
import org.koin.dsl.module

val appModule = module {
    single { PreferencesManager }
}