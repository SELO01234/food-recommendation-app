package com.app.foodbackend.security.user.repository;

import com.app.foodbackend.security.user.entity.UserFoodRating;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface UserFoodRatingRepository extends JpaRepository<UserFoodRating, Integer> {

    @Query(value = "SELECT EXISTS(SELECT * FROM user_food_rating WHERE user_id= :user_id AND food_id= :food_id)", nativeQuery = true)
    boolean isFoodExistsByIds(@Param("user_id") Integer userId,@Param("food_id") Integer foodId);

    @Query(value = "SELECT food.name FROM user_food_rating JOIN food ON user_food_rating.food_id = food.id WHERE user_food_rating.user_id = :user_id",nativeQuery = true)
    List<String> findFoodNamesByUserId(@Param("user_id") Integer userId);

    @Query(value = "SELECT rating FROM user_food_rating WHERE user_id= :user_id AND food_id= :food_id",nativeQuery = true)
    Float findFoodRatingOfUser(@Param("user_id") Integer userId,@Param("food_id") Integer foodId);

    @Query(value = "SELECT * FROM user_food_rating WHERE user_id= :user_id AND food_id= :food_id", nativeQuery = true)
    UserFoodRating findAFood(@Param("user_id") Integer userId,@Param("food_id") Integer foodId);

    @Query(value = "SELECT rating FROM user_food_rating WHERE food_id= :food_id",nativeQuery = true)
    List<Float> getFoodRatings(@Param("food_id") Integer foodId);

    @Query(value = "SELECT COUNT(*) FROM user_food_rating WHERE food_id= :food_id AND rating != 0",nativeQuery = true)
    Integer getFoodCount(@Param("food_id") Integer foodId);

}
