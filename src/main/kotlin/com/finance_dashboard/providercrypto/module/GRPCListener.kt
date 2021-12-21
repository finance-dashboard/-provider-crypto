package com.finance_dashboard.providercrypto.module

import finance_dashboard.CurrencyProviderGrpc
import finance_dashboard.CurrencyService
import io.grpc.stub.StreamObserver
import net.devh.boot.grpc.server.service.GrpcService

@GrpcService
class GRPCListener(
    private val coinMarketService: CoinMarketService
) : CurrencyProviderGrpc.CurrencyProviderImplBase() {

    override fun getCurrency(
        request: CurrencyService.TimeSlice?,
        responseObserver: StreamObserver<CurrencyService.Value>?
    ) {
        if (request != null) {
            coinMarketService.getDateValuePrices(request).forEach {
                responseObserver?.onNext(
                    CurrencyService.Value.newBuilder()
                        .setValue(it)
                        .build()
                )
            }
        }
        responseObserver?.onCompleted()
    }
}