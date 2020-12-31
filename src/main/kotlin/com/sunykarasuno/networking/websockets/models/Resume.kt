package com.sunykarasuno.networking.websockets.models

import com.google.gson.annotations.SerializedName

data class Resume(
    @SerializedName("d")
    val data: ResumeInfo,
    val op: Int = 6
) {
    data class ResumeInfo(
        val token: String,
        @SerializedName("session_id")
        val sessionId: String,
        val seq: Int
    )
}
