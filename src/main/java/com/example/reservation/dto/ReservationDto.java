package com.example.reservation.dto;

import com.example.reservation.annotation.HalfHour;
import com.example.reservation.annotation.ValidReservationTime;
import com.example.reservation.annotation.AfterCurrentTime;
import com.example.reservation.entity.Reservation;
import com.example.reservation.type.PaymentStatus;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;


public class ReservationDto {

    @Getter
    @Builder
    public static class Response {
        private Long id;
        private Long meetingRoomId;
        private String meetingRoomName;
        private Long userId;
        private String userName;
        private LocalDateTime startTime;
        private LocalDateTime endTime;
        private PaymentStatus paymentStatus;
        private BigDecimal totalFee;
        private boolean cancelled;
    }

    @Getter
    @Builder
    @ValidReservationTime
    public static class CreateRequest {
        @NotNull
        private Long meetingRoomId;

        @NotNull
        private Long userId;

        @NotNull
        @Future
        @HalfHour
        private LocalDateTime startTime;

        @NotNull
        @Future
        @HalfHour
        private LocalDateTime endTime;
    }

    @Getter
    @Builder
    @ValidReservationTime
    @AfterCurrentTime
    public static class UpdateRequest {

        @NotNull
        private Long reservationId;

        @NotNull
        private Long meetingRoomId;

        @NotNull
        @Future
        @HalfHour
        private LocalDateTime newStartTime;

        @NotNull
        @Future
        @HalfHour
        private LocalDateTime newEndTime;
    }

    @Getter
    @Builder
    public static class CancelRequest {
        @NotNull
        private Long reservationId;
    }

    public static Response toResponse(Reservation reservation) {
        return Response.builder()
                .id(reservation.getId())
                .meetingRoomId(reservation.getMeetingRoom().getId())
                .meetingRoomName(reservation.getMeetingRoom().getName())
                .userId(reservation.getUser().getId())
                .userName(reservation.getUser().getName())
                .startTime(reservation.getStartTime())
                .endTime(reservation.getEndTime())
                .paymentStatus(reservation.getPaymentStatus())
                .totalFee(reservation.getTotalFee())
                .cancelled(reservation.isCancelled())
                .build();
    }
}