package com.sunykarasuno

import com.jakewharton.rxrelay3.PublishRelay
import com.sunykarasuno.models.Intent
import com.sunykarasuno.networking.rest.DiscordService
import com.sunykarasuno.networking.websockets.GatewayService
import com.sunykarasuno.networking.websockets.intents.GatewayIntentInterpreter
import com.sunykarasuno.networking.websockets.intents.IntentController
import com.sunykarasuno.utils.models.BotStatus
import com.sunykarasuno.utils.status.StatusController
import com.sunykarasuno.utils.status.StatusService
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.functions.Consumer

fun main() {
    // TODO: Set this properly for prod
    System.setProperty(org.slf4j.impl.SimpleLogger.DEFAULT_LOG_LEVEL_KEY, "DEBUG")
    val d = DiscordService(System.getenv("BOT_TOKEN"))
    val q = PublishRelay.create<BotStatus>()
    val g = GatewayService(
        d, System.getenv("BOT_TOKEN"),
        GatewayIntentInterpreter(object : IntentController {
            override val consumer: Consumer<Intent>
                get() = PublishRelay.create()
        }),
        object : StatusController {
            override val consumer: Consumer<BotStatus>
                get() = q
        },
        object : StatusService {
            override val eventStream: Observable<BotStatus>
                get() = q
        }
    )
    g.createConnection()
}
