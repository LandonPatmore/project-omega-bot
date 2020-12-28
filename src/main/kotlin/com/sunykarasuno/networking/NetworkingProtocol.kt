package com.sunykarasuno.networking

interface NetworkingProtocol {
    fun createConnection(webSocketUrl: String)

    fun closeConnection(closeCode: Int)
}
