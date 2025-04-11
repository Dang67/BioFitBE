package com.example.biofitbe.service;

import com.example.biofitbe.dto.FoodDTO;
import com.example.biofitbe.model.Food;
import com.example.biofitbe.model.User;
import com.example.biofitbe.repository.FoodRepository;
import com.example.biofitbe.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class FoodService {
    @Autowired
    private FoodRepository foodRepository;

    @Autowired
    private UserRepository userRepository;

    // Lấy danh sách Food theo userId
    public List<FoodDTO> getFoodsByUserId(Long userId) {
        List<Food> foods = foodRepository.findByUserUserIdOrderByFoodNameAsc(userId);
        return foods.stream().map(FoodDTO::new).collect(Collectors.toList());
    }

    // Lấy thông tin chi tiết của Food
    public FoodDTO getFoodByIdWithDetails(Long foodId) {
        Food food = foodRepository.findById(foodId)
                .orElseThrow(() -> new RuntimeException("Food not found"));
        return new FoodDTO(food); // ✅ Trả về FoodDTO mà không cần chi tiết riêng nữa
    }

    @Transactional
    public Optional<FoodDTO> createFood(FoodDTO foodDTO) {
        // Kiểm tra xem thực phẩm đã tồn tại chưa
        if (foodRepository.findByUserUserIdAndFoodName(foodDTO.getUserId(), foodDTO.getFoodName()).isPresent()) {
            return Optional.empty();
        }

        // Tìm kiếm User
        Optional<User> userOpt = userRepository.findById(foodDTO.getUserId());
        if (userOpt.isEmpty()) {
            return Optional.empty();
        }
        User user = userOpt.get();

        // Tạo mới Food
        Food food = Food.builder()
                .user(user)
                .foodName(foodDTO.getFoodName())
                .session(foodDTO.getSession())
                .date(foodDTO.getDate())
                .foodImage(foodDTO.getFoodImage()) // Sử dụng trực tiếp byte[], có thể là null
                .servingSize(foodDTO.getServingSize())
                .servingSizeUnit(foodDTO.getServingSizeUnit())
                .mass(foodDTO.getMass())
                .calories(foodDTO.getCalories())
                .protein(foodDTO.getProtein())
                .carbohydrate(foodDTO.getCarbohydrate())
                .fat(foodDTO.getFat())
                .sodium(foodDTO.getSodium())
                .build();

        // Lưu Food vào cơ sở dữ liệu
        food = foodRepository.save(food);

        // Trả về FoodDTO
        FoodDTO createdFoodDTO = new FoodDTO(food);
        return Optional.of(createdFoodDTO);
    }


    public void deleteFood(Long foodId) {
        Food food = foodRepository.findById(foodId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Food not found with ID: " + foodId));
        foodRepository.delete(food);
    }

    @Transactional
    public Optional<FoodDTO> updateFood(FoodDTO foodDTO) {
        Optional<Food> foodOpt = foodRepository.findById(foodDTO.getFoodId());
        if (foodOpt.isEmpty()) {
            return Optional.empty();
        }
        Food food = foodOpt.get();

        // Cập nhật các trường
        food.setFoodName(foodDTO.getFoodName());
        food.setSession(foodDTO.getSession());
        food.setDate(foodDTO.getDate());
        if (foodDTO.getFoodImage() != null) {
            food.setFoodImage(foodDTO.getFoodImage());
        }
        food.setServingSize(foodDTO.getServingSize());
        food.setServingSizeUnit(foodDTO.getServingSizeUnit());
        food.setMass(foodDTO.getMass());
        food.setCalories(foodDTO.getCalories());
        food.setProtein(foodDTO.getProtein());
        food.setCarbohydrate(foodDTO.getCarbohydrate());
        food.setFat(foodDTO.getFat());
        food.setSodium(foodDTO.getSodium());

        foodRepository.save(food);
        return Optional.of(new FoodDTO(food));
    }
}
