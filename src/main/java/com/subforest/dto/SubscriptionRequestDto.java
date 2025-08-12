package com.subforest.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import lombok.Data;

/*
 * SubscriptionRequestDto:
 * - 생성/수정 요청용 DTO
 * - 형식 검증은 @Valid, 비즈니스 검증(XOR, 허용 주기)은 Service에서 처리
 */

@Data
public class SubscriptionRequestDto {

    @NotNull(message = "User ID는 필수입니다.")
    private Long userId;

    // 공통 서비스 ID 또는 사용자 커스텀 서비스 ID 중 하나만 입력
    private Long serviceId;           // 공통 서비스
    private Long customServiceId;     // 커스텀 서비스

    @NotNull(message = "금액은 필수입니다.")
    @Positive(message = "금액은 양수여야 합니다.")
    private Integer amount;

    @NotNull(message = "시작 날짜는 필수입니다.")
    private String startDate; // yyyy-MM-dd

    @NotNull(message = "반복 주기는 필수입니다.")
    @Pattern(regexp = "30|90|180|365", message = "반복 주기는 30/90/180/365 중 하나여야 합니다.")
    private Integer repeatCycleDays; // 30, 90, 180, 365 중 택 1

    @NotNull
    private Boolean autoPayment;

    @NotNull
    private Boolean isShared;
}
