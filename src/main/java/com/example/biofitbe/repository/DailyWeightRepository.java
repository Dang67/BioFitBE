package com.example.biofitbe.repository;

import com.example.biofitbe.model.DailyWeight;
import com.example.biofitbe.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DailyWeightRepository extends JpaRepository<DailyWeight, Long> {
    Optional<DailyWeight> findByUserUserIdAndDate(Long userId, String date);

    @Query("SELECT d FROM DailyWeight d WHERE d.user.userId = :userId ORDER BY d.date DESC")
    List<DailyWeight> findLatestWeightByUserId(@Param("userId") Long userId);

    @Query("SELECT d FROM DailyWeight d WHERE d.user.userId = :userId ORDER BY d.date ASC")
    List<DailyWeight> findAllByUserIdOrdered(@Param("userId") Long userId);
}