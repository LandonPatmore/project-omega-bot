package com.sunykarasuno.networking.rest

import com.sunykarasuno.networking.models.GatewayInfo
import retrofit2.Call
import retrofit2.http.GET

interface DiscordApi {

    @GET("gateway/bot")
    fun getGatewayInfo(): Call<GatewayInfo>
}
