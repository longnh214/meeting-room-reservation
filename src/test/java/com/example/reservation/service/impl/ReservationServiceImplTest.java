package com.example.reservation.service.impl;

import com.example.reservation.dto.ReservationDto;
import com.example.reservation.entity.MeetingRoom;
import com.example.reservation.entity.User;
import com.example.reservation.repository.MeetingRoomRepository;
import com.example.reservation.repository.UserRepository;
import com.example.reservation.service.ReservationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicReference;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@SpringBootTest
@ActiveProfiles("test")
class ReservationServiceImplTest {

    @Autowired
    private ReservationService reservationService;

    @Autowired
    private MeetingRoomRepository meetingRoomRepository;

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void setup() {
        MeetingRoom room = MeetingRoom.builder()
                .name("회의실 A")
                .hourlyFee(BigDecimal.valueOf(10000))
                .build();
        meetingRoomRepository.save(room);

        User user1 = User.builder().name("사용자1").email("user1@gmail.com").build();
        User user2 = User.builder().name("사용자2").email("user2@gmail.com").build();
        userRepository.save(user1);
        userRepository.save(user2);
    }

    @Test
    void 동시_예약시_한쪽만_성공해야_한다() throws InterruptedException {
        MeetingRoom room = meetingRoomRepository.findAll().get(0);
        User user1 = userRepository.findAll().get(0);
        User user2 = userRepository.findAll().get(1);

        LocalDateTime start = LocalDateTime.now().plusHours(1).withMinute(0).withSecond(0);
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

        // 검증
        List<ReservationDto.Response> all = reservationService.findAll();
        assertThat(all).hasSize(1); // 한 건만 성공

        // 실패한 스레드에서 예외 발생
        assertThat(ex1.get() != null || ex2.get() != null).isTrue();
        System.out.println("스레드1 예외: " + ex1.get());
        System.out.println("스레드2 예외: " + ex2.get());
    }
}