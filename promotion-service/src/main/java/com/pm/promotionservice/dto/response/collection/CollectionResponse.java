package com.pm.promotionservice.dto.response.collection;

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
    private static final long serialVersionUID = 1L;

    private List<T> data;
    private long totalElements;
    private int totalPages;
    private int currentPage;
    private int pageSize;
    private boolean hasNext;
    private boolean hasPrevious;

    public CollectionResponse(List<T> data) {
        this.data = data;
        this.totalElements = data != null ? data.size() : 0;
        this.totalPages = 1;
        this.currentPage = 0;
        this.pageSize = (int) this.totalElements;
        this.hasNext = false;
        this.hasPrevious = false;
    }

    public CollectionResponse(List<T> data, long totalElements, int totalPages, int currentPage, int pageSize) {
        this.data = data;
        this.totalElements = totalElements;
        this.totalPages = totalPages;
        this.currentPage = currentPage;
        this.pageSize = pageSize;
        this.hasNext = currentPage < totalPages - 1;
        this.hasPrevious = currentPage > 0;
    }

    public static <T> CollectionResponse<T> of(List<T> data) {
        return new CollectionResponse<>(data);
    }

    public static <T> CollectionResponse<T> of(List<T> data, long totalElements, int totalPages, int currentPage, int pageSize) {
        return new CollectionResponse<>(data, totalElements, totalPages, currentPage, pageSize);
    }

    public boolean isEmpty() {
        return data == null || data.isEmpty();
    }

    public int getSize() {
        return data != null ? data.size() : 0;
    }
}
