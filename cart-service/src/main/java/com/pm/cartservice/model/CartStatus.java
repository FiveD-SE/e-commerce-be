package com.pm.cartservice.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum CartStatus {
    ACTIVE("ACTIVE"),
    CONVERTED_TO_ORDER("CONVERTED_TO_ORDER"),
    EXPIRED("EXPIRED"),
    ABANDONED("ABANDONED");

    private final String status;
} 