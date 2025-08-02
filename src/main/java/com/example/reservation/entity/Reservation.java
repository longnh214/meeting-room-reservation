package com.example.reservation.entity;

import com.example.reservation.type.PaymentStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Table(
        name = "reservation",
        indexes = {
                @Index(
                        name = "idx_room_time",
                        columnList = "meeting_room_id, start_time, end_time"
                )
        }
)
public class Reservation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "meeting_room_id", nullable = false)
    @NotNull
    private MeetingRoom meetingRoom;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @NotNull
    private User user;

    @Column(nullable = false)
    @NotNull
    private LocalDateTime startTime;

    @Column(nullable = false)
    @NotNull
    private LocalDateTime endTime;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @NotNull
    private PaymentStatus paymentStatus = PaymentStatus.PENDING;

    @Column(nullable = false, scale = 2, precision = 19)
    @NotNull
    private BigDecimal totalFee;

    private boolean cancelled = false;

    public void changeTime(LocalDateTime newStart, LocalDateTime newEnd) {
        this.startTime = newStart.withSecond(0);
        this.endTime = newEnd.minusMinutes(1L).withSecond(59);
        this.totalFee = calculateTotalFee(newStart, newEnd);
    }

    private BigDecimal calculateTotalFee(LocalDateTime startTime, LocalDateTime endTime) {
        long minutes = Duration.between(startTime, endTime).toMinutes();
        return meetingRoom.getHourlyFee().multiply(BigDecimal.valueOf(minutes).divide(BigDecimal.valueOf(60)));
    }

    public void changePaymentStatus(PaymentStatus status) {
        this.paymentStatus = status;
    }

    public void cancel() {
        this.cancelled = true;
    }
}