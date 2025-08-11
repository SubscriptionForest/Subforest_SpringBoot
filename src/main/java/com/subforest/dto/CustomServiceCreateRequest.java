package com.subforest.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class CustomServiceCreateRequest {
    @NotNull
    private Long userId;
    @NotBlank
    private String name;
    private String logoUrl; // 업로드 기능 나중에 붙일 예정이면 경로 문자열
}
