package com.sunykarasuno.networking.websockets

interface NetworkingProtocol : NetworkingController, NetworkingService {
    fun createConnection(webSocketUrl: String)

    fun closeConnection()
}
