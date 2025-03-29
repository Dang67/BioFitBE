package com.example.biofitbe.repository;

import com.example.biofitbe.model.Exercise;
import com.example.biofitbe.model.ExerciseDetail;
import com.example.biofitbe.model.ExerciseDone;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ExerciseDetailRepository extends JpaRepository<ExerciseDetail, Long> {
    @Modifying
    @Query("DELETE FROM ExerciseDetail ed WHERE ed.exercise = :exercise AND NOT (ed.exerciseGoal = :baseGoal AND ed.intensity = :baseIntensity)")
    void deleteByExerciseAndNotBaseDetail(@Param("exercise") Exercise exercise, @Param("baseGoal") int baseGoal, @Param("baseIntensity") int baseIntensity);

    Optional<ExerciseDetail> findByExerciseAndExerciseGoalAndIntensity(Exercise exercise, int exerciseGoal, int intensity);

    Optional<ExerciseDetail> findExerciseDetailByExerciseDetailId(Long exerciseDetailId);
}
