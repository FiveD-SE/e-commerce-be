package com.pm.promotionservice.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@FeignClient(name = "payment-service", path = "/payment-service/api")
public interface PaymentServiceClient {

    @PostMapping("/payments/{paymentId}/apply-promotion")
    PaymentPromotionResponse applyPromotionToPayment(
            @PathVariable("paymentId") Long paymentId,
            @RequestBody PaymentPromotionRequest request
    );

    @PostMapping("/payments/{paymentId}/remove-promotion")
    PaymentPromotionResponse removePromotionFromPayment(
            @PathVariable("paymentId") Long paymentId
    );

    @GetMapping("/payments/{paymentId}/promotion-info")
    PaymentPromotionInfo getPaymentPromotionInfo(
            @PathVariable("paymentId") Long paymentId
    );

    // DTOs for Payment Service Integration
    record PaymentPromotionRequest(
            String promotionCode,
            Long promotionId,
            BigDecimal discountAmount,
            BigDecimal originalAmount,
            BigDecimal finalAmount,
            String promotionType,
            Long usageId
    ) {}

    record PaymentPromotionResponse(
            boolean success,
            String message,
            Long paymentId,
            BigDecimal originalAmount,
            BigDecimal discountAmount,
            BigDecimal finalAmount,
            String promotionCode,
            Long promotionId
    ) {}

    record PaymentPromotionInfo(
            Long paymentId,
            String promotionCode,
            Long promotionId,
            BigDecimal discountAmount,
            boolean hasPromotion
    ) {}
}
