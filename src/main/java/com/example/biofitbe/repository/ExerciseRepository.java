package com.example.biofitbe.repository;

import com.example.biofitbe.model.Exercise;
import com.example.biofitbe.model.ExerciseDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ExerciseRepository extends JpaRepository<Exercise, Long> {
    Optional<Exercise> findByUserUserIdAndExerciseName(Long userId, String exerciseName);

    List<Exercise> findByUserUserIdOrderByExerciseNameAsc(Long userId);

    @Query("SELECT e FROM Exercise e " +
            "LEFT JOIN FETCH e.exerciseDetail d " +
            "WHERE e.exerciseId = :exerciseId " +
            "AND d.exerciseGoal = :exerciseGoal " +
            "AND d.intensity = :intensity")
    Optional<Exercise> findExerciseWithDetailsByGoalAndIntensity(
            @Param("exerciseId") Long exerciseId,
            @Param("exerciseGoal") Integer exerciseGoal,
            @Param("intensity") Integer intensity
    );

    long countByUserUserId(Long userId);
}
