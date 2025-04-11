package com.example.biofitbe.controller;

import com.example.biofitbe.dto.FoodDTO;
import com.example.biofitbe.service.FoodService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/food")
@RequiredArgsConstructor
public class FoodController {
    private final FoodService foodService;

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<FoodDTO>> getFoods(@PathVariable Long userId) {
        List<FoodDTO> foods = foodService.getFoodsByUserId(userId);
        return ResponseEntity.ok(foods);
    }

    @PostMapping(value = "/create", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> createFood(
            @RequestPart("food") FoodDTO foodDTO,
            @RequestPart(value = "image", required = false) MultipartFile image
    ) {
        // Nếu foodDTO không chứa userId hoặc foodName, thêm kiểm tra
        if (foodDTO.getUserId() == null || foodDTO.getFoodName() == null) {
            return ResponseEntity.badRequest().body("Missing required fields!");
        }

        // Xử lý hình ảnh nếu có
        if (image != null && !image.isEmpty()) {
            try {
                foodDTO.setFoodImage(image.getBytes());
            } catch (IOException e) {
                return ResponseEntity.badRequest().body("Failed to process image!");
            }
        } else {
            foodDTO.setFoodImage(null);
        }

        Optional<FoodDTO> createdFood = foodService.createFood(foodDTO);

        if (createdFood.isPresent()) {
            return ResponseEntity.ok(createdFood.get());
        } else {
            return ResponseEntity.badRequest().body("Food already exists or invalid user!");
        }
    }

    @DeleteMapping("/{foodId}")
    public ResponseEntity<?> deleteFood(@PathVariable Long foodId) {
        try {
            foodService.deleteFood(foodId); // Xử lý xóa cả foodDone trong service
            return ResponseEntity.ok("Food and related records deleted successfully");
        } catch (ResponseStatusException e) {
            return ResponseEntity.status(e.getStatusCode()).body(e.getReason());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Unexpected error while deleting food");
        }
    }

    @PutMapping(value = "/{foodId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> updateFood(
            @PathVariable("foodId") Long foodId,
            @RequestPart("food") FoodDTO foodDTO,
            @RequestPart(value = "image", required = false) MultipartFile image
    ) {
        foodDTO.setFoodId(foodId); // Đảm bảo foodId khớp với path variable
        if (image != null && !image.isEmpty()) {
            try {
                foodDTO.setFoodImage(image.getBytes());
            } catch (IOException e) {
                return ResponseEntity.badRequest().body("Failed to process image!");
            }
        }
        Optional<FoodDTO> updatedFood = foodService.updateFood(foodDTO);
        if (updatedFood.isPresent()) {
            return ResponseEntity.ok(updatedFood.get());
        } else {
            return ResponseEntity.badRequest().body("Failed to update food!");
        }
    }
}
