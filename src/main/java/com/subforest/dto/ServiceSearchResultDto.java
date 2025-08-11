package com.subforest.dto;


import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ServiceSearchResultDto {
    private Long id;
    private String name;
    private String logoUrl;
}
