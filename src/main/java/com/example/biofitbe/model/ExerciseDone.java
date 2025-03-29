package com.example.biofitbe.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "exercise_done")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExerciseDone {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "exercise_done_id",  nullable = false)
    private Long exerciseDoneId;

    @ManyToOne
    @JoinColumn(name = "exercise_detail_id", nullable = false)
    @JsonBackReference
    private ExerciseDetail exerciseDetail;

    @Column(name = "date", nullable = false)
    private String date;

    @Column(name = "session", nullable = false)
    private Integer session;
}
