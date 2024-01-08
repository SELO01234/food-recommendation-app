package com.app.foodbackend.food.controller;

import com.app.foodbackend.food.dto.FoodCardResponse;
import com.app.foodbackend.food.dto.FoodResponse;
import com.app.foodbackend.food.dto.SuggestionDTO;
import com.app.foodbackend.food.dto.recommendation.PaginatedFood;
import com.app.foodbackend.food.dto.recommendation.RecommendationRes;
import com.app.foodbackend.food.entity.Food;
import com.app.foodbackend.food.dto.FoodDTO;
import com.app.foodbackend.food.service.FoodService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/food")
@RequiredArgsConstructor
public class FoodController {

    private final FoodService foodService;
    @PostMapping("/get-suggestion")
    public ResponseEntity<PaginatedFood> getFoodBySuggestion(@RequestBody SuggestionDTO suggestionDTO){
        try{
            return ResponseEntity.ok().body(foodService.getFoodSuggestion(suggestionDTO));
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/get/all-foods")
    public List<Food> getAllFoods(){
        return foodService.getAllFoods();
    }

    @GetMapping("/get/highest-rated/foods")
    public ResponseEntity<List<FoodCardResponse>> getHighestRatedFoods(){
        try{
            return ResponseEntity.ok().body(foodService.getHighestRatedFoods());
        }
        catch (Exception exception){
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/get/{id}")
    public ResponseEntity<FoodResponse> getFoodById(@PathVariable("id") Integer id){
        try{
            return ResponseEntity.ok().body(foodService.getFoodById(id));
        }
        catch(Exception exception){
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/get/ingredients")
    public ResponseEntity<List<String>> getAllIngredients(){
        return ResponseEntity.ok().body(foodService.getAllIngredients());
    }

    @PostMapping("/add")
    public ResponseEntity<String> addFood(@RequestBody FoodDTO foodDTO){
        try {
            foodService.addFood(foodDTO);
            return ResponseEntity.ok().body("Food added successfully");
        }
        catch (Exception e){
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/update/rating/{id}/{rating}")
    public ResponseEntity<String> updateRating(@PathVariable("id") Integer foodId, @PathVariable("rating") float rating){
        try{
            foodService.updateRating(foodId, rating);
            return ResponseEntity.ok().body("Rating updated successfully");
        }
        catch (Exception exception){
            return ResponseEntity.unprocessableEntity().build();
        }
    }
}
