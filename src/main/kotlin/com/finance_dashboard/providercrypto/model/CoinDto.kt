package com.finance_dashboard.providercrypto.model

class CoinDto(
    var time: String = "",
    val name: String = "",
    val ticker: String = "",
    val cost: Cost? = null
)

class Cost(
    val high: Float = 0F,
    val low: Float = 0F,
    val currency: String = ""
)