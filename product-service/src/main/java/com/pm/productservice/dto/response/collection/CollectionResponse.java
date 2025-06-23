package com.pm.productservice.dto.response.collection;

import lombok.Builder;
import lombok.Getter;

import java.util.Collection;

@Builder
@Getter
public class CollectionResponse<T> {
    private Collection<T> data;
    private Integer totalElements;
    private Integer page;
    private Integer size;
} 