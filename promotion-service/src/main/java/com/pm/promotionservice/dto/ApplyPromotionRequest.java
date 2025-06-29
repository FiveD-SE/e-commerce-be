package com.pm.promotionservice.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class ApplyPromotionRequest implements Serializable {
    private static final long serialVersionUID = 1L;

    @NotBlank(message = "Promotion code is required")
    private String promotionCode;

    @NotNull(message = "User ID is required")
    private Integer userId;

    @NotNull(message = "Order ID is required")
    private Long orderId;

    @NotNull(message = "Order amount is required")
    @DecimalMin(value = "0.01", message = "Order amount must be greater than 0")
    private BigDecimal orderAmount;

    private List<Long> productIds;
    private List<Long> categoryIds;
    private List<Long> brandIds;
    private String userGroup;
    private Boolean isFirstTimeUser;
    private String ipAddress;
    private String userAgent;
    private String notes;
}
