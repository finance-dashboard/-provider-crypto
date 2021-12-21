package com.finance_dashboard.providercrypto.module

import com.finance_dashboard.providercrypto.config.CoinProperties
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
import org.springframework.context.annotation.Bean
import org.springframework.stereotype.Service
import java.io.IOException
import java.net.URISyntaxException
import java.util.*

@Service
class CoinMarketService(
    private val coinProperties: CoinProperties
) {

    private val baseUri = coinProperties.apiUri
    private val apikey = coinProperties.apiKey

    fun getDateValuePrices(request: CurrencyService.TimeSlice): MutableList<Float> {
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
            result = makeAPICall(uri, parameters)
            println(result)
        } catch (e: IOException) {
            println("Error: cannot access content - $e")
        } catch (e: URISyntaxException) {
            println("Error: Invalid URL $e")
        }
        val fromJson = Gson().fromJson(result, JsonObject::class.java)
        val data = fromJson.get("data") as JsonObject
        answerValues.add(
            data.get("amount").asFloat
        )
    }

    fun makeAPICall(uri: String?, parameters: List<NameValuePair?>?): String {
        var responseContent = ""
        val query = URIBuilder(uri)
        query.addParameters(parameters)
        val client: CloseableHttpClient = HttpClients.createDefault()
        val request = HttpGet(query.build())
        request.setHeader(HttpHeaders.ACCEPT, "application/json")
        request.addHeader("CB-ACCESS-KEY ", apikey)
        val response: CloseableHttpResponse = client.execute(request)
        response.use {
            println(it.statusLine)
            val entity: HttpEntity = it.entity
            responseContent = EntityUtils.toString(entity)
            EntityUtils.consume(entity)
        }
        return responseContent
    }

}