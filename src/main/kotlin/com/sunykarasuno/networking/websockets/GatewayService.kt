package com.sunykarasuno.networking.websockets

import com.google.gson.Gson
import com.google.gson.JsonParser
import com.jakewharton.rxrelay3.PublishRelay
import com.sunykarasuno.networking.models.GatewayHeartbeat
import com.sunykarasuno.networking.models.GatewayHeartbeatAck
import com.sunykarasuno.networking.models.GatewayHello
import com.sunykarasuno.networking.models.GatewayIdentify
import com.sunykarasuno.networking.models.GatewayInvalid
import com.sunykarasuno.networking.models.GatewayResume
import com.sunykarasuno.networking.rest.DiscordService
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.functions.Consumer
import io.reactivex.rxjava3.schedulers.Schedulers
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicInteger

class GatewayService(
    discordService: DiscordService,
    private val token: String,
    private val gatewayIntentInterpreter: GatewayIntentInterpreter
) : NetworkingProtocol {

    private val relay = PublishRelay.create<Any>()
    override val networkController: Consumer<Any>
        get() = relay
    override val networkingService: PublishRelay<Any>
        get() = relay
    private val gson = Gson()
    private var websSocket: WebSocket? = null
    private val latestSequenceNumber = AtomicInteger(0)
    private var sessionId: String = ""
    private var ackReceived = AtomicBoolean(false)

    init {
        // TODO: Needs to be retried based on Discord's docs
        discordService.getGateway()?.let {
            createConnection("${it.url}/?v=$VERSION")
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

                        val json = JsonParser().parse(text).asJsonObject

                        when (json.get("op").asInt) {
                            0 -> {
                                println("Got a dispatch message: $text")
                                latestSequenceNumber.set(json.get("s").asInt)
                                gatewayIntentInterpreter.consumeIntent(json.get("t").asString, json.get("d").asJsonObject)
                            }
                            1 -> {
                                println("Received heartbeat message")
                                websSocket?.send(json(GatewayHeartbeatAck()))
                            }
                            7 -> {
                                println("Received reconnect message")
                                resumeSequence()
                            }
                            9 -> {
                                println("Received invalid session message")
                                val invalid = gson.fromJson(
                                    text,
                                    GatewayInvalid::class.java
                                )

                                if (invalid.isResumable) {
                                    resumeSequence()
                                } else {
                                    closeConnection(DEFAULT_CLOSE_CODE)
                                }
                            }
                            10 -> {
                                println("Received hello message")
                                startSequence(text)
                            }
                            11 -> {
                                println("Received heartbeat ack message")
                                ackReceived.set(true)
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

    override fun closeConnection(closeCode: Int) {
        websSocket?.close(closeCode, "Connection closed with code: $closeCode")
            ?: println("WebSocket could not be closed because it was not open")
        // start back up, and send a gateway resume instead of identify
    }

    private fun startSequence(json: String) {
        setHeartbeatInterval(
            gson.fromJson(
                json,
                GatewayHello::class.java
            ).data.heartbeatInterval
        )
        websSocket?.send(json(createIdentifyMessage()))
    }

    private fun resumeSequence() {
        websSocket?.send(json(createResumeMessage()))
    }

    private fun createResumeMessage(): GatewayResume {
        return GatewayResume(GatewayResume.ResumeInfo(token, sessionId, latestSequenceNumber.get()))
    }

    private fun createIdentifyMessage(): GatewayIdentify {
        return GatewayIdentify(
            GatewayIdentify.IdentificationInfo(
                token,
                INTENT,
                GatewayIdentify.PropertiesInfo(
                    OS,
                    LIBRARY,
                    LIBRARY
                )
            )
        )
    }

    private fun setHeartbeatInterval(interval: Int) {
        // TODO: Need to be able to stop this, probably use a switch map that wraps this
        val longInterval = interval.toLong()
        Observable.interval(longInterval, longInterval, TimeUnit.MILLISECONDS, Schedulers.io())
            .subscribe {
                println("Sending heartbeat")
                // TODO: Make sure ack was received, otherwise close connection since it is a zombie connection

                if(ackReceived.get()) {
                    websSocket?.send(json(GatewayHeartbeat(latestSequenceNumber.get())))
                    ackReceived.set(false)
                } else {
                    closeConnection(ZOMBIE_CODE)
                    // TODO: Stop interval
                }
            }
    }

    private fun json(obj: Any): String {
        return gson.toJson(obj)
    }

    companion object {
        private const val INTENT = 32735
        private const val VERSION = 8
        private const val CONNECTION_TIMEOUT = 30L
        private const val DEFAULT_CLOSE_CODE = 1000
        private const val ZOMBIE_CODE = 42
        private const val OS = "Linux"
        private const val LIBRARY = "Omega"
    }
}
