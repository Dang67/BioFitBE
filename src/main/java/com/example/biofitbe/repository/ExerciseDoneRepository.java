package com.example.biofitbe.repository;

import com.example.biofitbe.dto.OverviewExerciseDTO;
import com.example.biofitbe.model.ExerciseDone;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Repository
public interface ExerciseDoneRepository extends JpaRepository<ExerciseDone, Long> {
    @Query("SELECT ed FROM ExerciseDone ed " +
            "WHERE ed.exerciseDetail.exercise.user.userId = :userId " +
            "AND ed.date BETWEEN :startDate AND :endDate")
    List<ExerciseDone> findByUserIdAndDateRange(@Param("userId") Long userId,
                                                @Param("startDate") String startDate,
                                                @Param("endDate") String endDate);

    @Query("SELECT new com.example.biofitbe.dto.OverviewExerciseDTO(" +
            "e.exerciseName, ed.exerciseGoal, ed.intensity, ed.time, ed.burnedCalories, edone.date, edone.session) " +
            "FROM ExerciseDone edone " +
            "JOIN edone.exerciseDetail ed " +
            "JOIN ed.exercise e " +
            "WHERE e.user.userId = :userId " +
            "AND edone.date BETWEEN :startDate AND :endDate " +
            "ORDER BY edone.date DESC, edone.session DESC")
    List<OverviewExerciseDTO> findOverviewExercisesByUserAndDateRange(@Param("userId") Long userId,
                                                                      @Param("startDate") String startDate,
                                                                      @Param("endDate") String endDate);

    @Query("SELECT SUM(ed.burnedCalories) " +
            "FROM ExerciseDone edone " +
            "JOIN edone.exerciseDetail ed " +
            "JOIN ed.exercise e " +
            "WHERE e.user.userId = :userId " +
            "AND edone.date = :today")
    Float getTotalBurnedCaloriesToday(
            @Param("userId") Long userId,
            @Param("today") String today
    );

    @Query("SELECT SUM(ed.time) " +
            "FROM ExerciseDone edone " +
            "JOIN edone.exerciseDetail ed " +
            "JOIN ed.exercise e " +
            "WHERE e.user.userId = :userId " +
            "AND edone.date = :today")
    Float getTotalExerciseDoneTimeToday(
            @Param("userId") Long userId,
            @Param("today") String today
    );
}


