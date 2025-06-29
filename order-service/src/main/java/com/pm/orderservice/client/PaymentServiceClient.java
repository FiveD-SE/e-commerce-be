package com.pm.orderservice.client;

import com.pm.orderservice.dto.payment.CreatePaymentRequest;
import com.pm.orderservice.dto.payment.PaymentDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
@RequiredArgsConstructor
@Slf4j
public class PaymentServiceClient {

    private final RestTemplate restTemplate;

    @Value("${services.payment.url:http://payment-service:8800/payment-service}")
    private String paymentServiceUrl;

    public PaymentDto createPayment(CreatePaymentRequest request) {
        try {
            log.info("Creating payment for order ID: {}", request.getOrderId());
            String url = paymentServiceUrl + "/api/payments";
            PaymentDto response = restTemplate.postForObject(url, request, PaymentDto.class);
            log.info("Payment created successfully with ID: {}", response.getPaymentId());
            return response;
        } catch (Exception e) {
            log.error("Failed to create payment for order ID: {}", request.getOrderId(), e);
            throw new RuntimeException("Failed to create payment: " + e.getMessage());
        }
    }

    public PaymentDto getPaymentById(Long paymentId) {
        try {
            log.info("Fetching payment with ID: {}", paymentId);
            String url = paymentServiceUrl + "/api/payments/" + paymentId;
            PaymentDto response = restTemplate.getForObject(url, PaymentDto.class);
            log.info("Payment fetched successfully: {}", paymentId);
            return response;
        } catch (Exception e) {
            log.error("Failed to fetch payment with ID: {}", paymentId, e);
            throw new RuntimeException("Failed to fetch payment: " + e.getMessage());
        }
    }

    public PaymentDto getPaymentByReference(String paymentReference) {
        try {
            log.info("Fetching payment with reference: {}", paymentReference);
            String url = paymentServiceUrl + "/api/payments/reference/" + paymentReference;
            PaymentDto response = restTemplate.getForObject(url, PaymentDto.class);
            log.info("Payment fetched successfully: {}", paymentReference);
            return response;
        } catch (Exception e) {
            log.error("Failed to fetch payment with reference: {}", paymentReference, e);
            throw new RuntimeException("Failed to fetch payment: " + e.getMessage());
        }
    }

    public PaymentDto confirmPayment(Long paymentId, String gatewayTransactionId) {
        try {
            log.info("Confirming payment ID: {} with gateway transaction ID: {}", paymentId, gatewayTransactionId);
            String url = paymentServiceUrl + "/api/payments/" + paymentId + "/confirm?gatewayTransactionId=" + gatewayTransactionId;
            PaymentDto response = restTemplate.exchange(url, org.springframework.http.HttpMethod.PUT, null, PaymentDto.class).getBody();
            log.info("Payment confirmed successfully: {}", paymentId);
            return response;
        } catch (Exception e) {
            log.error("Failed to confirm payment ID: {}", paymentId, e);
            throw new RuntimeException("Failed to confirm payment: " + e.getMessage());
        }
    }

    public PaymentDto failPayment(Long paymentId, String reason, String errorCode) {
        try {
            log.info("Failing payment ID: {} with reason: {}", paymentId, reason);
            String url = paymentServiceUrl + "/api/payments/" + paymentId + "/fail?reason=" + reason;
            if (errorCode != null) {
                url += "&errorCode=" + errorCode;
            }
            PaymentDto response = restTemplate.exchange(url, org.springframework.http.HttpMethod.PUT, null, PaymentDto.class).getBody();
            log.info("Payment failed successfully: {}", paymentId);
            return response;
        } catch (Exception e) {
            log.error("Failed to fail payment ID: {}", paymentId, e);
            throw new RuntimeException("Failed to fail payment: " + e.getMessage());
        }
    }
}
