package com.sunykarasuno.networking.websockets.models

import com.google.gson.JsonObject
import com.google.gson.annotations.SerializedName

data class ReceivableGatewayEvent(
    @SerializedName("op")
    val code: Int,
    @SerializedName("t")
    val type: String,
    @SerializedName("s")
    val sequence: Int,
    @SerializedName("d")
    val data: JsonObject
)
