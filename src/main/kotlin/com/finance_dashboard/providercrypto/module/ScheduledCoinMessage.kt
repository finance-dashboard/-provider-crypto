package com.finance_dashboard.providercrypto.module

import org.springframework.messaging.simp.SimpMessagingTemplate
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service

@Service
class ScheduledCoinMessage(
    private val coinMarketService: CoinMarketService,
    private val simpMessagingTemplate: SimpMessagingTemplate
) {

    @Scheduled(fixedRate = 10000)
    fun sendMessage() {
        coinMarketService.getCoinList().forEach {
            simpMessagingTemplate.convertAndSend(
                "/coin/fetch",
                it
            )
        }
    }
}