package com.example.reservation.type;

import lombok.Getter;

import java.util.Arrays;

@Getter
public enum PaymentMeasurementType {
    CARD("cardPayment", "카드결제"),
    SIMPLE("simplePayment", "단순결제"),
    VIRTUAL_ACCOUNT("virtualAccount", "가상계좌"),
    ;

    private final String code;
    private final String description;

    PaymentMeasurementType(String code, String description) {
        this.code = code;
        this.description = description;
    }

    public static PaymentMeasurementType fromCode(String code) {
        return Arrays.stream(PaymentMeasurementType.values())
                .filter(s -> s.code.equals(code))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("알 수 없는 결제 수단 코드입니다. : " + code));
    }
}
