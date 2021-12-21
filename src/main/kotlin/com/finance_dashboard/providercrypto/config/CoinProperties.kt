package com.finance_dashboard.providercrypto.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConstructorBinding

@ConstructorBinding
@ConfigurationProperties(prefix = "app.coin")
data class CoinProperties(
    val apiKey: String = "",
    val apiUri: String = ""
)

