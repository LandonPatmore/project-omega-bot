package com.sunykarasuno.utils.status

import com.sunykarasuno.utils.models.BotStatus
import io.reactivex.rxjava3.core.Observable

interface StatusService {
    val eventStream: Observable<BotStatus>
}
