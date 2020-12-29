package com.sunykarasuno.networking.websockets

import com.google.gson.Gson
import com.sunykarasuno.networking.NetworkingProtocol
import com.sunykarasuno.networking.rest.DiscordService
import com.sunykarasuno.networking.websockets.intents.GatewayIntentInterpreter
import com.sunykarasuno.networking.websockets.models.Heartbeat
import com.sunykarasuno.networking.websockets.models.HeartbeatAck
import com.sunykarasuno.networking.websockets.models.Hello
import com.sunykarasuno.networking.websockets.models.Identify
import com.sunykarasuno.networking.websockets.models.ReceivableGatewayEvent
import com.sunykarasuno.networking.websockets.models.Resume
import com.sunykarasuno.utils.extensions.NetworkingExtensions.json
import com.sunykarasuno.utils.models.BotStatus
import com.sunykarasuno.utils.status.StatusController
import com.sunykarasuno.utils.status.StatusService
import mu.KotlinLogging
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import java.util.Timer
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicInteger
import kotlin.concurrent.fixedRateTimer
import kotlin.concurrent.thread

private val logger = KotlinLogging.logger {}

class GatewayService(
    private val discordService: DiscordService,
    private val token: String,
    private val gatewayIntentInterpreter: GatewayIntentInterpreter,
    private val statusController: StatusController,
    statusService: StatusService
) : NetworkingProtocol {
    private val gson = Gson()
    private var websSocket: WebSocket? = null
    private val latestSequenceNumber = AtomicInteger(0)
    private var ackReceived = AtomicBoolean(false)
    private var heartbeatTimer: Timer? = null
    private var connectionThread: Thread? = null

    // TODO: Most likely want to move these to optionals
    private var webSocketUrl: String = ""
    private var sessionId: String = ""

    init {
        // if the bot shuts down from anywhere, make sure to close the webSocket appropriately
        statusService.eventStream
            .filter {
                it == BotStatus.Shutdown
            }.subscribe { closeConnection(DEFAULT_CLOSE_CODE) }
    }

    override fun createConnection(url: String) {
        connectionThread?.interrupt()

        // TODO: Move this out once there is a way to start web socket from somewhere else to pass the url in
        discordService.getGateway()?.let {
            webSocketUrl = "${it.url}/?v=$VERSION"
        } ?: run {
            logger.debug { "Could not get gateway info" }
            statusController.consumer.accept(BotStatus.Shutdown)
            return
        }

        connectionThread = thread(true) {
            try {
                websSocket = newHttpClient().newWebSocket(
                    Request.Builder().url(url).build(),
                    object : WebSocketListener() {
                        override fun onOpen(webSocket: WebSocket, response: Response) {
                            super.onOpen(webSocket, response)
                            logger.debug { "Connection is open: $response" }
                        }

                        override fun onMessage(webSocket: WebSocket, text: String) {
                            super.onMessage(webSocket, text)
                            val json = gson.fromJson(text, ReceivableGatewayEvent::class.java)

                            when (json.code) {
                                0 -> {
                                    logger.debug { "Got a dispatch message: $text" }
                                    latestSequenceNumber.set(json.sequence)
                                    if (json.type == "READY") {
                                        sessionId = json.data.get("session_id").asString
                                    }
                                    gatewayIntentInterpreter.consumeIntent(json.type, json.data)
                                }
                                1 -> {
                                    logger.debug { "Received heartbeat message" }
                                    websSocket?.json(HeartbeatAck())
                                }
                                7 -> {
                                    logger.debug { "Received reconnect message" }
                                    resumeSequence()
                                }
                                9 -> {
                                    logger.debug { "Received invalid session message" }
                                    if (json.data.asBoolean) {
                                        resumeSequence()
                                    } else {
                                        closeConnection(DEFAULT_CLOSE_CODE)
                                    }
                                }
                                10 -> {
                                    logger.debug { "Received hello message" }
                                    startSequence(text)
                                    ackReceived.set(true)
                                }
                                11 -> {
                                    logger.debug { "Received heartbeat ack message" }
                                    ackReceived.set(true)
                                }
                            }
                        }

                        override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
                            super.onClosing(webSocket, code, reason)
                            logger.debug { "Discord closed socket with code $code because: $reason" }
                            when (code) {
                                4000 -> createConnection(webSocketUrl)
                                else -> shutdown(code)
                            }
                        }

                        override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
                            super.onFailure(webSocket, t, response)
                            shutdown(FAIL_CLOSE_CODE)
                        }
                    }
                )
            } catch (e: Exception) {
                logger.error(e) { "Exception was thrown" }
                shutdown(FAIL_CLOSE_CODE)
            }
        }
    }

    override fun closeConnection(closeCode: Int) {
        websSocket?.close(closeCode, "Connection closed with code: $closeCode")
            ?: logger.debug { "WebSocket could not be closed because it was not open" }
    }

    private fun shutdown(code: Int = DEFAULT_CLOSE_CODE) {
        // TODO: Notify us somehow and shut the bot down
        connectionThread?.interrupt()
        closeConnection(code)
        statusController.consumer.accept(BotStatus.Shutdown)
    }

    private fun startSequence(json: String) {
        setHeartbeatInterval(gson.fromJson(json, Hello::class.java).data.heartbeatInterval)
        websSocket?.json(
            Identify(
                Identify.IdentificationInfo(
                    token,
                    INTENT_CODE,
                    Identify.PropertiesInfo(
                        OS,
                        LIBRARY,
                        LIBRARY
                    )
                )
            )
        )
    }

    private fun resumeSequence() {
        websSocket?.json(Resume(Resume.ResumeInfo(token, sessionId, latestSequenceNumber.get())))
    }

    private fun setHeartbeatInterval(interval: Int) {
        val longInterval = interval.toLong()
        heartbeatTimer?.cancel()

        heartbeatTimer = fixedRateTimer("HeartbeatInterval", false, longInterval, longInterval) {
            if (ackReceived.get()) {
                logger.debug { "Sending heartbeat" }
                websSocket?.json(Heartbeat(latestSequenceNumber.get()))
                ackReceived.set(false)
            } else {
                closeConnection(ZOMBIE_CLOSE_CODE)
            }
        }
    }

    private fun newHttpClient(): OkHttpClient {
        return OkHttpClient()
            .newBuilder()
            .connectTimeout(CONNECTION_TIMEOUT, TimeUnit.SECONDS)
            .retryOnConnectionFailure(true)
            .build()
    }

    companion object {
        private const val INTENT_CODE = 32719
        private const val VERSION = 8
        private const val CONNECTION_TIMEOUT = 30L
        private const val DEFAULT_CLOSE_CODE = 1000
        private const val ZOMBIE_CLOSE_CODE = 4242
        private const val FAIL_CLOSE_CODE = 4343
        private const val OS = "Linux"
        private const val LIBRARY = "Omega"
    }
}
