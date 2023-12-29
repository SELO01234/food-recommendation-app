package com.app.foodbackend.food.dto;

import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FoodDTO {
    private Integer id;

    private String name;

    private String description;

    private String ingredients;

    private String ingredientsRawStr;

    private String servingSize;

    private Integer servings;

    private String steps;

    private String tags;

    private String searchTerms;

    private Integer rating;
}
