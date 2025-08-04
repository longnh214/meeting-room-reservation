package com.example.reservation.service.impl;

import com.example.reservation.dto.PaymentDto;
import com.example.reservation.service.PaymentProviderExternalService;
import com.example.reservation.type.PaymentStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
public class PaymentProviderExternalServiceImpl implements PaymentProviderExternalService {
    @Override
    public CompletableFuture<PaymentDto.Response> requestPaymentExternalApi(PaymentDto.RequestExternal request) {
        PaymentDto.Response response = null;
        //TODO 외부 API 호출
        try {
            Thread.sleep(1000);
        } catch(InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            //TODO 반환 값 변환 Mapper 구현 후 적용
            response = PaymentDto.Response.builder()
                    .externalPaymentId(UUID.randomUUID().toString())
                    .paymentStatus(PaymentStatus.SUCCESS)
                    .build();
        }
        return CompletableFuture.completedFuture(response);
    }

    @Override
    public CompletableFuture<PaymentDto.CancelResponse> cancelPayment(String externalPaymentId) {
        PaymentDto.CancelResponse response = null;
        //TODO 외부 API 호출
        try {
            Thread.sleep(1000);
        } catch(InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            response = PaymentDto.CancelResponse.builder()
                    .externalPaymentId(UUID.randomUUID().toString())
                    .paymentStatus(PaymentStatus.REFUNDED)
                    .build();
        }
        return CompletableFuture.completedFuture(response);
    }
}
