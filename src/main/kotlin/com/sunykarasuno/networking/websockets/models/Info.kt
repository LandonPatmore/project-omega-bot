package com.sunykarasuno.networking.websockets.models

import com.google.gson.annotations.SerializedName

data class Info(
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
