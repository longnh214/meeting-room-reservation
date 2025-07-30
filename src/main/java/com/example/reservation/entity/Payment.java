package com.example.entity;

import com.example.type.PaymentProviderType;
import com.example.type.PaymentStatus;
import jakarta.persistence.*;

import java.math.BigInteger;
import java.time.LocalDateTime;

@Entity
public class Payment {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reservation_id", nullable = false, columnDefinition = "결제에 대한 예약")
    private Reservation reservation;

    @Column(nullable = false, columnDefinition = "결제 금액")
    private BigInteger fee;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, columnDefinition = "결제사 타입")
    private PaymentProviderType paymentProviderType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, columnDefinition = "결제 상태")
    private PaymentStatus paymentStatus;

    @Column(name = "external_payment_id", columnDefinition = "외부 결제 id")
    private String externalPaymentId;

    @Column(name = "requested_at")
    private LocalDateTime requestedAt;

    @Column(name = "completed_at")
    private LocalDateTime completedAt;
}
