package com.sunykarasuno.intents

import com.sunykarasuno.intents.models.Intent
import io.reactivex.rxjava3.core.Observable

interface IntentService {
    val eventStream: Observable<Intent>
}
