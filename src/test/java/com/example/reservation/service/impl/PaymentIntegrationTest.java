package com.example.reservation.service.impl;

import com.example.reservation.AbstractIntegrationTest;
import com.example.reservation.dto.PaymentDto;
import com.example.reservation.entity.MeetingRoom;
import com.example.reservation.entity.Payment;
import com.example.reservation.entity.PaymentProvider;
import com.example.reservation.entity.Reservation;
import com.example.reservation.entity.User;
import com.example.reservation.repository.*;
import com.example.reservation.service.PaymentProviderExternalService;
import com.example.reservation.service.PaymentService;
import com.example.reservation.type.PaymentProviderType;
import com.example.reservation.type.PaymentStatus;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.testcontainers.shaded.org.awaitility.Awaitility;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;

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
    void 결제_성공_프로세스_MOCK_테스트() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);

        PaymentDto.Response pendingResponse =
                paymentService.requestPaymentExternalService(reservation.getId(), paymentProviderType);

        Assertions.assertEquals(PaymentStatus.PENDING,
                paymentRepository.findById(pendingResponse.getPaymentId())
                        .orElseThrow().getPaymentStatus());

        latch.await(2, TimeUnit.SECONDS);

        Payment updated = paymentRepository.findById(pendingResponse.getPaymentId()).orElseThrow();

        PaymentDto.WebhookRequest webhookRequest = PaymentDto.WebhookRequest.builder()
                .externalPaymentId(updated.getExternalPaymentId())
                .paymentStatus(PaymentStatus.SUCCESS)
                .build();

        paymentService.handleWebhook(paymentProviderType, webhookRequest);

        //TODO paymentProviderExternalService.requestPaymentExternalApi mock으로 관리
        updated = paymentRepository.findById(pendingResponse.getPaymentId()).orElseThrow();
        Assertions.assertEquals(PaymentStatus.SUCCESS, updated.getPaymentStatus());
        Assertions.assertNotNull(updated.getExternalPaymentId());
    }
}
