package com.github.search.application

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import com.github.search.BuildConfig
import com.github.search.data.model.ErrorMessage
import com.github.search.network.interceptor.RequestInterceptor
import com.github.search.network.interceptor.ResponseInterceptor
import com.github.search.util.StateWrapper
import com.google.gson.Gson
import com.google.gson.JsonParseException
import com.google.gson.JsonSyntaxException
import okhttp3.OkHttpClient
import retrofit2.HttpException
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException
import java.net.SocketTimeoutException
import java.util.concurrent.TimeUnit

sealed class ResultCall<out T> {
    data class Success<T>(val data: T) : ResultCall<T>()
    data class Failed(val responseCode: Int, val errorMessage: String) : ResultCall<Nothing>()
    data class Error(val errorMessage: String) : ResultCall<Nothing>()
}

suspend fun <T> Response<T>.callAwait(): ResultCall<T> {
    return try {
        val response = this
        if (response.isSuccessful)
            ResultCall.Success<T>(response.body()!!)
        else {
            val errorBody = response.errorBody()
            val errorResponse = Gson().fromJson(errorBody?.charStream(), ErrorMessage::class.java)
            ResultCall.Failed(
                response.code(),
                errorResponse.message ?: ""
            )
        }
    } catch (e: HttpException) {
        val error = e.response()
        ResultCall.Failed(error?.code() ?: 0, error?.message() ?: "Internal Server Error")
    } catch (e: JsonParseException) {
        ResultCall.Error(e.message ?: "")
    } catch (e: JsonSyntaxException) {
        ResultCall.Error(e.message ?: "")
    } catch (e: SocketTimeoutException) {
        ResultCall.Error(e.message ?: "")
    } catch (e: IOException) {
        ResultCall.Error(e.message ?: "")
    }
}

fun <A, B> ResultCall<A>.mapTo(block: (A) -> B): ResultCall<B> {
    return when (this) {
        is ResultCall.Success -> ResultCall.Success(block(this.data))
        is ResultCall.Failed -> ResultCall.Failed(this.responseCode, this.errorMessage)
        is ResultCall.Error -> ResultCall.Error(this.errorMessage)
    }
}

inline fun <T> LifecycleOwner.subscribeSingleState(
    liveData: LiveData<StateWrapper<T>>,
    crossinline onEventUnhandled: (T) -> Unit
) {
    liveData.observe(this, Observer { it?.getEventIfNotHandled()?.let(onEventUnhandled) })
}

fun createOkHttpClient(): OkHttpClient {
    return OkHttpClient.Builder()
        .addInterceptor(RequestInterceptor())
        .addInterceptor(ResponseInterceptor())
        .connectTimeout(1, TimeUnit.MINUTES)
        .readTimeout(1, TimeUnit.MINUTES)
        .build()
}

inline fun <reified T> createApi(okHttpClient: OkHttpClient): T {
    val retrofit = Retrofit.Builder()
        .baseUrl(BuildConfig.BASE_URL)
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    return retrofit.create(T::class.java)
}