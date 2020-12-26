package com.sunykarasuno.networking.models

import com.google.gson.annotations.SerializedName

data class GatewayInfo(
    val url: String,
    val shards: Int,
    val sessionStartLimit: SessionStartLimit
) {
    data class SessionStartLimit(
        val total: Int,
        val remaining: Int,
        @SerializedName("reset_after")
        val resetAfter: Int,
        @SerializedName("max_concurrency")
        val maxConcurrency: Int
    )
}
