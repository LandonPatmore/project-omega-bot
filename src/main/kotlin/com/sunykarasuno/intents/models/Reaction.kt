package com.sunykarasuno.intents.models

import com.google.gson.annotations.SerializedName

data class Reaction(
    @SerializedName("guild_id")
    val guildId: String,
    @SerializedName("channel_id")
    val channelId: String,
    val member: Member,
    @SerializedName("message_id")
    val messageId: String,
    val emoji: Emoji
)
