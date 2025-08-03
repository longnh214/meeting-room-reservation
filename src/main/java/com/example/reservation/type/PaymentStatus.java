package com.example.reservation.type;

import lombok.Getter;

import java.util.Arrays;

@Getter
public enum PaymentStatus{

    PENDING("PENDING", "결제 대기"),
    SUCCESS("SUCCESS", "결제 완료"),
    FAILED("FAILED", "결제 실패"),
    CANCELLED("CANCELLED", "결제 취소"),
    REFUNDED("REFUNDED", "환불 완료");

    private final String code;
    private final String description;

    PaymentStatus(String code, String description) {
        this.code = code;
        this.description = description;
    }

    public static PaymentStatus fromCode(String code) {
        return Arrays.stream(PaymentStatus.values())
                .filter(s -> s.code.equals(code))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unknown PaymentStatus code: " + code));
    }
}
