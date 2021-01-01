package com.sunykarasuno.networking.websockets

import com.google.gson.Gson
import com.google.gson.JsonParser
import com.sunykarasuno.networking.NetworkingProtocol
import com.sunykarasuno.networking.rest.DiscordService
import com.sunykarasuno.intents.GatewayIntentInterpreter
import com.sunykarasuno.networking.websockets.models.Heartbeat
import com.sunykarasuno.networking.websockets.models.HeartbeatAck
import com.sunykarasuno.networking.websockets.models.Hello
import com.sunykarasuno.networking.websockets.models.Identify
import com.sunykarasuno.networking.websockets.models.ReceivableGatewayEvent
import com.sunykarasuno.networking.websockets.models.Resume
import com.sunykarasuno.utils.extensions.NetworkingExtensions.json
import com.sunykarasuno.utils.models.BotStatus
import com.sunykarasuno.utils.status.BotStatusController
import com.sunykarasuno.utils.status.BotStatusService
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
    private val botStatusController: BotStatusController,
    botStatusService: BotStatusService
) : NetworkingProtocol {
    private val gson = Gson()
    private val jsonParser = JsonParser()
    private var websSocket: WebSocket? = null
    private val latestSequenceNumber = AtomicInteger(0)
    private var ackReceived = AtomicBoolean(true)
    private var heartbeatTimer: Timer? = null
    private var connectionThread: Thread? = null

    // TODO: Most likely want to move these to optionals
    private var webSocketUrl: String = ""
    private var sessionId: String = ""

    init {
        // if the bot shuts down from anywhere, make sure to close the webSocket appropriately
        botStatusService.eventStream
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
            botStatusController.consumer.accept(BotStatus.Shutdown)
            return
        }

        connectionThread = thread(true) {
            try {
                websSocket = newHttpClient().newWebSocket(
                    Request.Builder().url(webSocketUrl).build(),
                    object : WebSocketListener() {
                        override fun onOpen(webSocket: WebSocket, response: Response) {
                            super.onOpen(webSocket, response)
                            logger.debug { "Connection is open: $response" }
                        }

                        override fun onMessage(webSocket: WebSocket, text: String) {
                            super.onMessage(webSocket, text)
                            logger.debug { "Got a message: $text" }

                            when (jsonParser.parse(text).asJsonObject.get("op").asInt) {
                                0 -> {
                                    logger.info { "Got a dispatch message: $text" }
                                    handleDispatch(text)
                                }
                                1 -> {
                                    logger.info { "Received heartbeat message" }
                                    websSocket?.json(HeartbeatAck())
                                }
                                7 -> {
                                    logger.info { "Received reconnect message" }
                                    resumeSequence()
                                }
                                9 -> {
                                    logger.info { "Received invalid session message" }
                                    handleInvalidSession(text)
                                }
                                10 -> {
                                    logger.info { "Received hello message" }
                                    startSequence(text)
                                }
                                11 -> {
                                    logger.info { "Received heartbeat ack message" }
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

                        override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
                            super.onClosed(webSocket, code, reason)
                            logger.debug { "Closed because: $reason" }
                            shutdown(FAIL_CLOSE_CODE)
                        }

                        override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
                            super.onFailure(webSocket, t, response)
                            logger.error(t) { "WebSocket failure" }
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
        websSocket?.close(closeCode, "code: $closeCode")
            ?: logger.debug { "WebSocket could not be closed because it was not open" }
    }

    private fun shutdown(code: Int = DEFAULT_CLOSE_CODE) {
        // TODO: Notify us somehow and shut the bot down
        connectionThread?.interrupt()
        botStatusController.consumer.accept(BotStatus.Shutdown)
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
                logger.debug { "Closing because of zombie connection" }
                closeConnection(ZOMBIE_CLOSE_CODE)
            }
        }
    }

    private fun handleDispatch(text: String) {
        val json = gson.fromJson(text, ReceivableGatewayEvent::class.java)

        latestSequenceNumber.set(json.sequence)
        if (json.type == "READY") {
            sessionId = json.data.get("session_id").asString
        }
        gatewayIntentInterpreter.consumeIntent(json.type, json.data)
    }

    private fun handleInvalidSession(text: String) {
        val json = gson.fromJson(text, ReceivableGatewayEvent::class.java)

        if (json.data.asBoolean) {
            resumeSequence()
        } else {
            closeConnection(DEFAULT_CLOSE_CODE)
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
