package com.example.reservation.service;

import com.example.reservation.dto.PaymentDto;

import java.util.concurrent.CompletableFuture;

public interface PaymentProviderExternalService {
    CompletableFuture<PaymentDto.Response> requestPaymentExternalApi(PaymentDto.RequestExternal request);
    CompletableFuture<PaymentDto.CancelResponse> cancelPayment(String externalPaymentId);
}
