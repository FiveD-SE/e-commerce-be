package com.pm.productservice.dto.response.collection;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Collection;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CollectionResponse<T> {
    private Collection<T> data;
    private Integer totalElements;
    private Integer page;
    private Integer size;
} 