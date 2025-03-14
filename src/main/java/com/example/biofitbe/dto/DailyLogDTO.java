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
    private Long dailyLogId;
    private Long userId;
    private Float weight;
    private Float water;
    private String date;

    // ✅ Constructor để chuyển từ entity DailyWeight sang DTO
    public DailyLogDTO(DailyLog dailyLog) {
        this.dailyLogId = dailyLog.getDailyLogId();
        this.userId = dailyLog.getUser().getUserId(); // Lấy ID của user
        this.weight = dailyLog.getWeight();
        this.water = dailyLog.getWater();
        this.date = dailyLog.getDate();
    }
}
