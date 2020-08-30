package com.github.search.data.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.github.search.application.ResultCall
import com.github.search.application.base.BaseViewModel
import com.github.search.data.model.SearchItem
import com.github.search.data.repository.SearchRepository
import com.github.search.util.PreferencesManager
import com.github.search.util.StateWrapper
import kotlinx.coroutines.launch
import java.net.HttpURLConnection

class SearchViewModel(private val preferencesManager: PreferencesManager, private val searchRepository: SearchRepository) : BaseViewModel() {

    companion object {
        private const val DEFAULT_ERROR_MESSAGE = "Error loading result, please try again later"
    }

    sealed class Event {
        data class SubmitSearch(val keyword: String) : Event()
        object LoadMoreResult : Event()
    }

    sealed class State {
        data class ShowResult(val items: List<SearchItem>) : State()
        data class AddResult(val items: List<SearchItem>) : State()
        data class ShowError(val errorMessage: String) : State()
        object ShowEmptyResult : State()
    }

    private val _state: MutableLiveData<StateWrapper<State>> = MutableLiveData()
    val state: LiveData<StateWrapper<State>> = _state

    private fun setState(state: State) {
        _state.value = StateWrapper(state)
    }

    fun callEvent(event: Event) {
        when (event) {
            is Event.SubmitSearch -> requestSearch(isLoadMore = false, keyword = event.keyword)
            is Event.LoadMoreResult -> requestSearch(isLoadMore = true, href = preferencesManager.nextHref)
        }
    }

    private fun requestSearch(isLoadMore: Boolean = false, keyword: String = "", href: String = "") = launch {
        if (keyword.isBlank() && href.isBlank()) return@launch
        if (isLoadMore) {
            when (val result = searchRepository.requestSearchMore(href)) {
                is ResultCall.Success -> setState(State.AddResult(result.data))
                is ResultCall.Failed -> setState(State.ShowError(generateErrorMessage(result.responseCode, result.errorMessage)))
                is ResultCall.Error -> setState(State.ShowError(result.errorMessage))
            }
        } else {
            when (val result = searchRepository.requestSearch(keyword)) {
                is ResultCall.Success -> setState(
                    if (result.data.isEmpty()) {
                        State.ShowEmptyResult
                    } else {
                        State.ShowResult(result.data)
                    }
                )

                is ResultCall.Failed -> setState(State.ShowError(generateErrorMessage(result.responseCode, result.errorMessage)))
                is ResultCall.Error -> setState(State.ShowError(result.errorMessage))
            }
        }
    }

    private fun generateErrorMessage(responseCode: Int, errorMessage: String): String {
        return if (responseCode == HttpURLConnection.HTTP_FORBIDDEN) {
            DEFAULT_ERROR_MESSAGE
        } else {
            errorMessage
        }
    }
}