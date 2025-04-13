package com.example.biofitbe.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FoodSummaryDTO {
    private double totalCalories;
    private double totalProtein;
    private double totalCarb;
    private double totalFat;
}
