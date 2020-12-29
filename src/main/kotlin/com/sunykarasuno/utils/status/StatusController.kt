package com.sunykarasuno.utils.status

import com.sunykarasuno.utils.models.BotStatus
import io.reactivex.rxjava3.functions.Consumer

interface StatusController {
    val consumer: Consumer<BotStatus>
}
