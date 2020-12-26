package com.sunykarasuno.networking.websockets

import com.google.gson.JsonObject

interface Interpreter {
    fun consumeIntent(type: String, data: JsonObject)

    fun consumeGuildGenericIntent()

    fun consumeRoleIntent()

    fun consumeChannelIntent()

    fun consumeMemberIntent()

    fun consumeBanIntent()

    fun consumeEmojisIntent()

    fun consumeIntegrationsIntent()

    fun consumeInvitesIntent()

    fun consumeVoiceIntent()

    fun consumePresenceIntent()

    fun consumeReactionIntent()

    fun consumeTypingIntent()

    fun consumeMessageIntent()

    fun consumeDirectReaction()

    fun consumeDirectTypingIntent()
}
