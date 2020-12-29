package com.sunykarasuno.networking.websockets.intents

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonObject
import com.sunykarasuno.models.Channel
import com.sunykarasuno.models.Intent
import com.sunykarasuno.models.Member
import com.sunykarasuno.models.Message
import com.sunykarasuno.models.Presence
import com.sunykarasuno.models.Reaction
import com.sunykarasuno.models.User
import com.sunykarasuno.utils.deserializers.PresenceDeserializer
import com.sunykarasuno.utils.deserializers.ReadyDeserializer
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
            "CHANNEL_CREATE" -> Intent.Channels.Create(gson.fromJson(data, Channel::class.java))
            "CHANNEL_UPDATE" -> Intent.Channels.Update(gson.fromJson(data, Channel::class.java))
            "CHANNEL_DELETE" -> Intent.Channels.Delete(gson.fromJson(data, Channel::class.java))
            else -> null
        }
    }

    override fun consumeMemberIntent(type: String, data: JsonObject): Intent.Members? {
        return when (type) {
            "GUILD_MEMBER_ADD" -> Intent.Members.Add(gson.fromJson(data, Member::class.java))
            "GUILD_MEMBER_UPDATE" -> Intent.Members.Update(gson.fromJson(data, Member::class.java))
            "GUILD_MEMBER_REMOVE" -> Intent.Members.Remove(
                data.get("guild_id").asString,
                gson.fromJson(data, User::class.java)
            )
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
            "PRESENCE_UPDATE" -> Intent.Presences.Update(
                GsonBuilder().registerTypeAdapter(Presence::class.java, PresenceDeserializer())
                    .create().fromJson(data, Presence::class.java)
            )
            else -> null
        }
    }

    override fun consumeReactionIntent(type: String, data: JsonObject): Intent.Reactions? {
        return when (type) {
            "MESSAGE_REACTION_ADD" -> Intent.Reactions.Add(gson.fromJson(data, Reaction::class.java))
            "MESSAGE_REACTION_REMOVE" -> gson.fromJson(data, Intent.Reactions.Remove::class.java)
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
            "MESSAGE_CREATE" -> Intent.Messages.Create(gson.fromJson(data, Message::class.java))
            "MESSAGE_UPDATE" -> Intent.Messages.Update(gson.fromJson(data, Message::class.java))
            "MESSAGE_DELETE" -> gson.fromJson(data, Intent.Messages.Delete::class.java)
            else -> null
        }
    }

    override fun consumeGenericIntent(type: String, data: JsonObject): Intent.Generic? {
        return when (type) {
            "READY" -> GsonBuilder()
                .registerTypeAdapter(Intent.Generic.Ready::class.java, ReadyDeserializer())
                .create().fromJson(data, Intent.Generic.Ready::class.java)
            else -> null
        }
    }

    operator fun Regex.contains(text: CharSequence): Boolean = this.matches(text)
}
