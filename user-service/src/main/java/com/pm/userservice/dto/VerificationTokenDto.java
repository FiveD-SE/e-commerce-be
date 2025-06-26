package com.pm.userservice.dto;

import java.io.Serializable;
import java.time.LocalDate;

import com.pm.userservice.constant.AppConstants;
import org.springframework.format.annotation.DateTimeFormat;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonFormat.Shape;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class VerificationTokenDto implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private Integer verificationTokenId;
	
	private String token;
	
	@JsonFormat(pattern = AppConstants.LOCAL_DATE_FORMAT, shape = Shape.STRING)
	@DateTimeFormat(pattern = AppConstants.LOCAL_DATE_FORMAT)
	private LocalDate expireDate;

    @JsonProperty("credentialId")
    @JsonInclude(value = JsonInclude.Include.NON_NULL)
    private Integer credentialId;
}
