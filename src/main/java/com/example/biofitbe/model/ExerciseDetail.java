package com.example.biofitbe.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "exercise_detail")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExerciseDetail {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "exercise_detail_id",  nullable = false)
    private Long exerciseDetailId;

    @ManyToOne
    @JoinColumn(name = "exercise_id", nullable = false)
    @JsonBackReference
    private Exercise exercise;

    @Column(name = "exercise_goal",  nullable = false)
    private Integer exerciseGoal;

    @Column(name = "intensity", nullable = false)
    private Integer intensity;

    @Column(name = "time", nullable = false)
    private Float time;

    @Column(name = "burned_calories", nullable = false)
    private Float burnedCalories;
}
