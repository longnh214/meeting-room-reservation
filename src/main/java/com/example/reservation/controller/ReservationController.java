package com.example.reservation.controller;

import com.example.reservation.dto.ReservationDto;
import com.example.reservation.service.ReservationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/reservation")
@RequiredArgsConstructor
public class ReservationController {
    private final ReservationService reservationService;

    @GetMapping
    public ResponseEntity<List<ReservationDto.Response>> getAll() {
        return ResponseEntity.ok(reservationService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ReservationDto.Response> getById(@PathVariable Long id) {
        return ResponseEntity.ok(reservationService.findById(id));
    }

    @PostMapping
    public ResponseEntity<ReservationDto.Response> create(@Valid @RequestBody ReservationDto.CreateRequest request) {
        ReservationDto.Response response = reservationService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping
    public ResponseEntity<ReservationDto.Response> update(@Valid @RequestBody ReservationDto.UpdateRequest request) {
        return ResponseEntity.ok(reservationService.update(request));
    }

    @PostMapping("/{id}/cancel")
    public ResponseEntity<ReservationDto.Response> cancel(@PathVariable Long id) {
        return ResponseEntity.ok(reservationService.cancel(
                    ReservationDto.CancelRequest
                        .builder()
                        .reservationId(id)
                        .build()));
    }
}
