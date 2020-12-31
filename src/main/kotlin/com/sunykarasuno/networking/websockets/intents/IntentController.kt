package com.sunykarasuno.networking.websockets.intents

import com.sunykarasuno.models.Intent
import io.reactivex.rxjava3.functions.Consumer

interface IntentController {
    val consumer: Consumer<Intent>
}
