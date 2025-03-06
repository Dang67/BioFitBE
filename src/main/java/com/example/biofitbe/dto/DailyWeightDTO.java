package com.example.biofitbe.dto;

import com.example.biofitbe.model.DailyWeight;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class DailyWeightDTO {
    private Long dailyWeightId;
    private Long userId;
    private Float weight;
    private String date;

    // ✅ Constructor để chuyển từ entity DailyWeight sang DTO
    public DailyWeightDTO(DailyWeight weight) {
        this.dailyWeightId = weight.getDailyWeightId();
        this.userId = weight.getUser().getUserId(); // Lấy ID của user
        this.weight = weight.getWeight();
        this.date = weight.getDate();
    }
}
