package com.finance_dashboard.providercrypto

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.boot.runApplication

@SpringBootApplication
@ConfigurationPropertiesScan
class ProviderCryptoApplication

fun main(args: Array<String>) {
    runApplication<ProviderCryptoApplication>(*args)
}
