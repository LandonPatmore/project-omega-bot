package com.sunykarasuno.utils.models

sealed class BotStatus {
    object Startup : BotStatus()
    object Running : BotStatus()
    object Shutdown : BotStatus()
}
