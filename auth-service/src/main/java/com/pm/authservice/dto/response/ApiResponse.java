package com.pm.authservice.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {

    private Boolean success;
    private String message;
    private T data;
    private String error;
    private Integer code;
    private Instant timestamp;

    public static <T> ApiResponse<T> success(T data) {
        return ApiResponse.<T>builder()
                .success(true)
                .data(data)
                .timestamp(Instant.now())
                .build();
    }

    public static <T> ApiResponse<T> success(String message, T data) {
        return ApiResponse.<T>builder()
                .success(true)
                .message(message)
                .data(data)
                .timestamp(Instant.now())
                .build();
    }

    public static <T> ApiResponse<T> error(String error) {
        return ApiResponse.<T>builder()
                .success(false)
                .error(error)
                .timestamp(Instant.now())
                .build();
    }

    public static <T> ApiResponse<T> error(String error, Integer code) {
        return ApiResponse.<T>builder()
                .success(false)
                .error(error)
                .code(code)
                .timestamp(Instant.now())
                .build();
    }

} 