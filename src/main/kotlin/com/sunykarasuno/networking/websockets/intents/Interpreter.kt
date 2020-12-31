package com.sunykarasuno.networking.websockets.intents

import com.google.gson.JsonObject
import com.sunykarasuno.models.Intent

interface Interpreter {
    fun consumeIntent(type: String, data: JsonObject)

    fun consumeGuildIntent(type: String, data: JsonObject): Intent.Guild?

    fun consumeRoleIntent(type: String, data: JsonObject): Intent.Roles?

    fun consumeChannelIntent(type: String, data: JsonObject): Intent.Channels?

    fun consumeMemberIntent(type: String, data: JsonObject): Intent.Members?

    fun consumeBanIntent(type: String, data: JsonObject): Intent.Ban?

    fun consumeEmojisIntent(type: String, data: JsonObject): Intent.Emojis?

    fun consumeInvitesIntent(type: String, data: JsonObject): Intent.Invites?

    fun consumeVoiceIntent(type: String, data: JsonObject): Intent.Voice?

    fun consumePresenceIntent(type: String, data: JsonObject): Intent.Presences?

    fun consumeReactionIntent(type: String, data: JsonObject): Intent.Reactions?

    fun consumeTypingIntent(type: String, data: JsonObject): Intent.Typing?

    fun consumeMessageIntent(type: String, data: JsonObject): Intent.Messages?

    fun consumeGenericIntent(type: String, data: JsonObject): Intent.Generic?
}
