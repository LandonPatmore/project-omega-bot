package com.sunykarasuno.utils.models

sealed class BotStatus {
    object Running : BotStatus()
    object Shutdown : BotStatus()
}
