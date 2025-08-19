package com.subforest.dto;

import lombok.Getter;
import lombok.AllArgsConstructor;

@Getter
@AllArgsConstructor
public class LoginResponse {
    private String token;   // JWT 토큰
    private String email;   // 사용자 이메일
    private String name;    // 사용자 이름
}
