package com.sunykarasuno.networking.websockets

import com.google.gson.Gson
import com.google.gson.JsonObject
import com.sunykarasuno.networking.websockets.models.Intent
import mu.KotlinLogging

private val logger = KotlinLogging.logger {}

class GatewayIntentInterpreter(private val intentController: IntentController) : Interpreter {
    private val gson = Gson()

    override fun consumeIntent(type: String, data: JsonObject) {
        val intent = when (type) {
            in Regex("GUILD_ROLE.*") -> consumeRoleIntent(type, data)
            in Regex("GUILD_MEMBER.*") -> consumeMemberIntent(type, data)
            in Regex("GUILD_BAN.*") -> consumeBanIntent(type, data)
            in Regex("GUILD_EMOJIS.*") -> consumeEmojisIntent(type, data)
            in Regex("GUILD.*") -> consumeGuildIntent(type, data)
            in Regex("CHANNEL.*") -> consumeChannelIntent(type, data)
            in Regex("INVITE.*") -> consumeInvitesIntent(type, data)
            in Regex("VOICE_STATE.*") -> consumeVoiceIntent(type, data)
            in Regex("PRESENCE.*") -> consumePresenceIntent(type, data)
            in Regex("MESSAGE_REACTION.*") -> consumeReactionIntent(type, data)
            in Regex("MESSAGE.*") -> consumeMessageIntent(type, data)
            in Regex("TYPING.*") -> consumeTypingIntent(type, data)
            in Regex(".*") -> consumeGenericIntent(type, data)
            else -> null
        }

        intent?.let {
            logger.debug { it }
            intentController.consumer.accept(it)
        } ?: logger.warn { "Could not interpret the following intent: $type" }
    }

    override fun consumeGuildIntent(type: String, data: JsonObject): Intent.Guild? {
        return when (type) {
            "GUILD_CREATE" -> gson.fromJson(data, Intent.Guild.Create::class.java)
            "GUILD_UPDATE" -> gson.fromJson(data, Intent.Guild.Update::class.java)
            "GUILD_DELETE" -> gson.fromJson(data, Intent.Guild.Delete::class.java)
            else -> null
        }
    }

    override fun consumeRoleIntent(type: String, data: JsonObject): Intent.Roles? {
        return when (type) {
            "GUILD_ROLE_CREATE" -> gson.fromJson(data, Intent.Roles.Create::class.java)
            "GUILD_ROLE_UPDATE" -> gson.fromJson(data, Intent.Roles.Update::class.java)
            "GUILD_ROLE_DELETE" -> gson.fromJson(data, Intent.Roles.Delete::class.java)
            else -> null
        }
    }

    override fun consumeChannelIntent(type: String, data: JsonObject): Intent.Channels? {
        return when (type) {
            "CHANNEL_CREATE" -> gson.fromJson(data, Intent.Channels.Create::class.java)
            "CHANNEL_UPDATE" -> gson.fromJson(data, Intent.Channels.Update::class.java)
            "CHANNEL_DELETE" -> gson.fromJson(data, Intent.Channels.Delete::class.java)
            "CHANNEL_PINS_UPDATE" -> gson.fromJson(data, Intent.Channels.PinsUpdate::class.java)
            else -> null
        }
    }

    override fun consumeMemberIntent(type: String, data: JsonObject): Intent.Members? {
        return when (type) {
            "GUILD_MEMBER_ADD" -> gson.fromJson(data, Intent.Members.Add::class.java)
            "GUILD_MEMBER_UPDATE" -> gson.fromJson(data, Intent.Members.Update::class.java)
            "GUILD_MEMBER_REMOVE" -> gson.fromJson(data, Intent.Members.Remove::class.java)
            else -> null
        }
    }

    override fun consumeBanIntent(type: String, data: JsonObject): Intent.Ban? {
        return when (type) {
            "GUILD_BAN_ADD" -> gson.fromJson(data, Intent.Ban.Add::class.java)
            "GUILD_BAN_REMOVE" -> gson.fromJson(data, Intent.Ban.Remove::class.java)
            else -> null
        }
    }

    override fun consumeEmojisIntent(type: String, data: JsonObject): Intent.Emojis? {
        return when (type) {
            "GUILD_EMOJIS_UPDATE" -> gson.fromJson(data, Intent.Emojis.Update::class.java)
            else -> null
        }
    }

    override fun consumeInvitesIntent(type: String, data: JsonObject): Intent.Invites? {
        return when (type) {
            "INVITE_CREATE" -> gson.fromJson(data, Intent.Invites.Create::class.java)
            "INVITE_DELETE" -> gson.fromJson(data, Intent.Invites.Delete::class.java)
            else -> null
        }
    }

    override fun consumeVoiceIntent(type: String, data: JsonObject): Intent.Voice? {
        return when (type) {
            "VOICE_STATE_UPDATE" -> gson.fromJson(data, Intent.Voice.Update::class.java)
            else -> null
        }
    }

    override fun consumePresenceIntent(type: String, data: JsonObject): Intent.Presences? {
        return when (type) {
            "PRESENCE_UPDATE" -> gson.fromJson(data, Intent.Presences.Update::class.java)
            else -> null
        }
    }

    override fun consumeReactionIntent(type: String, data: JsonObject): Intent.Reactions? {
        return when (type) {
            "MESSAGE_REACTION_ADD" -> gson.fromJson(data, Intent.Reactions.Add::class.java)
            "MESSAGE_REACTION_REMOVE" -> gson.fromJson(data, Intent.Reactions.Remove::class.java)
            "MESSAGE_REACTION_REMOVE_ALL" -> gson.fromJson(data, Intent.Reactions.RemoveAll::class.java)
            "MESSAGE_REACTION_REMOVE_EMOJI" -> gson.fromJson(data, Intent.Reactions.RemoveEmoji::class.java)
            else -> null
        }
    }

    override fun consumeTypingIntent(type: String, data: JsonObject): Intent.Typing? {
        return when (type) {
            "TYPING_START" -> gson.fromJson(data, Intent.Typing.Start::class.java)
            else -> null
        }
    }

    override fun consumeMessageIntent(type: String, data: JsonObject): Intent.Messages? {
        return when (type) {
            "MESSAGE_CREATE" -> gson.fromJson(data, Intent.Messages.Create::class.java)
            "MESSAGE_UPDATE" -> gson.fromJson(data, Intent.Messages.Update::class.java)
            "MESSAGE_DELETE" -> gson.fromJson(data, Intent.Messages.Delete::class.java)
            "MESSAGE_PINS_UPDATE" -> gson.fromJson(data, Intent.Messages.PinsUpdate::class.java)
            else -> null
        }
    }

    override fun consumeGenericIntent(type: String, data: JsonObject): Intent.Generic? {
        return when (type) {
            "READY" -> gson.fromJson(data, Intent.Generic.Ready::class.java)
            else -> null
        }
    }

    operator fun Regex.contains(text: CharSequence): Boolean = this.matches(text)
}
