package com.github.search.network.interceptor

import com.github.search.util.LinkHeader
import com.github.search.util.PreferencesManager
import okhttp3.Interceptor
import okhttp3.Response
import okhttp3.ResponseBody

class ResponseInterceptor : Interceptor {

    companion object {
        const val HEADER_LINK = "Link"
    }

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val response = chain.proceed(request)
        val bodyString = response.body()?.string()
        val contentType = response.body()?.contentType()
        val headers = response.headers()

        if (headers[HEADER_LINK].isNullOrBlank().not()) {
            val linkHeader = LinkHeader(headers[HEADER_LINK])
            PreferencesManager.apply {
                nextHref = linkHeader.nextUrl
                lastHref = linkHeader.lastUrl
            }
        }

        return response.newBuilder()
            .body(ResponseBody.create(contentType, bodyString ?: ""))
            .build()
    }
}