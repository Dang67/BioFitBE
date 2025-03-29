package com.example.biofitbe.dto;

import com.example.biofitbe.model.ExerciseDone;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ExerciseDoneDTO {
    private Long exerciseDoneId;
    private Long exerciseDetailId;
    private String date;
    private Integer session;

    public ExerciseDoneDTO(ExerciseDone exerciseDone) {
        this.exerciseDoneId = exerciseDone.getExerciseDoneId();
        this.exerciseDetailId = exerciseDone.getExerciseDetail().getExerciseDetailId();
        this.date = exerciseDone.getDate();
        this.session = exerciseDone.getSession();
    }
}
