package com.example.reservation.type;

import lombok.Getter;

import java.util.Arrays;

@Getter
public enum PaymentProviderType {
    A("A", "A 회사"),
    B("B", "B 회사"),
    C("C", "C 회사");

    private final String code;
    private final String description;

    PaymentProviderType(String code, String description) {
        this.code = code;
        this.description = description;
    }

    public static PaymentProviderType fromCode(String code) {
        return Arrays.stream(PaymentProviderType.values())
                .filter(s -> s.code.equals(code))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("알 수 없는 결제 제공사 코드입니다. : " + code));
    }
}