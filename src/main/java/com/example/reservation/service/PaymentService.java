package com.example.service;

import com.example.entity.Payment;

public interface PaymentService {
    Payment requestPayment(Long reservationId, Long providerId);
    Payment getPaymentStatus(Long paymentId);
    Payment handleWebhook(Long providerId, String payload);
}