package com.sunykarasuno.networking.websockets

import com.google.gson.JsonObject

class GatewayIntentInterpreter : Interpreter {
    override fun consumeIntent(type: String, data: JsonObject) {
    }

    override fun consumeGuildGenericIntent() {
        TODO("Not yet implemented")
    }

    override fun consumeRoleIntent() {
        TODO("Not yet implemented")
    }

    override fun consumeChannelIntent() {
        TODO("Not yet implemented")
    }

    override fun consumeMemberIntent() {
        TODO("Not yet implemented")
    }

    override fun consumeBanIntent() {
        TODO("Not yet implemented")
    }

    override fun consumeEmojisIntent() {
        TODO("Not yet implemented")
    }

    override fun consumeIntegrationsIntent() {
        TODO("Not yet implemented")
    }

    override fun consumeInvitesIntent() {
        TODO("Not yet implemented")
    }

    override fun consumeVoiceIntent() {
        TODO("Not yet implemented")
    }

    override fun consumePresenceIntent() {
        TODO("Not yet implemented")
    }

    override fun consumeReactionIntent() {
        TODO("Not yet implemented")
    }

    override fun consumeTypingIntent() {
        TODO("Not yet implemented")
    }

    override fun consumeMessageIntent() {
        TODO("Not yet implemented")
    }

    override fun consumeDirectReaction() {
        TODO("Not yet implemented")
    }

    override fun consumeDirectTypingIntent() {
        TODO("Not yet implemented")
    }
}
