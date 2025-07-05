package com.ram.inventory.util;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * A generic wrapper for all API responses (both success and error).
 *
 * @param <T> the type of data being returned (e.g. DTO, List, null, etc.)
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ApiResponse<T> {
    private int status;       // HTTP status code (e.g., 200, 404)
    private String message;   // Descriptive message
    private T data;           // The actual payload (or null in case of error)
}