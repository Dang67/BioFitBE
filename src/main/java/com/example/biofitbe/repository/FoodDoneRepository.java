package com.example.biofitbe.repository;

import com.example.biofitbe.model.Food;
import com.example.biofitbe.model.FoodDone;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FoodDoneRepository extends JpaRepository<FoodDone, Long> {

    boolean existsByFoodAndDateAndSession(Food food, String date, String session);

    void deleteByFood(Food food);

    @Query("SELECT fd FROM FoodDone fd " +
            "WHERE fd.food.user.userId = :userId " +
            "AND fd.date = :date")
    List<FoodDone> findByUserIdAndDate(@Param("userId") Long userId,
                                       @Param("date") String date);


}
