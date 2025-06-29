package com.pm.userservice.mapper;

import com.pm.userservice.dto.UserProfileDto;
import com.pm.userservice.model.User;

public interface UserProfileMapper {

    static UserProfileDto map(final User user) {
        return UserProfileDto.builder()
                .userId(user.getUserId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .imageUrl(user.getImageUrl())
                .email(user.getEmail())
                .phone(user.getPhone())
                .emailSubscription(user.getEmailSubscription())
                .smsSubscription(user.getSmsSubscription())
                .marketingConsent(user.getMarketingConsent())
                .newsletterSubscription(user.getNewsletterSubscription())
                .addresses(user.getAddresses() != null ? AddressMapperUtils.map(user.getAddresses()) : null)
                .wishlists(user.getWishlists() != null ? WishlistMapperUtils.map(user.getWishlists()) : null)
                .build();
    }

    static User map(final UserProfileDto userProfileDto) {
        return User.builder()
                .userId(userProfileDto.getUserId())
                .firstName(userProfileDto.getFirstName())
                .lastName(userProfileDto.getLastName())
                .imageUrl(userProfileDto.getImageUrl())
                .email(userProfileDto.getEmail())
                .phone(userProfileDto.getPhone())
                .emailSubscription(userProfileDto.getEmailSubscription())
                .smsSubscription(userProfileDto.getSmsSubscription())
                .marketingConsent(userProfileDto.getMarketingConsent())
                .newsletterSubscription(userProfileDto.getNewsletterSubscription())
                .build();
    }

}