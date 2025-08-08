package com.example.reservation.service.impl;

import com.example.reservation.dto.PaymentDto;
import com.example.reservation.entity.Payment;
import com.example.reservation.entity.PaymentProvider;
import com.example.reservation.entity.Reservation;
import com.example.reservation.repository.PaymentProviderRepository;
import com.example.reservation.repository.PaymentRepository;
import com.example.reservation.repository.ReservationRepository;
import com.example.reservation.service.PaymentProviderExternalService;
import com.example.reservation.service.PaymentService;
import com.example.reservation.type.PaymentProviderType;
import com.example.reservation.type.PaymentStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.aop.framework.AopContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentServiceImpl implements PaymentService {
    private final PaymentRepository paymentRepository;
    private final PaymentProviderRepository paymentProviderRepository;
    private final ReservationRepository reservationRepository;
    private final PaymentProviderExternalService externalService;

    @Override
    @Transactional
    public Payment createPaymentPending(Long reservationId, PaymentProviderType paymentProviderType) {
        Reservation reservation = reservationRepository
                .findById(reservationId)
                .orElseThrow(() -> new IllegalArgumentException("해당 예약 정보를 찾을 수 없습니다."));

        if(reservation.getPaymentStatus() == PaymentStatus.SUCCESS){
            throw new IllegalStateException("해당 예약은 이미 결제 완료된 상태입니다.");
        }

        PaymentProvider paymentProvider = paymentProviderRepository
                .findByPaymentProviderType(paymentProviderType)
                .orElseThrow(() -> new IllegalArgumentException("해당 결제사 정보를 찾을 수 없습니다."));

        Payment payment = Payment.builder()
                .reservation(reservation)
                .paymentProvider(paymentProvider)
                .measurementType(paymentProvider.getPaymentProviderType().getPaymentMeasurementType())
                .fee(reservation.getTotalFee())
                .paymentStatus(PaymentStatus.PENDING)
                .requestedAt(LocalDateTime.now())
                .build();

        return paymentRepository.save(payment);
    }

    public PaymentDto.Response requestPaymentExternalService(Long reservationId, PaymentProviderType paymentProviderType) {
        Payment payment = ((PaymentService) AopContext.currentProxy()).createPaymentPending(reservationId, paymentProviderType);

        CompletableFuture<PaymentDto.Response> pgFuture =
                externalService.requestPaymentExternalApi(
                        PaymentDto.RequestExternal.builder()
                                .fee(payment.getFee())
                                .paymentProviderType(payment.getPaymentProvider().getPaymentProviderType())
                                .apiEndpoint(payment.getPaymentProvider().getApiEndpoint())
                                .apiKey(payment.getPaymentProvider().getApiKey())
                                .build()
                );

        pgFuture.whenCompleteAsync((pgResponse, throwable) -> {
            if (throwable != null) {
                log.error("PG 결제가 실패했습니다. : ", throwable);
            } else {
                ((PaymentService) AopContext.currentProxy()).updatePayment(payment.getId(), pgResponse.getExternalPaymentId());
            }
        });

        return PaymentDto.toResponse(payment);
    }

    @Override
    @Transactional
    public void updatePayment(Long paymentId, String externalPaymentId) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new IllegalStateException("해당 결제 정보를 찾을 수 없습니다."));

        payment.setExternalPaymentId(externalPaymentId);

        paymentRepository.save(payment);
    }

    @Override
    public PaymentDto.Response getPaymentStatus(Long paymentId) {
        return PaymentDto.toResponse(paymentRepository.findById(paymentId)
                .orElseThrow(() -> new IllegalArgumentException("결제 정보를 찾을 수 없습니다.")));
    }



    @Override
    @Transactional
    public PaymentDto.Response handleWebhook(PaymentProviderType provider, PaymentDto.WebhookRequest webhookRequest) {
        Payment payment = paymentRepository.findByExternalPaymentId(webhookRequest.getExternalPaymentId())
                .orElseThrow(() -> new IllegalArgumentException("해당 결제 정보를 찾을 수 없습니다."));

        if (webhookRequest.getPaymentStatus() == PaymentStatus.SUCCESS) {
            payment.markSuccess();
            payment.getReservation().changePaymentStatus(webhookRequest.getPaymentStatus());
        } else {
            //TODO 결제 실패 시 flow 정의
            payment.markFailed();
            payment.getReservation().changePaymentStatus(webhookRequest.getPaymentStatus());
            payment.getReservation().cancel();
        }

        return PaymentDto.toResponse(payment);
    }
}
