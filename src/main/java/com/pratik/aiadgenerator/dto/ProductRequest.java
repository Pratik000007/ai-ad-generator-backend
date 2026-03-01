package com.pratik.aiadgenerator.dto;



import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProductRequest {

    private String name;
    private String description;
    private Double price;
    private String category;
    private String targetAudience;
}
