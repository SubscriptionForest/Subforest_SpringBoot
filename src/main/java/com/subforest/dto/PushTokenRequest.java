package com.subforest.dto;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class PushTokenRequest {
    //private Long userId;     // JWT 붙이면 생략하고 서버에서 추출
    private String fcmToken; // 안드로이드가 발급받은 토큰
}
