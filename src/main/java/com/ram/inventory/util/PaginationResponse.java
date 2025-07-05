package com.ram.inventory.util;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class PaginationResponse<T> {
    private List<T> data;
    private PaginationInfo pagination;

    @Data
    @AllArgsConstructor
    public static class PaginationInfo {
        private int pageNumber;
        private int pageSize;
        private int totalPages;
        private long totalElements;
    }
}