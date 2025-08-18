package com.subforest.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

//커스텀 생성 시 받는 요청 바디 모델
@Getter @Setter
public class CustomServiceCreateRequest {
    @NotNull
    private Long userId; //JWT 붙인 뒤에는 userId는 바디에서 제거하고, 토큰에서 추출한 값 사용
    @NotBlank
    private String name; //서비스명
    private String logoUrl;
}
