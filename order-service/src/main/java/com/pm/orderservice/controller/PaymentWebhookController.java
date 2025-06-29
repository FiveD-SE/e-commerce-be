package com.pm.orderservice.controller;

import com.pm.orderservice.dto.OrderDto;
import com.pm.orderservice.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/orders/webhooks")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Payment Webhook", description = "APIs for handling payment webhooks")
public class PaymentWebhookController {

    private final OrderService orderService;

    @PostMapping("/payment-status")
    @Operation(summary = "Handle payment status webhook from Payment Service")
    public ResponseEntity<String> handlePaymentStatusWebhook(@RequestBody Map<String, Object> payload) {
        try {
            log.info("Received payment status webhook: {}", payload);
            
            Long orderId = Long.valueOf(payload.get("orderId").toString());
            String paymentStatus = payload.get("paymentStatus").toString();
            String gatewayTransactionId = payload.get("gatewayTransactionId") != null ? 
                    payload.get("gatewayTransactionId").toString() : null;
            
            OrderDto updatedOrder = orderService.updatePaymentStatus(orderId, paymentStatus, gatewayTransactionId);
            
            log.info("Payment status updated for order ID: {} to {}", orderId, paymentStatus);
            return ResponseEntity.ok("Payment status updated successfully");
            
        } catch (Exception e) {
            log.error("Failed to process payment status webhook", e);
            return ResponseEntity.badRequest().body("Failed to process webhook: " + e.getMessage());
        }
    }

    @PostMapping("/payment-confirmed")
    @Operation(summary = "Handle payment confirmation webhook from Payment Service")
    public ResponseEntity<String> handlePaymentConfirmationWebhook(@RequestBody Map<String, Object> payload) {
        try {
            log.info("Received payment confirmation webhook: {}", payload);
            
            Long orderId = Long.valueOf(payload.get("orderId").toString());
            String gatewayTransactionId = payload.get("gatewayTransactionId").toString();
            
            OrderDto updatedOrder = orderService.confirmPayment(orderId, gatewayTransactionId);
            
            log.info("Payment confirmed for order ID: {}", orderId);
            return ResponseEntity.ok("Payment confirmed successfully");
            
        } catch (Exception e) {
            log.error("Failed to process payment confirmation webhook", e);
            return ResponseEntity.badRequest().body("Failed to process webhook: " + e.getMessage());
        }
    }

    @PostMapping("/payment-failed")
    @Operation(summary = "Handle payment failure webhook from Payment Service")
    public ResponseEntity<String> handlePaymentFailureWebhook(@RequestBody Map<String, Object> payload) {
        try {
            log.info("Received payment failure webhook: {}", payload);
            
            Long orderId = Long.valueOf(payload.get("orderId").toString());
            String reason = payload.get("reason") != null ? payload.get("reason").toString() : "Payment failed";
            
            OrderDto updatedOrder = orderService.failPayment(orderId, reason);
            
            log.info("Payment failed for order ID: {}", orderId);
            return ResponseEntity.ok("Payment failure processed successfully");
            
        } catch (Exception e) {
            log.error("Failed to process payment failure webhook", e);
            return ResponseEntity.badRequest().body("Failed to process webhook: " + e.getMessage());
        }
    }
}
