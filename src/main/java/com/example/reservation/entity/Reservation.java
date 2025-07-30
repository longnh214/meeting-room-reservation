package com.example.entity;

import com.example.type.PaymentStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Reservation {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "meeting_room_id", nullable = false, columnDefinition = "예약한 회의실")
    @NotNull
    private MeetingRoom meetingRoom;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, columnDefinition = "예약자")
    @NotNull
    private User user;

    @Column(nullable = false, columnDefinition = "회의실 입실 시간")
    @NotNull
    private LocalDateTime startTime;

    @Column(nullable = false, columnDefinition = "회의실 퇴실 시간")
    @NotNull
    private LocalDateTime endTime;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, columnDefinition = "결제 상태")
    @NotNull
    private PaymentStatus paymentStatus = PaymentStatus.PENDING;

    @Column(nullable = false, columnDefinition = "총 결제 금액")
    @NotNull
    private int totalFee;

    // 예약 취소 여부
    private boolean cancelled = false;
}