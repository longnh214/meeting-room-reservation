package com.example.reservation.type;

import lombok.Getter;

import java.util.Arrays;

@Getter
public enum PaymentProviderType {
    A("A", "A 회사", PaymentMeasurementType.CARD),
    B("B", "B 회사", PaymentMeasurementType.SIMPLE),
    C("C", "C 회사", PaymentMeasurementType.VIRTUAL_ACCOUNT);

    private final String code;
    private final String description;
    private final PaymentMeasurementType paymentMeasurementType;

    PaymentProviderType(String code, String description, PaymentMeasurementType paymentMeasurementType) {
        this.code = code;
        this.description = description;
        this.paymentMeasurementType = paymentMeasurementType;
    }

    public static PaymentProviderType fromCode(String code) {
        return Arrays.stream(PaymentProviderType.values())
                .filter(s -> s.code.equals(code))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("알 수 없는 결제사 코드입니다. : " + code));
    }
}