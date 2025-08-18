package com.subforest.dto;


import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ServiceSearchResultDto {
    private Long id;
    private String name;
    private String logoUrl;
}
