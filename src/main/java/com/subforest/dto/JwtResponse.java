package com.subforest.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class JwtResponse {
    private final String token;
    private final String tokenType = "Bearer";
}
