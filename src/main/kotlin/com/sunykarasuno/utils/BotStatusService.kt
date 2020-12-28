package com.sunykarasuno.utils

import com.sunykarasuno.utils.models.BotStatus
import io.reactivex.rxjava3.core.Observable

interface BotStatusService {
    val eventStream: Observable<BotStatus>
}
