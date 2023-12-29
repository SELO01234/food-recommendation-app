package com.app.foodbackend.food.service;


import com.app.foodbackend.food.dto.FoodResponse;
import com.app.foodbackend.food.dto.SuggestionDTO;
import com.app.foodbackend.food.entity.Food;
import com.app.foodbackend.food.dto.FoodDTO;
import com.app.foodbackend.food.repository.FoodRepository;
import com.app.foodbackend.search.model.SearchRequestDTO;
import com.app.foodbackend.search.service.SearchService;
import com.app.foodbackend.security.user.entity.User;
import com.app.foodbackend.security.user.entity.UserFoodRating;
import com.app.foodbackend.security.user.repository.UserFoodRatingRepository;
import com.app.foodbackend.security.user.repository.UserRepository;
import com.app.foodbackend.security.user.service.UserService;
import com.app.foodbackend.util.UserUtil;
import jakarta.annotation.PostConstruct;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

@Service
public class FoodService extends SearchService<Food> {

    private final FoodRepository foodRepository;
    private final UserFoodRatingRepository userFoodRatingRepository;
    private final UserRepository userRepository;
    private final RestTemplate restTemplate;

    private final UserService userService;

    private static final String SECRET_KEY_FOR_FLASK = "ryt4f7@jvrn(6w^cg_d+idh3mj7=a!6fguh92rn)#x-omd2^$p";

    @Autowired
    public FoodService(EntityManager entityManager, FoodRepository foodRepository, UserFoodRatingRepository userFoodRatingRepository, UserRepository userRepository, RestTemplate restTemplate, UserService userService) {
        super(entityManager);
        this.foodRepository = foodRepository;
        this.userFoodRatingRepository = userFoodRatingRepository;
        this.userRepository = userRepository;
        this.restTemplate = restTemplate;
        this.userService = userService;
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
        food.setIngredients(convertSingleQuotes(values[2]));
        food.setIngredientsRawStr(removeQuotes(values[3]));
        food.setServingSize(values[4]);
        food.setServings(Integer.parseInt(values[5]));
        food.setSteps(convertSingleQuotes(values[6]));
        food.setTags(convertSingleQuotes(values[7]));
        food.setSearchTerms(convertSingleQuotesAndCurlyBrackets(values[8]));

        return food;
    }

    private String removeQuotes(String value) {
        String trimmedValue = value.replaceAll("^\"|\"$", "");
        return trimmedValue.replaceAll("\"{2}", "\"");
    }
    private String convertSingleQuotes(String value) {
        // Replace single quotes with double quotes
        return value.replaceAll("'", "\"");
    }

    private String convertSingleQuotesAndCurlyBrackets(String value) {
        // Replace single quotes with double quotes and curly brackets with square brackets
        return value.replaceAll("'", "\"").replaceAll("\\{", "[").replaceAll("\\}", "]");
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

    public List<Food> getFoodSuggestion(List<String> ingredients) throws Exception {

        List<String> visitedFoods = userService.findVisitedFoodsByUserId();

        SuggestionDTO suggestionDTO = new SuggestionDTO(ingredients, visitedFoods);

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + SECRET_KEY_FOR_FLASK);
        HttpEntity<SuggestionDTO> requestEntity = new HttpEntity<>(suggestionDTO, headers);

        List<Food> result = restTemplate.postForObject("http://data-service/get-food", requestEntity,List.class);
        return result;
    }

    public List<Food> getAllFoods() {
        return foodRepository.findAll();
    }

    public FoodResponse getFoodById(Integer foodId) {
        Food food = foodRepository.findById(foodId).orElseThrow();

        String username = UserUtil.getUsername();
        Integer userId = (userRepository.findByUserName(username).orElseThrow()).getId();

        Float userRating = userFoodRatingRepository.findFoodRatingOfUser(userId, foodId);

        if(userRating == null){
            userRating = 0.0F;
        }

        return FoodResponse.builder()
                .name(food.getName())
                .description(food.getDescription())
                .ingredients(food.getIngredients())
                .ingredientsRawStr(food.getIngredientsRawStr())
                .servingSize(food.getServingSize())
                .servings(food.getServings())
                .steps(food.getSteps())
                .tags(food.getTags())
                .searchTerms(food.getSearchTerms())
                .rating(food.getRating())
                .userRating(userRating)
                .build();
    }

    public List<String> getAllIngredients() {
        return foodRepository.findUniqueIngredients();
    }

    @Transactional
    public void updateRating(Integer foodId, float rating) throws Exception{

        String username = UserUtil.getUsername();

        User user = userRepository.findByUserName(username).orElseThrow();

        //If user did not visit this food
        if(!userFoodRatingRepository.isFoodExistsByIds(user.getId(), foodId)){
            throw new Exception();
        }

        UserFoodRating userFoodRating = userFoodRatingRepository.findAFood(user.getId(), foodId);

        updateUsersFoodRating(userFoodRating, rating);

        List<Float> ratings = userFoodRatingRepository.getFoodRatings(foodId);
        Float count = (userFoodRatingRepository.getFoodCount(foodId)).floatValue();

        Float newRating= 0.0F;

        for(int i=0; i < ratings.size(); ++i){
            newRating += ratings.get(i);
        }

        newRating = newRating / count;

        Food food = foodRepository.findById(foodId).orElseThrow();
        food.setRating(newRating);

        foodRepository.save(food);

    }

    @Transactional
    public void updateUsersFoodRating(UserFoodRating userFoodRating,float rating){
        userFoodRating.setRating(rating);
        userFoodRatingRepository.save(userFoodRating);
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
