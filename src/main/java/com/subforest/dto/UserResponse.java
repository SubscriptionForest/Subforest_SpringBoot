package com.subforest.dto;

import lombok.Getter;
import lombok.AllArgsConstructor;

@Getter
@AllArgsConstructor
public class UserResponse {
    private Long id;
    private String email;
    private String name;
}
