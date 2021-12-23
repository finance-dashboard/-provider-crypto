package com.finance_dashboard.providercrypto.module

import com.google.gson.Gson
import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import org.springframework.web.socket.CloseStatus
import org.springframework.web.socket.PongMessage
import org.springframework.web.socket.TextMessage
import org.springframework.web.socket.WebSocketSession
import org.springframework.web.socket.handler.TextWebSocketHandler
import java.io.IOException
import java.util.concurrent.ConcurrentHashMap


@Component
class WebSocketHandler(
    private val coinMarketService: CoinMarketService
) : TextWebSocketHandler() {

    private val logger = LoggerFactory.getLogger(WebSocketHandler::class.java)
    private val sessions: MutableMap<String, WebSocketSession> = ConcurrentHashMap()

    override fun afterConnectionEstablished(session: WebSocketSession) {
        logger.info("Connected ... " + session.id)
        sessions[session.id] = session
    }

    override fun afterConnectionClosed(session: WebSocketSession, status: CloseStatus) {
        logger.info(String.format("Session %s closed because of %s", session.id, status.reason))
        sessions.remove(session.id)
    }

    override fun handleTransportError(session: WebSocketSession, exception: Throwable) {
        logger.error("error occured at sender $session", exception)
    }

    override fun handleTextMessage(session: WebSocketSession, message: TextMessage) {
        logger.info("Handling message: {}", message)
    }

    @Scheduled(fixedRateString = "\${app.coin.delay}")
    fun sendMessage() {
        val coinList = coinMarketService.getCoinList()
        sessions.forEach {
            try {
                coinList.forEach { coin ->
                    val message = TextMessage(Gson().toJson(coin))
                    it.value.sendMessage(message)
                    logger.info("Send coins {} to socketId: {}", message, it.key)
                }
            } catch (e: IOException) {
                logger.error("Error sending message")
            }
        }
    }
}


