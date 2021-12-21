package com.finance_dashboard.providercrypto.module

import org.springframework.messaging.simp.SimpMessagingTemplate
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service

@Service
class ScheduledCoinMessage(
    private val coinMarketService: CoinMarketService
) {

    @Scheduled(fixedRate = 5000)
    fun sendMessage(simpMessagingTemplate: SimpMessagingTemplate) {
        println("keks")
        simpMessagingTemplate.convertAndSend(
            "/coin/fetch",
            //OutputMessage("Chuck Norris (@Scheduled)", chuckNorris().fact(), time)
        )
    }
}