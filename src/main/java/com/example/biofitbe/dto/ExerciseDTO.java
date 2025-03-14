package com.example.biofitbe.dto;

import com.example.biofitbe.model.Exercise;
import com.example.biofitbe.model.ExerciseDetail;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ExerciseDTO {
    private Long exerciseId;
    private Long userId;
    private String exerciseName;
    private List<ExerciseDetailDTO> detailList;

    public ExerciseDTO(Exercise exercise) {
        this.exerciseId = exercise.getExerciseId();
        this.userId = exercise.getUser().getUserId();
        this.exerciseName = exercise.getExerciseName();
        this.detailList = exercise.getExerciseDetail().stream()
                .map(ExerciseDetailDTO::new)
                .collect(Collectors.toList());
    }

    public ExerciseDTO(Exercise exercise, List<ExerciseDetail> exerciseDetails) {
        this.exerciseId = exercise.getExerciseId();
        this.userId = exercise.getUser().getUserId();
        this.exerciseName = exercise.getExerciseName();
        this.detailList = exerciseDetails.stream()
                .map(ExerciseDetailDTO::new)
                .collect(Collectors.toList());
    }
}
