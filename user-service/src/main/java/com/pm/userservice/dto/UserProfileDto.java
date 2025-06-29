package com.pm.userservice.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Set;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserProfileDto implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer userId;
    private String firstName;
    private String lastName;
    private String imageUrl;
    private String email;
    private String phone;
    private Boolean emailSubscription;
    private Boolean smsSubscription;
    private Boolean marketingConsent;
    private Boolean newsletterSubscription;
    private Set<AddressDto> addresses;
    private Set<WishlistDto> wishlists;

} 