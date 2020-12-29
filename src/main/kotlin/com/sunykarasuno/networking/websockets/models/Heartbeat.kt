package com.sunykarasuno.networking.websockets.models

import com.google.gson.annotations.SerializedName

data class Heartbeat(
    @SerializedName("d")
    val sequenceNumber: Int,
    val op: Int = 1
)
