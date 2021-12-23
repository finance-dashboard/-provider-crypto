package com.finance_dashboard.providercrypto.config

import com.finance_dashboard.providercrypto.module.WebSocketHandler
import org.springframework.context.annotation.Configuration
import org.springframework.scheduling.annotation.EnableScheduling
import org.springframework.web.socket.config.annotation.WebSocketConfigurer
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry

@Configuration
class WebSocketConfig(
    private val webSocketHandler: WebSocketHandler
) : WebSocketConfigurer {
    override fun registerWebSocketHandlers(registry: WebSocketHandlerRegistry) {
        registry.addHandler(webSocketHandler, "/api/v1/coins").setAllowedOrigins("*")
    }
}

@Configuration
@EnableScheduling
class SchedulerConfig