package io.muzoo.ssc.plogit.web.dto;

public record ApiResponse(
    String message
) {
    public static ApiResponse of(String message) {
        return new ApiResponse(message);
    }
}
