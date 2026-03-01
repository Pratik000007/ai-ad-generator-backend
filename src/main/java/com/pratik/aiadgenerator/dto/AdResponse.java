package com.pratik.aiadgenerator.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AdResponse {

    private Long id;
    private String headline;
    private String description;
    private String cta;
    private String platform;
    private String imageUrl;
}