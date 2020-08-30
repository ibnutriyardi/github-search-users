package com.github.search.util

open class StateWrapper<out T>(private val content: T) {

    private var hasBeenHandled = false
    fun getEventIfNotHandled(): T? = if (hasBeenHandled) {
        null
    } else {
        hasBeenHandled = true
        content
    }

    fun peekContent(): T = content
}