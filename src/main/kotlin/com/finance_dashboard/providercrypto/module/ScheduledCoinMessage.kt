package com.finance_dashboard.providercrypto.module

import org.slf4j.LoggerFactory
import org.springframework.messaging.simp.SimpMessagingTemplate
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service

@Service
class ScheduledCoinMessage(
    private val coinMarketService: CoinMarketService,
    private val simpMessagingTemplate: SimpMessagingTemplate
) {

    private val logger = LoggerFactory.getLogger(ScheduledCoinMessage::class.java)

    @Scheduled(fixedRateString = "\${app.coin.delay}")
    fun sendMessage() {
        logger.info("Sending coins to clients...")
        coinMarketService.getCoinList().forEach {
            simpMessagingTemplate.convertAndSend(
                "/coin/fetch",
                it
            )
        }
    }
}