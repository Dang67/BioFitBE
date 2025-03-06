package com.example.biofitbe.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateDailyWeightRequest {
    private Long dailyWeightId;
    private Float weight;
}
