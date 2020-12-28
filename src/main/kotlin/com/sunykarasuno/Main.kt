package com.sunykarasuno

import com.jakewharton.rxrelay3.PublishRelay
import com.sunykarasuno.networking.rest.DiscordService
import com.sunykarasuno.networking.websockets.GatewayIntentInterpreter
import com.sunykarasuno.networking.websockets.GatewayService
import com.sunykarasuno.networking.websockets.IntentController
import com.sunykarasuno.networking.websockets.models.Intent
import com.sunykarasuno.utils.BotStatusController
import com.sunykarasuno.utils.BotStatusService
import com.sunykarasuno.utils.models.BotStatus
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.functions.Consumer

fun main() {
    // TODO: Set this properly for prod
    System.setProperty(org.slf4j.impl.SimpleLogger.DEFAULT_LOG_LEVEL_KEY, "DEBUG");
    val d = DiscordService(System.getenv("TOKEN"))
    val q = PublishRelay.create<BotStatus>()
    GatewayService(d, System.getenv("TOKEN"), GatewayIntentInterpreter(object : IntentController{
        override val consumer: Consumer<Intent>
            get() = PublishRelay.create()

    }), object: BotStatusController {
        override val consumer: Consumer<BotStatus>
            get() = q
    }, object : BotStatusService {
        override val eventStream: Observable<BotStatus>
            get() = q

    })
}
