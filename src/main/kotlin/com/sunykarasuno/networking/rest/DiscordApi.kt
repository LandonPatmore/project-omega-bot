package com.sunykarasuno.networking.rest

import com.sunykarasuno.intents.models.Channel
import com.sunykarasuno.networking.rest.models.ChannelModify
import com.sunykarasuno.networking.rest.models.Message
import com.sunykarasuno.networking.websockets.models.Info
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path

interface DiscordApi {

    //GET

    @GET("gateway/bot")
    fun getGatewayInfo(): Call<Info>

    @GET("channels/{channelId}")
    fun getChannel(@Path("channelId") channelId: String) : Call<Channel>

    // PATCH

    @PATCH("channels/{channelId}")
    fun modifyChannel(@Path("channelId") channelId: String, @Body channelModify: ChannelModify): Call<Any>

    // DELETE
    @DELETE("channels/{channelId}")
    fun deleteChannel(@Path("channelId") channelId: String): Call<Any>

    // POST

    @POST("channels/{channelId}/messages")
    fun sendMessage(@Path("channelId") channelId: String, @Body message: Message): Call<Any>
}
