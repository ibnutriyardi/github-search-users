package com.github.search.application

import android.app.Application
import com.github.search.application.di.appModule
import com.github.search.application.di.networkModule
import com.github.search.application.di.repositoryModule
import com.github.search.application.di.viewModelModule
import com.orhanobut.hawk.Hawk
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level

class MainApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        Hawk.init(this).build()
        startKoin {
            androidLogger(Level.ERROR)
            androidContext(this@MainApplication)
            modules(appModule + networkModule + repositoryModule + viewModelModule)
        }
    }
}