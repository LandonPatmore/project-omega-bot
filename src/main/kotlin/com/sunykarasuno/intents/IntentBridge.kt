package com.sunykarasuno.intents

import com.jakewharton.rxrelay3.PublishRelay
import com.sunykarasuno.intents.models.Intent
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.functions.Consumer

class IntentBridge : IntentService, IntentController {
    private val relay = PublishRelay.create<Intent>()
    override val consumer: Consumer<Intent>
        get() = relay
    override val eventStream: Observable<Intent>
        get() = relay
}
