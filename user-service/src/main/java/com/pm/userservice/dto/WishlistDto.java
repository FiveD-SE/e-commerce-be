package com.pm.userservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.Instant;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class WishlistDto implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer wishlistId;
    private Integer productId;
    private Integer userId;
    private Instant createdAt;

} 