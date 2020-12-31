package com.sunykarasuno.networking.websockets.intents

import com.sunykarasuno.models.Intent
import io.reactivex.rxjava3.core.Observable

interface IntentService {
    val eventStream: Observable<Intent>
}
