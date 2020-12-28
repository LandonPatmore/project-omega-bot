package com.sunykarasuno.networking.websockets

import com.sunykarasuno.networking.websockets.models.Intent
import io.reactivex.rxjava3.functions.Consumer

interface IntentController {
    val consumer: Consumer<Intent>
}
