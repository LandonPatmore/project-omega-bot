package com.sunykarasuno.networking.websockets.models

import com.google.gson.annotations.SerializedName

data class Hello(
    @SerializedName("op")
    val code: Int,
    @SerializedName("d")
    val data: Data
) {
    data class Data(
        @SerializedName("heartbeat_interval")
        val heartbeatInterval: Int
    )
}
