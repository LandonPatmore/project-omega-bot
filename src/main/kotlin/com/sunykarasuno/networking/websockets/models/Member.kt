package com.sunykarasuno.networking.websockets.models

import com.google.gson.annotations.SerializedName

data class Member(
    val user: User?,
    @SerializedName("nick")
    val nickName: String?,
    val roles: List<String>,
    @SerializedName("joined_at")
    val joinedAt: String,
    @SerializedName("guild_id")
    val guildId: String?,
    val deaf: Boolean?,
    val mute: Boolean?
)
