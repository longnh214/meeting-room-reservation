package com.example.reservation.dto;

import com.example.reservation.annotation.HalfHour;
import com.example.reservation.annotation.ValidReservationTime;
import com.example.reservation.annotation.AfterCurrentTime;
import com.example.reservation.entity.Reservation;
import com.example.reservation.type.PaymentStatus;
import io.swagger.v3.oas.annotations.media.Schema;
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
        @Schema(name = "예약 id")
        private Long id;

        @Schema(name = "회의실 id")
        private Long meetingRoomId;

        @Schema(name = "회의실 이름")
        private String meetingRoomName;

        @Schema(name = "사용자 id")
        private Long userId;

        @Schema(name = "사용자 이름")
        private String userName;

        @Schema(name = "예약 시작 시간")
        private LocalDateTime startTime;

        @Schema(name = "예약 마감 시간")
        private LocalDateTime endTime;

        @Schema(name = "결제 상태")
        private PaymentStatus paymentStatus;

        @Schema(name = "총 결제 요금")
        private BigDecimal totalFee;

        @Schema(name = "예약 취소 여부")
        private boolean cancelled;
    }

    @Getter
    @Builder
    @ValidReservationTime
    public static class CreateRequest {
        @NotNull
        private Long meetingRoomId;

        @NotNull
        @Schema(name = "사용자 이름")
        private Long userId;

        @NotNull
        @Future
        @HalfHour
        @Schema(name = "예약 시작 시간")
        private LocalDateTime startTime;

        @NotNull
        @Future
        @HalfHour
        @Schema(name = "예약 마감 시간")
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
        @Schema(name = "예약 시작 시간")
        private LocalDateTime newStartTime;

        @NotNull
        @Future
        @HalfHour
        @Schema(name = "예약 마감 시간")
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