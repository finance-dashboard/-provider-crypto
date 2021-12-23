package com.finance_dashboard.providercrypto.module

import com.finance_dashboard.providercrypto.config.CoinProperties
import com.finance_dashboard.providercrypto.model.CoinDto
import com.finance_dashboard.providercrypto.model.Cost
import com.google.gson.Gson
import com.google.gson.JsonObject
import finance_dashboard.CurrencyService
import org.apache.http.HttpEntity
import org.apache.http.HttpHeaders
import org.apache.http.NameValuePair
import org.apache.http.client.methods.CloseableHttpResponse
import org.apache.http.client.methods.HttpGet
import org.apache.http.client.utils.URIBuilder
import org.apache.http.impl.client.CloseableHttpClient
import org.apache.http.impl.client.HttpClients
import org.apache.http.message.BasicNameValuePair
import org.apache.http.util.EntityUtils
import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Bean
import org.springframework.stereotype.Service
import java.io.IOException
import java.net.URISyntaxException
import java.util.*

@Service
class CoinMarketService(
    coinProperties: CoinProperties
) {

    private val baseUri = coinProperties.apiUri
    private val apikey = coinProperties.apiKey
    private val coins = coinProperties.coins
    private val logger = LoggerFactory.getLogger(CoinMarketService::class.java)

    fun getDateValuePrices(request: CurrencyService.TimeSlice): MutableList<Float> {
        logger.info("From GRPC: ${request.start}, ${request.end}, ${request.currencyCode}")
        val amountValues = mutableListOf<Float>()
        val uri = "$baseUri/prices/${request.currencyCode}/spot"
        val parameters: MutableList<NameValuePair> = ArrayList<NameValuePair>()
        //start
        parameters.add(BasicNameValuePair("date", request.start))
        parameters.clear()
        getAmountValue(uri, parameters, amountValues)
        //end
        parameters.add(BasicNameValuePair("date", request.end))
        getAmountValue(uri, parameters, amountValues)
        return amountValues
    }

    private fun getAmountValue(
        uri: String,
        parameters: MutableList<NameValuePair>,
        answerValues: MutableList<Float>
    ) {
        var result = ""
        try {
            result = makeAPICall(uri, parameters, true)
            logger.info("")
        } catch (e: IOException) {
            logger.error("Error: cannot access content - $e")
        } catch (e: URISyntaxException) {
            logger.error("Error: Invalid URL $e")
        }
        val fromJson = Gson().fromJson(result, JsonObject::class.java)
        val data = fromJson.get("data") as JsonObject
        answerValues.add(
            data.get("amount").asFloat
        )
    }

    fun getCoinList(): MutableList<CoinDto> {
        val coins = coins.split(";").toList()
        val coinList = mutableListOf<CoinDto>()
        coins.forEach {
            var fromJson = JsonObject()
            try {
                fromJson = Gson().fromJson(
                    makeAPICall("https://data.messari.io/api/v1/assets/$it/metrics", listOf()),
                    JsonObject::class.java
                )
            } catch (e: Exception) {
                logger.error("Error fetch coins ticker - $e")
            }
            val time = (fromJson.get("status") as JsonObject).get("timestamp").asString

            val data = fromJson.get("data") as JsonObject
            val name = data.get("name").asString
            val ticker = data.get("symbol").asString
            val marketData = (data.get("market_data") as JsonObject).get("ohlcv_last_1_hour") as JsonObject
            val high = marketData.get("high").asFloat
            val low = marketData.get("low").asFloat

            coinList.add(
                CoinDto(
                    time = time,
                    name = name,
                    ticker = ticker,
                    cost = Cost(high = high, low = low, currency = "USD")
                )
            )
        }
        return coinList
    }

    fun makeAPICall(uri: String?, parameters: List<NameValuePair?>?, aut: Boolean = false): String {
        var responseContent: String
        val query = URIBuilder(uri)
        query.addParameters(parameters)
        val client: CloseableHttpClient = HttpClients.createDefault()
        val request = HttpGet(query.build())
        request.setHeader(HttpHeaders.ACCEPT, "application/json")
        if (aut) request.addHeader("CB-ACCESS-KEY ", apikey)
        val response: CloseableHttpResponse = client.execute(request)
        response.use {
            //println(it.statusLine)
            val entity: HttpEntity = it.entity
            responseContent = EntityUtils.toString(entity)
            EntityUtils.consume(entity)
        }
        return responseContent
    }
}