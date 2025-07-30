package com.example.service;

import com.example.entity.Reservation;

import java.time.LocalDateTime;
import java.util.List;

public interface ReservationService {
    Reservation createReservation(Long userId, Long meetingRoomId, LocalDateTime startTime, LocalDateTime endTime);
    Reservation getReservation(Long reservationId);
    List<Reservation> getUserReservations(Long userId);
    void cancelReservation(Long reservationId);
}