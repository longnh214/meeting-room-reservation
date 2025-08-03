package com.example.reservation.dto;

import com.example.reservation.entity.Payment;
import com.example.reservation.type.PaymentMeasurementType;
import com.example.reservation.type.PaymentProviderType;
import com.example.reservation.type.PaymentStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class PaymentDto {
    
    @Getter
    @Builder
    public static class Request {
        @Schema(name = "예약 id")
        private Long reservationId;

        @Schema(name = "결제사 코드", example = "A")
        private PaymentProviderType paymentProviderType;
    }

    @Getter
    @Builder
    public static class RequestExternal {
        @Schema(name = "결제 총 요금")
        private BigDecimal fee;

        @Schema(name = "결제사 코드", example = "A")
        private PaymentProviderType paymentProviderType;

        @Schema(name = "결제사 API endpoint")
        private String apiEndpoint;

        @Schema(name = "결제사 API KEY")
        private String apiKey;
    }
    
    @Getter
    @Builder
    public static class Response {
        @Schema(name = "결제 DB ID")
        private Long paymentId;

        @Schema(name = "결제사 내부 관리 ID")
        private String externalPaymentId;

        @Schema(name = "결제 상태")
        private PaymentStatus paymentStatus;

        @Schema(name = "결제 금액")
        private BigDecimal fee;

        @Schema(name = "결제 요청 시각")
        private LocalDateTime requestedAt;

        @Schema(name = "결제사 API endpoint")
        private String apiEndpoint;

        @Schema(name = "결제사 코드", example = "A")
        private PaymentProviderType paymentProviderType;

        @Schema(name = "결제 수단 코드", example = "SIMPLE")
        private PaymentMeasurementType measurementType;
    }

    @Getter
    @Builder
    public static class CancelResponse {
        @Schema(name = "결제 DB ID")
        private Long paymentId;

        @Schema(name = "결제사 내부 관리 ID")
        private String externalPaymentId;

        @Schema(name = "결제 상태")
        private PaymentStatus paymentStatus;
    }

    @Getter
    @Builder
    public static class WebhookRequest {
        @Schema(name = "결제사 내부 관리 ID")
        private String externalPaymentId;

        @Schema(name = "결제 상태")
        private PaymentStatus paymentStatus;
    }

    public static Response toResponse(Payment payment) {
        return Response.builder()
                .paymentId(payment.getId())
                .externalPaymentId(payment.getExternalPaymentId())
                .paymentStatus(payment.getPaymentStatus())
                .fee(payment.getFee())
                .requestedAt(payment.getRequestedAt())
                .apiEndpoint(payment.getPaymentProvider() != null
                        ? payment.getPaymentProvider().getApiEndpoint()
                        : null)
                .paymentProviderType(payment.getPaymentProvider() != null
                        ? payment.getPaymentProvider().getPaymentProviderType()
                        : null)
                .measurementType(payment.getMeasurementType())
                .build();
    }
}
