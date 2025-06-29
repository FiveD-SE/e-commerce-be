package com.pm.userservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotNull;
import java.io.Serializable;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class SubscriptionRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotNull(message = "User ID must not be null")
    private Integer userId;

    private Boolean emailSubscription;
    private Boolean smsSubscription;
    private Boolean marketingConsent;
    private Boolean newsletterSubscription;

} 