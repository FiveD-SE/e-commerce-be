package com.pm.orderservice.dto.response.collection;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class CollectionResponse<T> implements Serializable {
    private List<T> data;
    private int totalElements;
} 