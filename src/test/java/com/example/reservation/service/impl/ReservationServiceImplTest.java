package com.example.reservation.service.impl;

import com.example.reservation.AbstractIntegrationTest;
import com.example.reservation.dto.ReservationDto;
import com.example.reservation.entity.MeetingRoom;
import com.example.reservation.entity.Reservation;
import com.example.reservation.entity.User;
import com.example.reservation.repository.MeetingRoomRepository;
import com.example.reservation.repository.ReservationRepository;
import com.example.reservation.repository.UserRepository;
import com.example.reservation.service.ReservationService;
import com.example.reservation.type.PaymentStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicReference;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

class ReservationServiceImplTest extends AbstractIntegrationTest {

    @Autowired
    private ReservationService reservationService;

    @Autowired
    private MeetingRoomRepository meetingRoomRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ReservationRepository reservationRepository;

    private MeetingRoom testRoom;
    private User testUser;
    private User testUser2;
    private LocalDateTime startTime;
    private LocalDateTime endTime;

    @BeforeEach
    void setup() {
        reservationRepository.deleteAll();
        meetingRoomRepository.deleteAll();
        userRepository.deleteAll();

        startTime = LocalDateTime.now().plusHours(1).withMinute(0);
        endTime = LocalDateTime.now().plusHours(2).withMinute(0);

        testRoom = MeetingRoom.builder()
                .name("회의실 A")
                .hourlyFee(BigDecimal.valueOf(100000))
                .capacity(16)
                .build();

        testUser = User.builder()
                .name("사용자1")
                .email("user1@gmail.com")
                .build();
        testUser2 = User
                .builder()
                .name("사용자2")
                .email("user2@gmail.com")
                .build();

        meetingRoomRepository.save(testRoom);
        userRepository.save(testUser);
        userRepository.save(testUser2);
    }

    @Test
    @Transactional
    void 정상적인_예약_요청() {
        ReservationDto.CreateRequest request = ReservationDto.CreateRequest.builder()
                .meetingRoomId(testRoom.getId())
                .userId(testUser.getId())
                .startTime(startTime)
                .endTime(endTime)
                .build();

        ReservationDto.Response response = reservationService.create(request);

        assertThat(response).isNotNull();
        assertThat(response.getMeetingRoomName()).isEqualTo("회의실 A");
        assertThat(response.getUserName()).isEqualTo("사용자1");
        assertThat(response.getTotalFee()).isEqualTo(BigDecimal.valueOf(100000));
        assertThat(response.getPaymentStatus()).isEqualTo(PaymentStatus.PENDING);

        List<Reservation> savedReservations = reservationRepository.findAll();
        assertThat(savedReservations).hasSize(1);
        assertThat(savedReservations.get(0).getMeetingRoom().getName()).isEqualTo("회의실 A");
        assertThat(savedReservations.get(0).getUser().getName()).isEqualTo("사용자1");
    }

