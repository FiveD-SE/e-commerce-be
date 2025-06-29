package com.pm.paymentservice.dto.response.collection;

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
    
    // Convenience constructor for simple lists without pagination
    public CollectionResponse(List<T> data) {
        this.data = data;
        this.totalElements = data != null ? data.size() : 0;
        this.totalPages = 1;
        this.currentPage = 0;
        this.pageSize = (int) this.totalElements;
    }
}
