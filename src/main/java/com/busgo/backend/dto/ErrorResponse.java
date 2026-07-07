package com.busgo.backend.dto;
import lombok.Builder;
import lombok.Data;
@Data
@Builder
public class ErrorResponse {
    private ErrorDetail error;
    
    @Data
    @Builder
    public static class ErrorDetail {
        private String code;
        private String message;
        private Object details;
    }
}
