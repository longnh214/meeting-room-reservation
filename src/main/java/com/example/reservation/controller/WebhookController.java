package com.example.reservation.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Tag(name = "결제 웹훅 API", description = "결제사 별 웹훅 수신 API")
@RequestMapping("/webhooks")
@RequiredArgsConstructor
public class PaymentController {
}
