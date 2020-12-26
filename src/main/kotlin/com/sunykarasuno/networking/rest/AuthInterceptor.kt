package com.sunykarasuno.networking.rest

import okhttp3.Interceptor
import okhttp3.Response

/**
 * An interceptor that always adds a token to the request.
 */
class AuthInterceptor(private val token: String) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val requestBuilder = chain.request().newBuilder()

        requestBuilder.addHeader("Authorization", "Bot $token")

        return chain.proceed(requestBuilder.build())
    }
}
