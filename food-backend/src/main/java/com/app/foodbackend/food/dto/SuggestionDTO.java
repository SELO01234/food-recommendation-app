package com.app.foodbackend.food.dto;

import lombok.Builder;

import java.util.List;


@Builder
public class SuggestionDTO {

    private List<String> ingredients;
    private List<String> visitedFoods;
    private Integer page_num;
    private Integer page_size;

    public SuggestionDTO(List<String> ingredients,List<String> visitedFoods, Integer page_num, Integer page_size){
        this.ingredients = ingredients;
        this.visitedFoods = visitedFoods;
        this.page_num = page_num;
        this.page_size = page_size;
    }

    public List<String> getIngredients() {
        return ingredients;
    }

    public void setIngredients(List<String> ingredients) {
        this.ingredients = ingredients;
    }

    public List<String> getVisitedFoods() {
        return visitedFoods;
    }

    public void setVisitedFoods(List<String> visitedFoods) {
        this.visitedFoods = visitedFoods;
    }

    public Integer getPage_num() {
        return page_num;
    }

    public void setPage_num(Integer page_num) {
        this.page_num = page_num;
    }

    public Integer getPage_size() {
        return page_size;
    }

    public void setPage_size(Integer page_size) {
        this.page_size = page_size;
    }
}
