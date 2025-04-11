package com.example.biofitbe.dto;

import com.example.biofitbe.model.Food;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Arrays;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class FoodDTO {
    private Long foodId;
    private Long userId;
    private String foodName;
    private String date;
    private String session;
    private byte[] foodImage;
    private Float servingSize;
    private String servingSizeUnit;
    private Float mass;
    private Float calories;
    private Float protein;
    private Float carbohydrate;
    private Float fat;
    private Float sodium;

    public FoodDTO(Food food) {
        this.foodId = food.getFoodId();
        this.userId = food.getUser().getUserId();
        this.foodName = food.getFoodName();
        this.date = food.getDate();
        this.session = food.getSession();
        this.foodImage = food.getFoodImage();
        this.servingSize = food.getServingSize();
        this.servingSizeUnit = food.getServingSizeUnit();
        this.mass = food.getMass();
        this.calories = food.getCalories();
        this.protein = food.getProtein();
        this.carbohydrate = food.getCarbohydrate();
        this.fat = food.getFat();
        this.sodium = food.getSodium();
    }
}
