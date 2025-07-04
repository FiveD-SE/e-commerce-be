package com.pm.userservice.dto;

import java.io.Serializable;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class UserDto implements Serializable {
	
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

    private Integer credentialId;

    @JsonInclude(value = Include.NON_NULL)
    private Set<AddressDto> addressDtos;

    @JsonInclude(value = Include.NON_NULL)
    private Set<WishlistDto> wishlistDtos;
}










