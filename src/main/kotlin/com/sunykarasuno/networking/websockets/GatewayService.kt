package com.sunykarasuno.networking.websockets

import com.google.gson.Gson
import com.jakewharton.rxrelay3.PublishRelay
import com.sunykarasuno.networking.models.ReceivableGatewayEvent
import com.sunykarasuno.networking.rest.DiscordService
import io.reactivex.rxjava3.functions.Consumer
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import java.util.concurrent.TimeUnit

class GatewayService(
    private val discordService: DiscordService
) : NetworkingProtocol {

    private val relay = PublishRelay.create<Any>()
    override val networkController: Consumer<Any>
        get() = relay
    override val networkingService: PublishRelay<Any>
        get() = relay
    private val gson = Gson()
    private var websSocket: WebSocket? = null

    init {
        // TODO: Needs to be retried based on Discord's docs
        discordService.getGateway()?.let {
            createConnection(it.url)
        } ?: println("Could not get gateway info")
    }

    override fun createConnection(webSocketUrl: String) {
        val client = OkHttpClient()
            .newBuilder()
            .connectTimeout(CONNECTION_TIMEOUT, TimeUnit.SECONDS)
            .retryOnConnectionFailure(true)
            .build()

        try {
            websSocket = client.newWebSocket(
                Request.Builder().url(webSocketUrl).build(),
                object : WebSocketListener() {
                    override fun onOpen(webSocket: WebSocket, response: Response) {
                        super.onOpen(webSocket, response)
                        println("Connection is open: $response")
                    }

                    override fun onMessage(webSocket: WebSocket, text: String) {
                        super.onMessage(webSocket, text)
                        println("Got a message: $text")
                        val message = gson.fromJson(
                            text,
                            ReceivableGatewayEvent::class.java
                        )

                        when (message.code) {
                            1 -> {
                                println("Received heartbeat message")
                                // Heartbeat, heartbeat ack back
                            }
                            10 -> {
                                println("Received hello message")
                                // Hello, we need to ack back 1 after heartbeat seconds
                                // We now need to identify with 2
                            }
                            11 -> {
                                println("Received heartbeat ack message")
                                // Heartbeat ack, no need to do anything, however close connection if going to
                                // send 1, but 11 was never received before this with a non-1000 code
                            }
                        }
                    }

                    override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
                        super.onClosing(webSocket, code, reason)
                    }

                    override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
                        super.onClosed(webSocket, code, reason)
                    }

                    override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
                        super.onFailure(webSocket, t, response)
                    }
                }
            )
        } catch (e: Exception) {
            // TODO: Need to consume this error, log it, and recreate the connection
        }
    }

    override fun closeConnection() {
        websSocket?.close(CLOSE_CODE, "Requested to shutdown by bot itself")
            ?: println("WebSocket could not be closed because it was not open")
    }

    companion object {
        private const val CONNECTION_TIMEOUT = 30L
        private const val CLOSE_CODE = 1000
    }
}