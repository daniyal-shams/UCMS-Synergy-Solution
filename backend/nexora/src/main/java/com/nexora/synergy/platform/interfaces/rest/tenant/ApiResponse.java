package com.nexora.synergy.platform.interfaces.rest.tenant;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * A generic wrapper for API responses.
 *
 * @param <T> The type of the data payload.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {

    private final T data;

    private ApiResponse(T data) {
        this.data = data;
    }

    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(data);
    }

    public T getData() {
        return data;
    }
}
