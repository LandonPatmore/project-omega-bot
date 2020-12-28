package com.sunykarasuno.networking.websockets

import com.sunykarasuno.networking.websockets.models.Intent
import io.reactivex.rxjava3.core.Observable

interface IntentService {
    val eventStream: Observable<Intent>
}