    @Test
    @Transactional
    void 예약_생성_실패_존재하지_않는_회의실() {
        ReservationDto.CreateRequest request = ReservationDto.CreateRequest.builder()
                .meetingRoomId(999L)
                .userId(testUser.getId())
                .startTime(startTime)
                .endTime(endTime)
                .build();

        assertThatThrownBy(() -> reservationService.create(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("회의실이 존재하지 않습니다.");

        List<Reservation> reservations = reservationRepository.findAll();
        assertThat(reservations).isEmpty();
    }

    @Test
    @Transactional
    void 예약_생성_실패_존재하지_않는_사용자() {
        ReservationDto.CreateRequest request = ReservationDto.CreateRequest.builder()
                .meetingRoomId(testRoom.getId())
                .userId(999L)
                .startTime(startTime)
                .endTime(endTime)
                .build();

        assertThatThrownBy(() -> reservationService.create(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("사용자가 존재하지 않습니다.");

        List<Reservation> reservations = reservationRepository.findAll();
        assertThat(reservations).isEmpty();
    }

    @Test
    @DisplayName("예약 생성 실패 - 시간 겹침 (실제 DB)")
    @Transactional
    void 예약_생성_실패_겹치는_시간() {
        Reservation existingReservation = Reservation.builder()
                .meetingRoom(testRoom)
                .user(testUser)
                .startTime(startTime.withSecond(0))
                .endTime(endTime.minusMinutes(1).withSecond(59))
                .paymentStatus(PaymentStatus.PENDING)
                .totalFee(BigDecimal.valueOf(50000))
                .cancelled(false)
                .build();
        reservationRepository.save(existingReservation);

        ReservationDto.CreateRequest request = ReservationDto.CreateRequest.builder()
                .meetingRoomId(testRoom.getId())
                .userId(testUser.getId())
                .startTime(startTime.plusMinutes(30))
                .endTime(endTime.plusMinutes(30))
                .build();

        assertThatThrownBy(() -> reservationService.create(request))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("해당 시간대에 이미 예약이 존재합니다.");

        List<Reservation> reservations = reservationRepository.findAll();
        assertThat(reservations).hasSize(1);
        assertThat(reservations.get(0).getId()).isEqualTo(existingReservation.getId());
    }

    @Test
    @Transactional
    void 예약_변경_성공() {
        Reservation existingReservation = Reservation.builder()
                .meetingRoom(testRoom)
                .user(testUser)
                .startTime(startTime.withSecond(0))
                .endTime(endTime.minusMinutes(1).withSecond(59))
                .paymentStatus(PaymentStatus.PENDING)
                .totalFee(BigDecimal.valueOf(100000))
                .cancelled(false)
                .build();
        existingReservation = reservationRepository.save(existingReservation);

        LocalDateTime newStartTime = LocalDateTime.of(2024, 8, 1, 18, 0);
        LocalDateTime newEndTime = LocalDateTime.of(2024, 8, 1, 20, 0);

        ReservationDto.UpdateRequest request = ReservationDto.UpdateRequest.builder()
                .reservationId(existingReservation.getId())
                .meetingRoomId(testRoom.getId())
                .newStartTime(newStartTime)
                .newEndTime(newEndTime)
                .build();

        ReservationDto.Response response = reservationService.update(request);

        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(existingReservation.getId());

        Reservation updatedReservation = reservationRepository.findById(existingReservation.getId())
                .orElseThrow();
        assertThat(updatedReservation.getStartTime()).isEqualTo(newStartTime.withSecond(0));
        assertThat(updatedReservation.getEndTime()).isEqualTo(newEndTime.minusMinutes(1).withSecond(59));
    }

    @Test
    @Transactional
    void 예약_취소_성공() {
        LocalDateTime futureStartTime = LocalDateTime.now().plusDays(1);
        Reservation futureReservation = Reservation.builder()
                .meetingRoom(testRoom)
                .user(testUser)
                .startTime(futureStartTime.withSecond(0))
                .endTime(futureStartTime.plusHours(2).minusMinutes(1).withSecond(59))
                .paymentStatus(PaymentStatus.PENDING)
                .totalFee(BigDecimal.valueOf(100000))
                .cancelled(false)
                .build();
        futureReservation = reservationRepository.save(futureReservation);

        ReservationDto.CancelRequest request = ReservationDto.CancelRequest.builder()
                .reservationId(futureReservation.getId())
                .build();

        ReservationDto.Response response = reservationService.cancel(request);

        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(futureReservation.getId());

        Reservation cancelledReservation = reservationRepository.findById(futureReservation.getId())
                .orElseThrow();
        assertThat(cancelledReservation.isCancelled()).isTrue();
    }

    @Test
    @Transactional
    void 예약_취소_실패_지난_예약() {
        LocalDateTime pastStartTime = LocalDateTime.now().minusDays(1);
        Reservation pastReservation = Reservation.builder()
                .meetingRoom(testRoom)
                .user(testUser)
                .startTime(pastStartTime.withSecond(0))
                .endTime(pastStartTime.plusHours(2).minusMinutes(1).withSecond(59))
                .paymentStatus(PaymentStatus.PENDING)
                .totalFee(BigDecimal.valueOf(100000))
                .cancelled(false)
                .build();
        pastReservation = reservationRepository.save(pastReservation);

        ReservationDto.CancelRequest request = ReservationDto.CancelRequest.builder()
                .reservationId(pastReservation.getId())
                .build();

        assertThatThrownBy(() -> reservationService.cancel(request))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("이미 지난 예약에 대해서는 취소할 수 없습니다.");

        Reservation unchangedReservation = reservationRepository.findById(pastReservation.getId())
                .orElseThrow();
        assertThat(unchangedReservation.isCancelled()).isFalse();
    }

    @Test
    @Transactional
    void 전체_예약_조회() {
        Reservation reservation1 = Reservation.builder()
                .meetingRoom(testRoom)
                .user(testUser)
                .startTime(startTime.withSecond(0))
                .endTime(endTime.minusMinutes(1).withSecond(59))
                .paymentStatus(PaymentStatus.PENDING)
                .totalFee(BigDecimal.valueOf(100000))
                .cancelled(false)
                .build();

        Reservation reservation2 = Reservation.builder()
                .meetingRoom(testRoom)
                .user(testUser)
                .startTime(startTime.plusHours(3).withSecond(0))
                .endTime(endTime.plusHours(3).minusMinutes(1).withSecond(59))
                .paymentStatus(PaymentStatus.SUCCESS)
                .totalFee(BigDecimal.valueOf(100000))
                .cancelled(false)
                .build();

        reservationRepository.saveAll(List.of(reservation1, reservation2));

        List<ReservationDto.Response> responses = reservationService.findAll();

        assertThat(responses).hasSize(2);
        assertThat(responses)
                .extracting(ReservationDto.Response::getMeetingRoomName)
                .containsOnly("회의실 A");
        assertThat(responses)
                .extracting(ReservationDto.Response::getUserName)
                .containsOnly("사용자1");
    }

    @Test
    @Transactional
    void ID로_예약_조회_성공() {
        Reservation savedReservation = Reservation.builder()
                .meetingRoom(testRoom)
                .user(testUser)
                .startTime(startTime.withSecond(0))
                .endTime(endTime.minusMinutes(1).withSecond(59))
                .paymentStatus(PaymentStatus.PENDING)
                .totalFee(BigDecimal.valueOf(100000))
                .cancelled(false)
                .build();
        savedReservation = reservationRepository.save(savedReservation);

        ReservationDto.Response response = reservationService.findById(savedReservation.getId());

        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(savedReservation.getId());
        assertThat(response.getMeetingRoomName()).isEqualTo("회의실 A");
        assertThat(response.getUserName()).isEqualTo("사용자1");
        assertThat(response.getTotalFee()).isEqualTo(BigDecimal.valueOf(100000));
    }

    @Test
    @Transactional
    void ID로_예약_조회_실패_존재하지_않는_예약() {
        assertThatThrownBy(() -> reservationService.findById(999L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("예약 없음");
    }

    @Test
    @Transactional
    void 같은_시간대_동시_예약_시도() {
        ReservationDto.CreateRequest request1 = ReservationDto.CreateRequest.builder()
                .meetingRoomId(testRoom.getId())
                .userId(testUser.getId())
                .startTime(startTime)
                .endTime(endTime)
                .build();

        ReservationDto.CreateRequest request2 = ReservationDto.CreateRequest.builder()
                .meetingRoomId(testRoom.getId())
                .userId(testUser.getId())
                .startTime(startTime.plusMinutes(30))
                .endTime(endTime.plusMinutes(30))
                .build();

        ReservationDto.Response response1 = reservationService.create(request1);
        assertThat(response1).isNotNull();

        assertThatThrownBy(() -> reservationService.create(request2))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("해당 시간대에 이미 예약이 존재합니다.");

        List<Reservation> reservations = reservationRepository.findAll();
        assertThat(reservations).hasSize(1);
    }

    @Test
    @Transactional
    void 요금_계산_검증() {
        ReservationDto.CreateRequest request30min = ReservationDto.CreateRequest.builder()
                .meetingRoomId(testRoom.getId())
                .userId(testUser.getId())
                .startTime(startTime)
                .endTime(startTime.plusMinutes(30))
                .build();

        ReservationDto.Response response30min = reservationService.create(request30min);

        assertThat(response30min.getTotalFee()).isEqualTo(BigDecimal.valueOf(50000).setScale(1));

        LocalDateTime newStartTime = startTime.plusHours(4);
        ReservationDto.CreateRequest request3hours = ReservationDto.CreateRequest.builder()
                .meetingRoomId(testRoom.getId())
                .userId(testUser.getId())
                .startTime(newStartTime)
                .endTime(newStartTime.plusHours(3))
                .build();

        ReservationDto.Response response3hours = reservationService.create(request3hours);

        assertThat(response3hours.getTotalFee()).isEqualTo(BigDecimal.valueOf(300000));

        List<Reservation> reservations = reservationRepository.findAll();
        assertThat(reservations).hasSize(2);
    }

    @Test
    void 동시_예약시_한쪽만_성공해야_한다() throws InterruptedException {
        MeetingRoom room = meetingRoomRepository.findAll().get(0);
        User user1 = userRepository.findAll().get(0);
        User user2 = userRepository.findAll().get(1);

        LocalDateTime start = LocalDateTime.now().plusHours(3).withMinute(0).withSecond(0);
        LocalDateTime end = start.plusHours(1);

        ReservationDto.CreateRequest req1 = ReservationDto.CreateRequest.builder()
                .meetingRoomId(room.getId())
                .userId(user1.getId())
                .startTime(start)
                .endTime(end)
                .build();

        ReservationDto.CreateRequest req2 = ReservationDto.CreateRequest.builder()
                .meetingRoomId(room.getId())
                .userId(user2.getId())
                .startTime(start)
                .endTime(end)
                .build();

        CountDownLatch latch = new CountDownLatch(2);

        AtomicReference<Exception> ex1 = new AtomicReference<>();
        AtomicReference<Exception> ex2 = new AtomicReference<>();

        Runnable task1 = () -> {
            try {
                reservationService.create(req1);
            } catch (Exception e) {
                ex1.set(e);
            } finally {
                latch.countDown();
            }
        };

        Runnable task2 = () -> {
            try {
                reservationService.create(req2);
            } catch (Exception e) {
                ex2.set(e);
            } finally {
                latch.countDown();
            }
        };

        Thread t1 = new Thread(task1);
        Thread t2 = new Thread(task2);

        t1.start();
        t2.start();

        latch.await();

        List<ReservationDto.Response> all = reservationService.findAll();
        assertThat(all).hasSize(1);

        assertThat(ex1.get() != null || ex2.get() != null).isTrue();
        System.out.println("스레드1 예외: " + ex1.get());
        System.out.println("스레드2 예외: " + ex2.get());
    }
}