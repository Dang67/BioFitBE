package com.example.biofitbe.service;

import com.example.biofitbe.dto.FoodDoneDTO;
import com.example.biofitbe.model.Food;
import com.example.biofitbe.model.FoodDone;
import com.example.biofitbe.repository.FoodDoneRepository;
import com.example.biofitbe.repository.FoodRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FoodDoneService {

    @Autowired
    private FoodDoneRepository foodDoneRepository;
    @Autowired
    private FoodRepository foodRepository;

    String today = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

    public static String determineSession() {
        LocalTime now = LocalTime.now();

        /// Morning: 5:00 AM - 11:59 AM
        if (now.isAfter(LocalTime.of(5, 0)) && now.isBefore(LocalTime.of(12, 0))) {
            return "Morning"; // Morning
        }
        // Afternoon: 12:00 PM - 5:59 PM
        else if (now.isAfter(LocalTime.of(11, 59)) && now.isBefore(LocalTime.of(18, 0))) {
            return "Afternoon"; // Afternoon
        }
        // Evening: 6:00 PM - 4:59 AM
        else if (now.isAfter(LocalTime.of(17, 59)) || now.isBefore(LocalTime.of(5, 0))) {
            return "Evening"; // Evening
        }
        // Fallback (this should not be reached)
        return "Unknown";
    }

    @Transactional
    public Optional<FoodDoneDTO> createFoodDone(FoodDoneDTO foodDoneDTO) {
        // Tìm kiếm Food theo foodId
        Optional<Food> foodOpt = foodRepository.findById(foodDoneDTO.getFoodId());
        if (foodOpt.isEmpty()) {
            return Optional.empty();
        }
        Food food = foodOpt.get();
        // Lấy ngày hôm nay dưới dạng chuỗi yyyy-MM-dd
        String today = LocalDate.now().format(DateTimeFormatter.ISO_DATE);
        // Kiểm tra trùng food + ngày + session
        boolean exists = foodDoneRepository.existsByFoodAndDateAndSession(food, today, foodDoneDTO.getSession());
        if (exists) {
            return Optional.empty(); // Đã tồn tại
        }
        // Tạo mới FoodDone
        FoodDone foodDone = new FoodDone();
        foodDone.setFood(food);
        foodDone.setDate(today);
        foodDone.setSession(foodDoneDTO.getSession());

        // Lưu vào DB
        foodDone = foodDoneRepository.save(foodDone);

        // Trả về DTO
        return Optional.of(new FoodDoneDTO(foodDone));
    }
    // thành phần thức ăn
    public List<FoodDoneDTO> getFoodDoneByUserAndDate(Long userId, String date) {
        List<FoodDone> foodDones = foodDoneRepository.findByUserIdAndDate(userId, date);
        return foodDones.stream()
                .map(FoodDoneDTO::new)
                .collect(Collectors.toList());
    }
    @Transactional
    public boolean deleteFoodDoneById(Long id) {
        Optional<FoodDone> foodDoneOpt = foodDoneRepository.findById(id);
        if (foodDoneOpt.isPresent()) {
            foodDoneRepository.deleteById(id);
            return true;
        }
        return false;
    }

}
