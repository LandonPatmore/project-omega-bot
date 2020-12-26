package com.sunykarasuno.networking.models

import com.google.gson.annotations.SerializedName

data class GatewayHeartbeat(
    @SerializedName("d")
    val sequenceNumber: Int,
    val op: Int = 1
)
