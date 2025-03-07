package com.example.biofitbe.dto;

import com.example.biofitbe.model.DailyLog;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class DailyLogDTO {
    private Long dailyWeightId;
    private Long userId;
    private Float weight;
    private Float water;
    private String date;

    // ✅ Constructor để chuyển từ entity DailyWeight sang DTO
    public DailyLogDTO(DailyLog weight) {
        this.dailyWeightId = weight.getDailyWeightId();
        this.userId = weight.getUser().getUserId(); // Lấy ID của user
        this.weight = weight.getWeight();
        this.water = weight.getWater();
        this.date = weight.getDate();
    }
}
