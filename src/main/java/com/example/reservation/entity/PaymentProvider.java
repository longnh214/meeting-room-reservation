package com.example.reservation.entity;

import com.example.reservation.type.PaymentProviderType;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class PaymentProvider {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentProviderType paymentProviderType;

    @Column(nullable = false)
    private String apiEndpoint;

    @Column(nullable = false)
    private String apiKey;
}
