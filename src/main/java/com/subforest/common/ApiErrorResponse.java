package com.subforest.common;

import lombok.Builder;
import lombok.Getter;
import java.time.LocalDateTime;

@Getter
@Builder
public class ApiErrorResponse {
    private LocalDateTime timestamp;
    private String error;
    private String message;
    private String path;
}
