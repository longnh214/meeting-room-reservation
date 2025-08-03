package com.example.reservation.controller;

import com.example.reservation.dto.PaymentDto;
import com.example.reservation.service.PaymentService;
import com.example.reservation.type.PaymentProviderType;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@Tag(name = "결제 웹훅 API", description = "결제사 별 웹훅 수신 API")
@RequestMapping("/webhooks")
@RequiredArgsConstructor
public class WebhookController {
    private final PaymentService paymentService;

    @PostMapping("/payments/{provider}")
    @Operation(summary = "결제사 별 웹훅 수신")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "요청 성공")
    })
    public ResponseEntity<PaymentDto.Response> handleWebhook(@PathVariable PaymentProviderType provider, @RequestBody PaymentDto.WebhookRequest webhookRequest) {
        return ResponseEntity.ok(paymentService.handleWebhook(provider, webhookRequest));
    }
}
