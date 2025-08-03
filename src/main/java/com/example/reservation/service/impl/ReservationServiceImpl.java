package com.example.reservation.service.impl;

import com.example.reservation.dto.ReservationDto;
import com.example.reservation.entity.MeetingRoom;
import com.example.reservation.entity.Reservation;
import com.example.reservation.entity.User;
import com.example.reservation.repository.MeetingRoomRepository;
import com.example.reservation.repository.ReservationRepository;
import com.example.reservation.repository.UserRepository;
import com.example.reservation.service.ReservationService;
import com.example.reservation.type.PaymentStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReservationServiceImpl implements ReservationService {
    private final ReservationRepository reservationRepository;
    private final MeetingRoomRepository meetingRoomRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public ReservationDto.Response create(ReservationDto.CreateRequest request) {
        MeetingRoom room = meetingRoomRepository.findById(request.getMeetingRoomId())
                .orElseThrow(() -> new IllegalArgumentException("회의실이 존재하지 않습니다."));
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("사용자가 존재하지 않습니다."));

        List<Reservation> overlapping = reservationRepository.findOverlappingReservationsForUpdate(
                room.getId(),
                request.getStartTime(),
                request.getEndTime()
        );
        if (!overlapping.isEmpty()) {
            throw new IllegalStateException("해당 시간대에 이미 예약이 존재합니다.");
        }

        Reservation reservation = Reservation.builder()
                .meetingRoom(room)
                .user(user)
                .startTime(request.getStartTime().withSecond(0))
                .endTime(request.getEndTime().minusMinutes(1L).withSecond(59))
                .paymentStatus(PaymentStatus.PENDING)
                .totalFee(calculateTotalFee(room, request))
                .cancelled(false)
                .build();

        return ReservationDto.toResponse(reservationRepository.save(reservation));
    }

    @Override
    @Transactional
    public ReservationDto.Response update(ReservationDto.UpdateRequest request) {
        Reservation reservation = reservationRepository.findById(request.getReservationId())
                .orElseThrow(() -> new IllegalArgumentException("해당 예약이 존재하지 않습니다."));

        List<Reservation> overlapping = reservationRepository.findOverlappingReservationsForUpdate(
                request.getMeetingRoomId(),
                request.getNewStartTime(),
                request.getNewEndTime()
        );
        overlapping.removeIf(r -> r.getId().equals(reservation.getId()));
        if (!overlapping.isEmpty()) {
            throw new IllegalStateException("해당 시간대에 이미 예약이 존재합니다.");
        }

        reservation.changeTime(request.getNewStartTime(), request.getNewEndTime());

        return ReservationDto.toResponse(reservation);
    }

    @Override
    @Transactional
    public ReservationDto.Response cancel(ReservationDto.CancelRequest request) {
        Reservation reservation = reservationRepository.findById(request.getReservationId())
                .orElseThrow(() -> new IllegalArgumentException("해당 예약이 존재하지 않습니다."));

        if(reservation.getStartTime().isBefore(LocalDateTime.now())){
            throw new IllegalStateException("이미 지난 예약에 대해서는 취소할 수 없습니다.");
        }

        reservation.cancel();
        return ReservationDto.toResponse(reservation);
    }

    @Override
    public List<ReservationDto.Response> findAll() {
        return reservationRepository.findAllWithRoomAndUser()
                .stream()
                .map(ReservationDto::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public ReservationDto.Response findById(Long id) {
        return ReservationDto.toResponse(reservationRepository.findById(id)
                                            .orElseThrow(() -> new IllegalArgumentException("예약 없음")));
    }

    private BigDecimal calculateTotalFee(MeetingRoom room, ReservationDto.CreateRequest request) {
        long minutes = Duration.between(request.getStartTime(), request.getEndTime()).toMinutes();
        return room.getHourlyFee().multiply(BigDecimal.valueOf(minutes).divide(BigDecimal.valueOf(60)));
    }
}
