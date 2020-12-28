package com.sunykarasuno.networking.websockets.models

import com.google.gson.annotations.SerializedName

// TODO: Will need custom Gson parsers
sealed class Intent {
    sealed class Generic : Intent() {
        data class Ready(
            @SerializedName("v")
            val version: Int,
            val user: User,
            @SerializedName("session_id")
            val sessionId: String,
            // TODO: Custom deserializer
//            val guilds: List<String>,
            val shard: List<Int>?
        ) : Generic()
    }

    sealed class Guild : Intent() {
        data class Create(
            val id: String,
            val name: String,
            val channels: List<Channel>,
            val roles: List<Role>,
            val presences: List<Presence>,
            val members: List<Member>
        ) : Guild()

        data class Update(
            @SerializedName("guild_id")
            val id: String,
            val name: String,
            val roles: List<Role>
        ) : Guild()

        data class Delete(
            @SerializedName("guild_id")
            val id: String,
        ) : Guild()
    }

    sealed class Roles : Intent() {
        data class Create(
            @SerializedName("guild_id")
            val guildId: String,
            val role: Role
        ) : Roles()

        data class Update(
            @SerializedName("guild_id")
            val guildId: String,
            val role: Role
        ) : Roles()

        data class Delete(
            @SerializedName("role_id")
            val roleId: String,
            @SerializedName("guild_id")
            val guildId: String
        ) : Roles()
    }

    sealed class Channels : Intent() {
        data class Create(
            @SerializedName("guild_id")
            val guildId: String,
            // TODO: Custom deserializer
            val channel: Channel
        ) : Channels()

        data class Update(
            @SerializedName("guild_id")
            val guildId: String,
            // TODO: Custom deserializer
            val channel: Channel
        ) : Channels()

        data class Delete(
            @SerializedName("channel_id")
            val channelId: String,
            @SerializedName("guild_id")
            val guildId: String
        ) : Channels()

        @Deprecated("Not implemented yet", ReplaceWith(""), DeprecationLevel.WARNING)
        object PinsUpdate : Channels()
    }

    sealed class Members : Intent() {
        data class Add(
            @SerializedName("guild_id")
            val guildId: String,
            val member: Member
        ) : Members()

        data class Update(
            @SerializedName("guild_id")
            val guildId: String,
            // TODO: Custom deserializer
            val member: Member
        ) : Members()

        data class Remove(
            @SerializedName("guild_id")
            val guildId: String,
            // TODO: Custom deserializer
            val member: Member
        ) : Members()
    }

    sealed class Ban : Intent() {
        data class Add(
            @SerializedName("guild_id")
            val guildId: String,
            val reason: String,
            val user: User
        ) : Ban()

        data class Remove(
            @SerializedName("guild_id")
            val guildId: String,
            val user: User
        ) : Ban()
    }

    sealed class Emojis : Intent() {
        data class Update(
            val emoji: Emoji
        ) : Emojis()
    }

    sealed class Invites : Intent() {
        data class Create(
            val inviter: User,
            @SerializedName("guild_id")
            val guildId: String,
            @SerializedName("channel_id")
            val channelId: String,
            val code: String,
            @SerializedName("created_at")
            val createdAt: String,
            @SerializedName("max_age")
            val maxAge: Int,
            @SerializedName("max_uses")
            val maxUses: Int,
            val uses: Int
        ) : Invites()

        data class Delete(
            @SerializedName("guild_id")
            val guildId: String,
            @SerializedName("channel_id")
            val channelId: String,
            val code: String
        ) : Invites()
    }

    sealed class Voice : Intent() {
        data class Update(
            @SerializedName("guild_id")
            val guildId: String,
            @SerializedName("channel_id")
            val channelId: String,
            @SerializedName("session_id")
            val sessionId: String,
            val deaf: Boolean,
            val mute: Boolean,
            @SerializedName("self_deaf")
            val selfDeaf: Boolean,
            @SerializedName("self_mute")
            val selfMute: Boolean
        ) : Voice()
    }

    sealed class Presences : Intent() {
        // TODO: Custom deserializer
        data class Update(val presence: Presence) : Presences()
    }

    sealed class Reactions : Intent() {
        // TODO: Custom deserializer
        data class Add(val reaction: Reaction) : Reactions()

        data class Remove(
            @SerializedName("guild_id")
            val guildId: String,
            @SerializedName("channel_id")
            val channelId: String,
            @SerializedName("message_id")
            val messageId: String,
            val emoji: Emoji,
            @SerializedName("user_id")
            val userId: String
        ) : Reactions()

        @Deprecated("Not implemented yet", ReplaceWith(""), DeprecationLevel.WARNING)
        object RemoveAll : Reactions()

        @Deprecated("Not implemented yet", ReplaceWith(""), DeprecationLevel.WARNING)
        object RemoveEmoji : Reactions()
    }

    sealed class Typing : Intent() {
        data class Start(
            @SerializedName("guild_id")
            val guildId: String,
            @SerializedName("channel_id")
            val channelId: String,
            @SerializedName("user_id")
            val userId: String,
            val timestamp: Int,
            // TODO: Could be null if a DM
            val member: Member?
        ) : Typing()
    }

    sealed class Messages : Intent() {
        // TODO: Custom deserializer
        data class Create(val message: Message) : Messages()
        // TODO: Custom deserializer
        data class Update(val message: Message) : Messages()
        data class Delete(
            val id: String,
            @SerializedName("guild_id")
            val guildId: String?,
            @SerializedName("channel_id")
            val channelId: String
        ) : Messages()

        @Deprecated("Not implemented yet", ReplaceWith(""), DeprecationLevel.WARNING)
        object PinsUpdate : Messages()
    }
}
