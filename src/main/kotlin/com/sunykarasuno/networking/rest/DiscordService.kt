package com.sunykarasuno.networking.rest

import com.sunykarasuno.networking.websockets.models.Info
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class DiscordService(
    token: String
) {
    private val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(
            OkHttpClient.Builder()
                .addInterceptor(AuthInterceptor(token))
                .build()
        ).addConverterFactory(GsonConverterFactory.create())
        .build()

    fun getGateway(): Info? {
        val discordApi = retrofit.create(DiscordApi::class.java)
        val response = discordApi.getGatewayInfo().execute()

        return response.body()
    }

    companion object {
        private const val VERSION = 8
        private const val BASE_URL = "https://discord.com/api/v$VERSION/"
    }
}
