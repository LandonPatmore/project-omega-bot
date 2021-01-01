package com.sunykarasuno.intents.models

import com.google.gson.annotations.SerializedName

data class Channel(
    val id: String,
    val type: Int,
    val position: Int,
    val name: String,
    val nsfw: Boolean,
    @SerializedName("guild_id")
    val guildId: String?,
    @SerializedName("last_message_id")
    val lastMessageId: String?
)
