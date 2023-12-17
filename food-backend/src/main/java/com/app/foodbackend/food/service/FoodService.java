package com.app.foodbackend.food.service;


import com.app.foodbackend.food.entity.Food;
import com.app.foodbackend.food.entity.FoodDTO;
import com.app.foodbackend.food.repository.FoodRepository;
import com.app.foodbackend.search.model.RequestFilter;
import com.app.foodbackend.search.model.SearchRequestDTO;
import com.app.foodbackend.search.service.SearchService;
import jakarta.annotation.PostConstruct;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class FoodService extends SearchService<Food> {

    private final FoodRepository foodRepository;
    private final RestTemplate restTemplate;

    @Autowired
    public FoodService(EntityManager entityManager, FoodRepository foodRepository, RestTemplate restTemplate) {
        super(entityManager);
        this.foodRepository = foodRepository;
        this.restTemplate = restTemplate;
    }

    @PostConstruct
    @Transactional
    public void importDataFromCSV(){
        if(foodRepository.numberOfRecords() == 0){
            String csvFilePath = "C:\\Users\\SELO\\OneDrive\\Masaüstü\\recipes_two.csv";
            try {
                BufferedReader br = new BufferedReader(new FileReader(csvFilePath));
                String headerLine = br.readLine();

                String line;
                while ((line = br.readLine()) != null) {
                    // Parse the CSV line and create a Food entity
                    Food food = createFoodFromCSVLine(line);

                    // Save the Food entity to the database
                    foodRepository.save(food);
                }
            }
            catch (IOException e){
                System.out.println("exception");
            }
        }
    }

    private Food createFoodFromCSVLine(String csvLine) {
        String[] values = csvLine.split(",(?=([^\\\"]*\\\"[^\\\"]*\\\")*[^\\\"]*$)"); // Assuming CSV is comma-separated

        Food food = new Food();
        food.setName(values[0]);
        food.setDescription(values[1]);
        food.setIngredients(values[2]);
        food.setIngredientsRawStr(values[3]);
        food.setServingSize(values[4]);
        food.setServings(Integer.parseInt(values[5]));
        food.setSteps(values[6]);
        food.setTags(values[7]);
        food.setSearchTerms(values[8]);

        return food;
    }

    public void addFood(FoodDTO foodDTO) throws Exception {
        if(foodDTO == null){
            throw new Exception();
        }

        Food food = Food.builder()
                .name(foodDTO.getName())
                .description(foodDTO.getDescription())
                .ingredients(foodDTO.getIngredients())
                .ingredientsRawStr(foodDTO.getIngredientsRawStr())
                .steps(foodDTO.getSteps())
                .tags(foodDTO.getTags())
                .servingSize(foodDTO.getServingSize())
                .servings(foodDTO.getServings())
                .build();

        foodRepository.save(food);
    }

    public Food getFood(List<String> ingredients) {

        List<RequestFilter> requestFilters = ingredients.stream().map( ingredient -> {

            String ingredientString = "\'" + ingredient + "\'";

            RequestFilter requestFilter = RequestFilter.builder()
                    .field("ingredients")
                    .value(ingredientString)
                    .operator("CONTAINS")
                    .build();

            return requestFilter;
        }).filter(Objects::nonNull).collect(Collectors.toList());

        SearchRequestDTO requestDTO = SearchRequestDTO.builder()
                .filters(requestFilters)
                .build();

        List<Food> foods = search(requestDTO);
        Food result = restTemplate.postForObject("http://127.0.0.1:5000/get-food", foods,Food.class);
        return result;
    }

    public List<String> getAllIngredients() {
        return foodRepository.findUniqueIngredients();
    }

    @Override
    public List<Food> search(SearchRequestDTO requestDTO){
        return super.search(requestDTO);
    }

    @Override
    protected Class<Food> getEntityClass() {
        return Food.class;
    }
}
