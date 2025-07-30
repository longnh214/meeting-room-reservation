package com.example.reservation.type;

import lombok.Getter;

import java.util.Arrays;

@Getter
public enum PaymentProviderType {
    A("A", "A Company");

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
                .orElseThrow(() -> new IllegalArgumentException("Unknown PaymentProviderType code: " + code));
    }
}