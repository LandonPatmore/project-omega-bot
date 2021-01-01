package com.sunykarasuno.intents.models

import com.google.gson.annotations.SerializedName

data class Message(
    val id: String,
    @SerializedName("guild_id")
    val guildId: String,
    @SerializedName("channel_id")
    val channelId: String,
    val author: User,
    val member: Member?,
    val content: String,
    val timestamp: String,
    @SerializedName("edited_timestamp")
    val editedTimestamp: String?,
    val mentions: List<User>,
    @SerializedName("mention_roles")
    val mentionRoles: List<String>,
    @SerializedName("mention_channels")
    val mentionChannels: List<String>?,
    val reactions: List<Reaction>,
    val pinned: Boolean,
    @SerializedName("webhook_id")
    val webhookId: String?,
    // TODO: Change to MessageType
    val type: Int,
    val referencedMessage: Message?

)
