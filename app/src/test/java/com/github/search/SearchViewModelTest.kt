package com.github.search

import androidx.lifecycle.Observer
import com.github.search.application.ResultCall
import com.github.search.base.BaseTest
import com.github.search.data.model.ErrorMessage
import com.github.search.data.model.SearchResult
import com.github.search.data.repository.SearchRepository
import com.github.search.data.viewmodel.SearchViewModel
import com.github.search.util.StateWrapper
import com.google.gson.Gson
import com.nhaarman.mockitokotlin2.never
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Test
import org.koin.test.mock.declareMock
import org.mockito.ArgumentMatchers.anyBoolean
import org.mockito.ArgumentMatchers.anyString
import org.mockito.Mock

@ExperimentalCoroutinesApi
class SearchViewModelTest : BaseTest() {

    private val viewModel by lazy { declareMock<SearchViewModel>() }
    private val repository by lazy { declareMock<SearchRepository>() }

    @Mock
    private lateinit var viewStateObserver: Observer<StateWrapper<SearchViewModel.State>>

    @Test
    fun `do nothing when given keyword is blank`() = testCoroutineRule.runBlockingTest {
        whenever(viewModel.callEvent(SearchViewModel.Event.SubmitSearch(anyString()))).then {
            verify(viewModel).requestSearch(anyBoolean(), anyString(), anyString())
        }

        verify(repository, never()).requestSearch(anyString())
    }

    @Test
    fun `do nothing when given href is blank`() = testCoroutineRule.runBlockingTest {
        whenever(viewModel.callEvent(SearchViewModel.Event.LoadMoreResult)).then {
            verify(viewModel).requestSearch(anyBoolean(), anyString(), anyString())
        }

        verify(repository, never()).requestSearchMore(anyString())
    }

    @Test
    fun `search users with not empty result`() = testCoroutineRule.runBlockingTest {
        val keyword = "user"
        val json = javaClass.classLoader?.getResource("success_search_result.json")?.readText()
        val searchResult = Gson().fromJson(json, SearchResult::class.java)
        val items = searchResult.items ?: listOf()

        whenever(viewModel.callEvent(SearchViewModel.Event.SubmitSearch(anyString()))).then {
            verify(viewModel).requestSearch(anyBoolean(), anyString(), anyString())
        }

        whenever(repository.requestSearch(keyword)).thenReturn(ResultCall.Success(items)).then {
            viewModel.state.observeForever(viewStateObserver)
            verify(viewStateObserver).onChanged(StateWrapper(SearchViewModel.State.ShowResult(items)))
        }
    }

    @Test
    fun `search users with empty result`() = testCoroutineRule.runBlockingTest {
        val keyword = "this is empty test result"
        whenever(viewModel.callEvent(SearchViewModel.Event.SubmitSearch(anyString()))).then {
            verify(viewModel).requestSearch(anyBoolean(), anyString(), anyString())
        }

        whenever(repository.requestSearch(keyword)).thenReturn(ResultCall.Success(listOf())).then {
            viewModel.state.observeForever(viewStateObserver)
            verify(viewStateObserver).onChanged(StateWrapper(SearchViewModel.State.ShowResult(listOf())))
        }
    }

    @Test
    fun `search users with failed result`() = testCoroutineRule.runBlockingTest {
        val keyword = "user"
        val json = javaClass.classLoader?.getResource("error_search_result.json")?.readText()
        val errorMessage = Gson().fromJson(json, ErrorMessage::class.java)
        val code = 403
        val message = errorMessage.message ?: ""

        whenever(viewModel.callEvent(SearchViewModel.Event.SubmitSearch(anyString()))).then {
            verify(viewModel).requestSearch(anyBoolean(), anyString(), anyString())
        }

        whenever(repository.requestSearch(keyword)).thenReturn(ResultCall.Failed(code, message)).then {
            viewModel.state.observeForever(viewStateObserver)
            verify(viewStateObserver).onChanged(StateWrapper(SearchViewModel.State.ShowError(message)))
        }
    }

    @Test
    fun `search users with error result`() = testCoroutineRule.runBlockingTest {
        val keyword = "user"
        val json = javaClass.classLoader?.getResource("error_search_result.json")?.readText()
        val errorMessage = Gson().fromJson(json, ErrorMessage::class.java)
        val message = errorMessage.message ?: ""

        whenever(viewModel.callEvent(SearchViewModel.Event.SubmitSearch(anyString()))).then {
            verify(viewModel).requestSearch(anyBoolean(), anyString(), anyString())
        }

        whenever(repository.requestSearch(keyword)).thenReturn(ResultCall.Error(message)).then {
            viewModel.state.observeForever(viewStateObserver)
            verify(viewStateObserver).onChanged(StateWrapper(SearchViewModel.State.ShowError(message)))
        }
    }
}