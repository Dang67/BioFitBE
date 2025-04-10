package com.example.biofitbe.controller;

import com.example.biofitbe.dto.FoodDTO;
import com.example.biofitbe.service.FoodService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

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

    @PostMapping("/create")
    public ResponseEntity<?> createFood(@RequestBody FoodDTO foodDTO) {
        Optional<FoodDTO> createdFood = foodService.createFood(foodDTO);

        if (createdFood.isPresent()) {
            return ResponseEntity.ok(createdFood.get());
        } else {
            return ResponseEntity.badRequest().body("Food already exists!");
        }
    }
    @DeleteMapping("/{foodId}")
    public ResponseEntity<?> deleteFood(@PathVariable Long foodId) {
        try {
            foodService.deleteFood(foodId);
            return ResponseEntity.ok().body("Food deleted successfully");
        } catch (ResponseStatusException e) {
            return ResponseEntity.status(e.getStatusCode()).body(e.getReason());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error deleting food");
        }
    }

    @PutMapping("/{foodId}")
    public ResponseEntity<FoodDTO> updateFood(
            @PathVariable Long foodId,
            @RequestBody FoodDTO updatedFoodDTO) {
        Optional<FoodDTO> updatedFood = foodService.updateFood(foodId, updatedFoodDTO);
        return updatedFood
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }
}
