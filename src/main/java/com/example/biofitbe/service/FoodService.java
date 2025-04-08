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
import org.springframework.web.server.ResponseStatusException;

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
            return Optional.empty(); // Nếu đã có, trả về Optional rỗng
        }

        // Tìm kiếm User
        Optional<User> userOpt = userRepository.findById(foodDTO.getUserId());
        if (userOpt.isEmpty()) {
            return Optional.empty(); // Nếu không có User, trả về lỗi
        }
        User user = userOpt.get();

        // ✅ Tạo mới Food
        Food food = Food.builder()
                .user(user) // Gán user từ foodDTO
                .foodName(foodDTO.getFoodName()) // Gán tên thực phẩm từ foodDTO
                .session(foodDTO.getSession()) // Gán session từ foodDTO
                .date(foodDTO.getDate()) // Gán ngày từ foodDTO
                .foodImage(foodDTO.getFoodImage()) // Gán foodImage từ foodDTO
                .servingSize(foodDTO.getServingSize()) // Gán servingSizeValue từ foodDTO
                .servingSizeUnit(foodDTO.getServingSizeUnit()) // Gán servingSizeUnit từ foodDTO
                .mass(foodDTO.getMass()) // Gán mass từ foodDTO
                .calories(foodDTO.getCalories()) // Gán calories từ foodDTO
                .protein(foodDTO.getProtein()) // Gán proteinValue từ foodDTO
                .carbohydrate(foodDTO.getCarbohydrate()) // Gán carbohydrateValue từ foodDTO
                .fat(foodDTO.getFat()) // Gán fatValue từ foodDTO
                .sodium(foodDTO.getSodium()) // Gán sodium từ foodDTO (nếu có)
                .build();

        // Lưu Food vào cơ sở dữ liệu
        food = foodRepository.save(food);

        // Trả về FoodDTO sau khi lưu thành công
        FoodDTO createdFoodDTO = new FoodDTO(food);
        return Optional.of(createdFoodDTO);
    }


    public void deleteFood(Long foodId) {
        Food food = foodRepository.findById(foodId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Food not found with ID: " + foodId));
        foodRepository.delete(food);
    }

    @Transactional
    public Optional<FoodDTO> updateFood(Long foodId, FoodDTO updatedFoodDTO) {
        Food food = foodRepository.findById(foodId)
                .orElseThrow(() -> new RuntimeException("Food not found"));

        food.setFoodName(updatedFoodDTO.getFoodName());
        food.setSession(updatedFoodDTO.getSession());
        food.setDate(updatedFoodDTO.getDate());

        // Lưu lại Food đã cập nhật
        foodRepository.save(food);

        return Optional.of(new FoodDTO(food));
    }
}
