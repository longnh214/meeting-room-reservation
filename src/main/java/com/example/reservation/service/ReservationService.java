package com.example.reservation.service;

import com.example.reservation.dto.ReservationDto;

import java.util.List;

public interface ReservationService {
    ReservationDto.Response create(ReservationDto.CreateRequest request);
    ReservationDto.Response cancel(ReservationDto.CancelRequest request);
    List<ReservationDto.Response> findAll();
    ReservationDto.Response update(ReservationDto.UpdateRequest request);
    ReservationDto.Response findById(Long id);
}