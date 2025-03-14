package com.example.biofitbe.dto;

import com.example.biofitbe.model.Exercise;
import com.example.biofitbe.model.ExerciseDetail;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ExerciseDetailDTO {
    private Long exerciseDetailId;
    private Long exerciseId;
    private Integer exerciseGoal;
    private Integer intensity;
    private Float time;
    private Float burnedCalories;

    public ExerciseDetailDTO(ExerciseDetail exerciseDetail) {
        this.exerciseDetailId = exerciseDetail.getExerciseDetailId();
        this.exerciseId = exerciseDetail.getExercise().getExerciseId();
        this.exerciseGoal = exerciseDetail.getExerciseGoal();
        this.intensity = exerciseDetail.getIntensity();
        this.time = exerciseDetail.getTime();
        this.burnedCalories = exerciseDetail.getBurnedCalories();
    }
}
