package com.sunykarasuno

import com.sunykarasuno.networking.rest.DiscordService
import com.sunykarasuno.networking.websockets.GatewayService

fun main() {
    val d = DiscordService(System.getenv("TOKEN"))
    GatewayService(d, System.getenv("TOKEN"))
}
