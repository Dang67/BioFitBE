package com.example.biofitbe.repository;

import com.example.biofitbe.model.Food;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FoodRepository extends JpaRepository<Food, Long> {

    // Các phương thức truy vấn cho Food
    Optional<Food> findByUserUserIdAndFoodName(Long userId, String foodName);
    List<Food> findByUserUserIdOrderByFoodNameAsc(Long userId);

    long countByUserUserId(Long userId);

    // Truy vấn lấy Food với tất cả thông tin liên quan
    @Query("SELECT f FROM Food f WHERE f.foodId = :foodId")
    Optional<Food> findById(@Param("foodId") Long foodId);
}
