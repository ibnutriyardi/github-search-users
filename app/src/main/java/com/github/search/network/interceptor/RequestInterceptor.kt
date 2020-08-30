package com.github.search.network.interceptor

import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response

class RequestInterceptor : Interceptor {

    companion object {
        const val HEADER_ACCEPT = "Accept"
        const val HEADER_ACCEPT_VALUE = "application/vnd.github.v3+json"
    }

    override fun intercept(chain: Interceptor.Chain): Response {
        val original = chain.request()
        val builder: Request.Builder = original.newBuilder()
            .addHeader(HEADER_ACCEPT, HEADER_ACCEPT_VALUE)

        return chain.proceed(builder.build())
    }
}