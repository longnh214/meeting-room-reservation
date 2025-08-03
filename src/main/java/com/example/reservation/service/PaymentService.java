package com.example.reservation.service;

import com.example.reservation.dto.PaymentDto;
import com.example.reservation.entity.Payment;
import com.example.reservation.type.PaymentProviderType;

public interface PaymentService {
    Payment createPaymentPending(Long reservationId, PaymentProviderType paymentProviderType);
    PaymentDto.Response requestPaymentExternalService(Long reservationId, PaymentProviderType paymentProviderType);
    PaymentDto.Response getPaymentStatus(Long paymentId);
    void updatePayment(Long paymentId, String externalPaymentId);
    PaymentDto.Response handleWebhook(PaymentProviderType provider, PaymentDto.WebhookRequest webhookRequest);
}