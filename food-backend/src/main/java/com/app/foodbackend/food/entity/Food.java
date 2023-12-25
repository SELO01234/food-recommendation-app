package com.app.foodbackend.food.entity;

import com.app.foodbackend.security.user.entity.UserFoodRating;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Set;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "food")
public class Food {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(columnDefinition = "TEXT")
    private String ingredients;

    @Column(name = "ingredients_raw_str",columnDefinition = "TEXT")
    private String ingredientsRawStr;

    @Column(columnDefinition = "TEXT")
    private String servingSize;

    private Integer servings;

    @Column(columnDefinition = "TEXT")
    private String steps;

    @Column(columnDefinition = "TEXT")
    private String tags;

    @Column(columnDefinition = "TEXT")
    private String searchTerms;

    private float rating;

    @JsonIgnore
    @OneToMany(mappedBy = "food")
    private List<UserFoodRating> userLogs;
}
