package com.sunykarasuno.networking.rest

import com.sunykarasuno.networking.websockets.models.Info
import retrofit2.Call
import retrofit2.http.GET

interface DiscordApi {

    @GET("gateway/bot")
    fun getGatewayInfo(): Call<Info>
}
