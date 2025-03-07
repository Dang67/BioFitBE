package com.example.biofitbe.repository;

import com.example.biofitbe.model.DailyLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DailyLogRepository extends JpaRepository<DailyLog, Long> {
    Optional<DailyLog> findByUserUserIdAndDate(Long userId, String date);

    @Query("SELECT d FROM DailyLog d WHERE d.user.userId = :userId ORDER BY d.date DESC")
    List<DailyLog> findLatestWeightByUserId(@Param("userId") Long userId);

    @Query("SELECT d FROM DailyLog d WHERE d.user.userId = :userId ORDER BY d.date ASC")
    List<DailyLog> findAllByUserIdOrdered(@Param("userId") Long userId);
}