package com.example.reservation.service.impl;

import com.example.reservation.AbstractIntegrationTest;
import com.example.reservation.dto.PaymentDto;
import com.example.reservation.entity.*;
import com.example.reservation.repository.*;
import com.example.reservation.service.PaymentService;
import com.example.reservation.type.PaymentProviderType;
import com.example.reservation.type.PaymentStatus;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

class PaymentIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    private PaymentService paymentService;

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private PaymentProviderRepository paymentProviderRepository;

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private MeetingRoomRepository meetingRoomRepository;

    @Autowired
    private UserRepository userRepository;

    private MeetingRoom testRoom;
    private User testUser;
    private User testUser2;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Reservation reservation;
    private PaymentProvider paymentProvider;
    private PaymentProviderType paymentProviderType;

    @BeforeEach
    void setup() {
        paymentRepository.deleteAll();
        paymentProviderRepository.deleteAll();
        reservationRepository.deleteAll();
        meetingRoomRepository.deleteAll();
        userRepository.deleteAll();

        startTime = LocalDateTime.now().plusHours(1).withMinute(0);
        endTime = LocalDateTime.now().plusHours(2).withMinute(0);
        paymentProviderType = PaymentProviderType.A;

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

        paymentProvider = PaymentProvider.builder()
                .apiEndpoint("/api/v1/A")
                .paymentProviderType(paymentProviderType)
                .apiKey(UUID.randomUUID().toString())
                .build();

        meetingRoomRepository.save(testRoom);
        userRepository.save(testUser);
        userRepository.save(testUser2);
        paymentProviderRepository.save(paymentProvider);

        reservation = Reservation.builder()
                .meetingRoom(testRoom)
                .user(testUser)
                .startTime(startTime)
                .endTime(endTime)
                .paymentStatus(PaymentStatus.PENDING)
                .build();

        reservation.changeTime(startTime, endTime);

        reservation = reservationRepository.save(reservation);
    }

    @Test
    void 결제_성공_프로세스_통합_테스트() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);

        PaymentStatus 성공 = PaymentStatus.SUCCESS;

        PaymentDto.Response pendingResponse =
                paymentService.requestPaymentExternalService(reservation.getId(), paymentProviderType);

        Assertions.assertEquals(PaymentStatus.PENDING,
                paymentRepository.findById(pendingResponse.getPaymentId())
                        .orElseThrow().getPaymentStatus());

        latch.await(2, TimeUnit.SECONDS);

        Payment updated = paymentRepository.findById(pendingResponse.getPaymentId()).orElseThrow();

        PaymentDto.WebhookRequest webhookRequest = PaymentDto.WebhookRequest.builder()
                .externalPaymentId(updated.getExternalPaymentId())
                .paymentStatus(성공)
                .build();

        paymentService.handleWebhook(paymentProviderType, webhookRequest);

        //TODO paymentProviderExternalService.requestPaymentExternalApi mock으로 관리
        updated = paymentRepository.findById(pendingResponse.getPaymentId()).orElseThrow();
        Assertions.assertEquals(성공, updated.getPaymentStatus());
        Assertions.assertNotNull(updated.getExternalPaymentId());
    }

    @Test
    void 결제_실패_프로세스_통합_테스트() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);

        PaymentStatus 실패 = PaymentStatus.FAILED;

        PaymentDto.Response pendingResponse =
                paymentService.requestPaymentExternalService(reservation.getId(), paymentProviderType);

        Assertions.assertEquals(PaymentStatus.PENDING,
                paymentRepository.findById(pendingResponse.getPaymentId())
                        .orElseThrow().getPaymentStatus());

        latch.await(2, TimeUnit.SECONDS);

        Payment updated = paymentRepository.findById(pendingResponse.getPaymentId()).orElseThrow();

        PaymentDto.WebhookRequest webhookRequest = PaymentDto.WebhookRequest.builder()
                .externalPaymentId(updated.getExternalPaymentId())
                .paymentStatus(실패)
                .build();

        paymentService.handleWebhook(paymentProviderType, webhookRequest);

        //TODO paymentProviderExternalService.requestPaymentExternalApi mock으로 관리
        updated = paymentRepository.findById(pendingResponse.getPaymentId()).orElseThrow();
        Assertions.assertEquals(실패, updated.getPaymentStatus());
        Assertions.assertNotNull(updated.getExternalPaymentId());
    }
}
