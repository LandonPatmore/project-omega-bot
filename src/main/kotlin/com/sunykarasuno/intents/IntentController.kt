package com.sunykarasuno.intents

import com.sunykarasuno.intents.models.Intent
import io.reactivex.rxjava3.functions.Consumer

interface IntentController {
    val consumer: Consumer<Intent>
}
