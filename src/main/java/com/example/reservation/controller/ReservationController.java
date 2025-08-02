package com.example.reservation.controller;

import com.example.reservation.dto.ReservationDto;
import com.example.reservation.service.ReservationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Tag(name = "회의실 예약 API", description = "회의실 예약과 관련된 API")
@RequestMapping("/reservation")
@RequiredArgsConstructor
public class ReservationController {
    private final ReservationService reservationService;

    @GetMapping
    @Operation(summary = "회의실 예약 내역 전체 조회")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "요청 성공")
    })
    public ResponseEntity<List<ReservationDto.Response>> getAll() {
        return ResponseEntity.ok(reservationService.findAll());
    }

    @GetMapping("/{id}")
    @Operation(summary = "회의실 예약 내역 ID 별 조회")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "요청 성공")
    })
    public ResponseEntity<ReservationDto.Response> getById(@PathVariable Long id) {
        return ResponseEntity.ok(reservationService.findById(id));
    }

    @PostMapping
    @Operation(summary = "회의실 예약 요청")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "요청 성공"),
            @ApiResponse(responseCode = "400", description = "시간 관련 유효성 및 중복 예약 이슈"),
    })
    public ResponseEntity<ReservationDto.Response> create(@Valid @RequestBody ReservationDto.CreateRequest request) {
        ReservationDto.Response response = reservationService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping
    @Operation(summary = "회의실 예약 변경")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "요청 성공"),
            @ApiResponse(responseCode = "400", description = "시간 관련 유효성 및 중복 예약 이슈"),
    })
    public ResponseEntity<ReservationDto.Response> update(@Valid @RequestBody ReservationDto.UpdateRequest request) {
        return ResponseEntity.ok(reservationService.update(request));
    }

    @PostMapping("/{id}/cancel")
    @Operation(summary = "회의실 예약 취소")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "요청 성공"),
            @ApiResponse(responseCode = "400", description = "이미 지난 예약에 대한 요청 유효성 이슈"),
    })
    public ResponseEntity<ReservationDto.Response> cancel(@PathVariable Long id) {
        return ResponseEntity.ok(reservationService.cancel(
                    ReservationDto.CancelRequest
                        .builder()
                        .reservationId(id)
                        .build()));
    }
}
