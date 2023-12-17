package com.app.foodbackend.food.controller;

import com.app.foodbackend.food.entity.Food;
import com.app.foodbackend.food.entity.FoodDTO;
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
    @PostMapping("/get")
    public Food getFood(@RequestBody List<String> ingredients){
        return foodService.getFood(ingredients);
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
            return ResponseEntity.badRequest().build();
        }
    }
}
