package com.sunykarasuno.networking.websockets.models

import com.google.gson.annotations.SerializedName

data class Presence(
    @SerializedName("guild_id")
    val guildId: String,
    val user: User,
    val status: Status
)
