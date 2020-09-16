package com.github.search.base

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.github.search.application.di.appModule
import com.github.search.application.di.networkModule
import com.github.search.application.di.repositoryModule
import com.github.search.application.di.viewModelModule
import com.github.search.rule.CoroutineRuleTest
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.koin.test.KoinTest
import org.koin.test.KoinTestRule
import org.koin.test.mock.MockProviderRule
import org.mockito.Mockito
import org.mockito.MockitoAnnotations

@ExperimentalCoroutinesApi
open class BaseTest : KoinTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val testCoroutineRule = CoroutineRuleTest()

    @get:Rule
    val koinTestRule = KoinTestRule.create {
        modules(appModule + networkModule + repositoryModule + viewModelModule)
    }

    @get:Rule
    val mockProvider = MockProviderRule.create { clazz ->
        Mockito.mock(clazz.java)
    }

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
    }

    @After
    fun finish() {
        MockitoAnnotations.openMocks(this)
    }
}