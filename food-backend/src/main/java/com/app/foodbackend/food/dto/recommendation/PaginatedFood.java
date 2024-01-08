package com.app.foodbackend.food.dto.recommendation;

import com.app.foodbackend.food.dto.FoodResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaginatedFood {
    List<FoodResponse> filtered_recommendations;
    Integer page_size;
    PaginationResponse pagination;
    Integer total;
}
