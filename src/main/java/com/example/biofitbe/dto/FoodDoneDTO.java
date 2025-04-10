package com.example.biofitbe.dto;

import com.example.biofitbe.model.FoodDone;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class FoodDoneDTO {
    private Long foodDoneId;
    private Long foodId;
    private String date;
    private String session;

    public FoodDoneDTO(FoodDone foodDone) {
        this.foodDoneId = foodDone.getFoodDoneId();
        this.foodId = foodDone.getFood().getFoodId();
        this.date = foodDone.getDate();
        this.session = foodDone.getSession();
    }
}