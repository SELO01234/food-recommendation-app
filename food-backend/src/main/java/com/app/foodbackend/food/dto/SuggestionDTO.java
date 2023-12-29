package com.app.foodbackend.food.dto;

import lombok.Builder;

import java.util.List;


@Builder
public class SuggestionDTO {

    private List<String> indredients;
    private List<String> visitedFoods;

    public SuggestionDTO(List<String> indredients,List<String> visitedFoods){
        this.indredients = indredients;
        this.visitedFoods = visitedFoods;
    }

    public List<String> getIndredients() {
        return indredients;
    }

    public void setIndredients(List<String> indredients) {
        this.indredients = indredients;
    }

    public List<String> getVisitedFoods() {
        return visitedFoods;
    }

    public void setVisitedFoods(List<String> visitedFoods) {
        this.visitedFoods = visitedFoods;
    }
}
