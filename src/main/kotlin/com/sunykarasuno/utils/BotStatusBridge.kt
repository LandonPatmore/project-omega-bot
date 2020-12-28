package com.sunykarasuno.utils

import com.jakewharton.rxrelay3.PublishRelay
import com.sunykarasuno.utils.models.BotStatus
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.functions.Consumer
import mu.KotlinLogging

private val logger = KotlinLogging.logger {}

class BotStatusBridge : BotStatusService, BotStatusController {
    private val relay = PublishRelay.create<BotStatus>()
    override val consumer: Consumer<BotStatus>
        get() = relay
    override val eventStream: Observable<BotStatus>
        get() = relay.doOnNext {
            logger.debug {
                "Bot status: ${
                when (it) {
                    BotStatus.Startup -> "Startup"
                    BotStatus.Running -> "Running"
                    BotStatus.Shutdown -> "Shutdown"
                }
                }"
            }
        }
}
