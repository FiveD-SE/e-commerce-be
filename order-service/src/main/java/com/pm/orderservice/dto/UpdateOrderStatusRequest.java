package com.pm.orderservice.dto;

import com.pm.orderservice.model.OrderStatus;
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
public class UpdateOrderStatusRequest implements Serializable {
    private static final long serialVersionUID = 1L;

    @NotNull(message = "Order status is required")
    private OrderStatus status;
    
    private String adminNotes;
    private String cancellationReason;
    private String trackingNumber;
}
