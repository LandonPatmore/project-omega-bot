package com.sunykarasuno.utils.status

import com.sunykarasuno.utils.models.BotStatus
import io.reactivex.rxjava3.core.Observable

interface BotStatusService {
    val eventStream: Observable<BotStatus>
}
