package com.pm.authservice.service.impl;

import com.pm.authservice.model.OtpToken;
import com.pm.authservice.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;

@Service
@ConditionalOnProperty(value = "services.user-service.mock-enabled", havingValue = "false", matchIfMissing = true)
@RequiredArgsConstructor
@Slf4j
public class NotificationServiceImpl implements NotificationService {

    private final JavaMailSender mailSender;
    private final WebClient.Builder webClientBuilder;

    @Value("${spring.mail.username}")
    private String fromEmail;

    @Value("${sms.api-url:}")
    private String smsApiUrl;

    @Value("${sms.api-key:}")
    private String smsApiKey;

    @Value("${sms.sender:ECOMMERCE}")
    private String smsSender;

    @Override
    public void sendEmailOtp(String email, String otpCode, String purpose) {
        log.info("Sending OTP email to: {}", maskEmail(email));

        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(email);
            message.setSubject(getEmailSubject(purpose));
            message.setText(getEmailBody(otpCode, purpose));

            mailSender.send(message);
            log.info("OTP email sent successfully to: {}", maskEmail(email));
        } catch (Exception e) {
            log.error("Failed to send OTP email to {}: {}", maskEmail(email), e.getMessage());
            throw new RuntimeException("Failed to send OTP email", e);
        }
    }

    @Override
    public void sendSmsOtp(String phone, String otpCode, String purpose) {
        log.info("Sending OTP SMS to: {}", maskPhone(phone));

        if (smsApiUrl == null || smsApiUrl.trim().isEmpty()) {
            log.warn("SMS API URL not configured. Skipping SMS sending for development.");
            log.info("Development mode: OTP code for {} is: {}", maskPhone(phone), otpCode);
            return;
        }

        try {
            String message = getSmsMessage(otpCode, purpose);
            sendSmsViaApi(phone, message);
            log.info("OTP SMS sent successfully to: {}", maskPhone(phone));
        } catch (Exception e) {
            log.error("Failed to send OTP SMS to {}: {}", maskPhone(phone), e.getMessage());
            throw new RuntimeException("Failed to send OTP SMS", e);
        }
    }

    @Override
    public void sendOtp(OtpToken otpToken) {
        switch (otpToken.getIdentifierType()) {
            case EMAIL:
                sendEmailOtp(otpToken.getIdentifier(), otpToken.getOtpCode(), otpToken.getPurpose());
                break;
            case PHONE:
                sendSmsOtp(otpToken.getIdentifier(), otpToken.getOtpCode(), otpToken.getPurpose());
                break;
            default:
                throw new IllegalArgumentException("Unsupported identifier type: " + otpToken.getIdentifierType());
        }
    }

    private void sendSmsViaApi(String phone, String message) {
        WebClient webClient = webClientBuilder.build();

        Map<String, Object> smsRequest = new HashMap<>();
        smsRequest.put("to", phone);
        smsRequest.put("message", message);
        smsRequest.put("sender", smsSender);

        Mono<String> response = webClient.post()
                .uri(smsApiUrl)
                .header("Authorization", "Bearer " + smsApiKey)
                .header("Content-Type", "application/json")
                .bodyValue(smsRequest)
                .retrieve()
                .bodyToMono(String.class);

        // Execute the request (blocking for simplicity, consider async in production)
        String result = response.block();
        log.debug("SMS API response: {}", result);
    }

    private String getEmailSubject(String purpose) {
        return switch (purpose) {
            case "LOGIN" -> "Your Login OTP Code";
            case "REGISTRATION" -> "Welcome! Verify Your Account";
            case "PASSWORD_RESET" -> "Password Reset OTP";
            default -> "Your OTP Code";
        };
    }

    private String getEmailBody(String otpCode, String purpose) {
        String baseMessage = switch (purpose) {
            case "LOGIN" -> "Your login verification code is: %s\n\nThis code will expire in 5 minutes.\n\nIf you didn't request this code, please ignore this email.";
            case "REGISTRATION" -> "Welcome to our platform!\n\nYour account verification code is: %s\n\nThis code will expire in 5 minutes.\n\nPlease enter this code to complete your registration.";
            case "PASSWORD_RESET" -> "You requested a password reset.\n\nYour verification code is: %s\n\nThis code will expire in 5 minutes.\n\nIf you didn't request this, please ignore this email.";
            default -> "Your verification code is: %s\n\nThis code will expire in 5 minutes.";
        };

        return String.format(baseMessage, otpCode) + 
               "\n\nFor security reasons, never share this code with anyone.\n\nBest regards,\nE-Commerce Team";
    }

    private String getSmsMessage(String otpCode, String purpose) {
        return switch (purpose) {
            case "LOGIN" -> String.format("Your login OTP: %s. Valid for 5 minutes. Do not share.", otpCode);
            case "REGISTRATION" -> String.format("Welcome! Your verification OTP: %s. Valid for 5 minutes.", otpCode);
            case "PASSWORD_RESET" -> String.format("Password reset OTP: %s. Valid for 5 minutes. Do not share.", otpCode);
            default -> String.format("Your OTP: %s. Valid for 5 minutes. Do not share.", otpCode);
        };
    }

    private String maskEmail(String email) {
        if (email == null || !email.contains("@")) {
            return email;
        }
        
        String[] parts = email.split("@");
        if (parts.length != 2) {
            return email;
        }
        
        String localPart = parts[0];
        String domain = parts[1];
        
        if (localPart.length() <= 2) {
            return "*".repeat(localPart.length()) + "@" + domain;
        }
        
        return localPart.substring(0, 2) + "*".repeat(localPart.length() - 2) + "@" + domain;
    }

    private String maskPhone(String phone) {
        if (phone == null || phone.length() < 4) {
            return phone;
        }
        
        return "*".repeat(phone.length() - 4) + phone.substring(phone.length() - 4);
    }
} 