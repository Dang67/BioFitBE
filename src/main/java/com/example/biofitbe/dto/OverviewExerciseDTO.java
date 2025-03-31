package com.example.biofitbe.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class OverviewExerciseDTO {
    private String exerciseName;
    private Integer level;
    private Integer intensity;
    private Float time;
    private Float burnedCalories;
    private String date;
    private Integer session;
}
