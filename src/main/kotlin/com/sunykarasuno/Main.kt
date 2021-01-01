package com.sunykarasuno

import com.sunykarasuno.intents.GatewayIntentInterpreter
import com.sunykarasuno.intents.IntentBridge
import com.sunykarasuno.intents.models.Intent
import com.sunykarasuno.networking.rest.DiscordService
import com.sunykarasuno.networking.rest.models.ChannelModify
import com.sunykarasuno.networking.websockets.GatewayService
import com.sunykarasuno.utils.state.State
import com.sunykarasuno.utils.state.StateArbiter
import com.sunykarasuno.utils.status.BotStatusBridge

fun main() {
    // TODO: Set this properly for prod
    System.setProperty(org.slf4j.impl.SimpleLogger.DEFAULT_LOG_LEVEL_KEY, "DEBUG")
    val d = DiscordService(System.getenv("BOT_TOKEN"))
    val i = IntentBridge()

    val st = StateArbiter()

    i.eventStream
        .ofType(Intent.Generic.Ready::class.java)
        .subscribe {
            st.modifyState(State(it.id))
        }

    i.eventStream
        .ofType(Intent.Messages.Create::class.java)
        .filter {
            it.message.mentions.filter { user ->
                user.id == st.getState().id
            }.firstOrNull() != null
        }.map {
            Pair(it.message.channelId, it.message)
        }.subscribe {
            d.sendMessage(it.first, "Hey there <@!${it.second.author.id}>, you are in guild \"${it.second.guildId}\"!")
        }

    val s = BotStatusBridge()
    val g = GatewayService(d, System.getenv("BOT_TOKEN"), GatewayIntentInterpreter(i), s, s)
    g.createConnection()
}
