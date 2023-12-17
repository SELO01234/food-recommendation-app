package com.app.foodbackend.food.repository;

import com.app.foodbackend.food.entity.Food;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FoodRepository extends JpaRepository<Food, Integer> {
    @Query(value = "SELECT COUNT(*) FROM food", nativeQuery = true)
    int numberOfRecords();

    @Query(value =
            "SELECT DISTINCT\n" +
            "    trim('\"' FROM regexp_split_to_table(\n" +
            "        regexp_replace(ingredients, E'\\\\[|\\\\]', '', 'g'), \n" +
            "        E'\\\\s*,\\\\s*'\n" +
            "    )) AS unique_ingredients\n" +
            "FROM food;\n"
            , nativeQuery = true)
    List<String> findUniqueIngredients();
}
