package com.example.recipiedata.model;

import io.hypersistence.utils.hibernate.type.json.JsonType;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.Type;

import java.util.Map;

@Data
@Entity
@Table(name = "recipes")
public class Recipe{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private String cuisine;
    private Float rating;
    private Integer prep_time;
    private Integer cook_time;
    private Integer total_time;

    @Column(columnDefinition = "TEXT")
    private String description;
    private String serves;

    @Type(JsonType.class)
    @Column(columnDefinition = "jsonb")
    private Map<String, String> nutrients;
}