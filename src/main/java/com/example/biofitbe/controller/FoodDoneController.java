package com.example.biofitbe.controller;

import com.example.biofitbe.dto.FoodDoneDTO;
import com.example.biofitbe.service.FoodDoneService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/food-done")
@RequiredArgsConstructor
public class FoodDoneController {
    private final FoodDoneService foodDoneService;

    @PostMapping("/create")
    public ResponseEntity<?> createFoodDone(@RequestBody FoodDoneDTO foodDoneDTO) {
        Optional<FoodDoneDTO> createdFoodDone = foodDoneService.createFoodDone(foodDoneDTO);
        if (createdFoodDone.isPresent()) {
            return ResponseEntity.ok(createdFoodDone.get()); // Trả về DTO khi tạo thành công
        } else {
            return ResponseEntity.badRequest().body("Food already exists!"); // Báo lỗi nếu đã tồn tại
        }
    }
}
