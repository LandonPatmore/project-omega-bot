package com.sunykarasuno.utils.status

import com.sunykarasuno.utils.models.BotStatus
import io.reactivex.rxjava3.functions.Consumer

interface BotStatusController {
    val consumer: Consumer<BotStatus>
}
