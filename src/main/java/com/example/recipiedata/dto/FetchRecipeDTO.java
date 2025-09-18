package com.example.recipiedata.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FetchRecipeDTO {
    private Long id;
    private String title;
    private String cuisine;
    private Float rating;
    private Integer prep_time;
    private Integer cook_time;
    private Integer total_time;
    private String description;
    private String serves;
    private Map<String, String> nutrients;
}
