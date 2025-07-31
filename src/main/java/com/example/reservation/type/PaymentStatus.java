package com.example.reservation.type;

import lombok.Getter;

import java.util.Arrays;

@Getter
public enum PaymentStatus{

    PENDING("P", "결제 대기"),
    SUCCESS("S", "결제 완료"),
    FAILED("F", "결제 실패"),
    CANCELLED("C", "결제 취소"),
    REFUNDED("R", "환불 완료");

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
