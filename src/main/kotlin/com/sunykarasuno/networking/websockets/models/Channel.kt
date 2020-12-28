package com.sunykarasuno.networking.websockets.models

import com.google.gson.annotations.SerializedName

data class Channel(
    val id: String,
    val type: Int,
    val position: Int,
    val name: String,
    val nsfw: Boolean,
    @SerializedName("last_message_id")
    val lastMessageId: String
)
