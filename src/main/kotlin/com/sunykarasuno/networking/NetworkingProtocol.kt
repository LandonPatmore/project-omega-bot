package com.sunykarasuno.networking

interface NetworkingProtocol {
    fun createConnection(url: String = "")

    fun closeConnection(closeCode: Int)
}
