package com.sunykarasuno.networking.models

import com.google.gson.JsonObject
import com.google.gson.annotations.SerializedName

data class ReceivableGatewayEvent(
    @SerializedName("op")
    val code: Int,
    @SerializedName("d")
    val data: JsonObject
)
