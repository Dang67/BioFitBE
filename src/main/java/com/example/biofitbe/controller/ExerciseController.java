package com.example.biofitbe.controller;

import com.example.biofitbe.dto.ExerciseDTO;
import com.example.biofitbe.service.ExerciseService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/exercise")
@RequiredArgsConstructor
public class ExerciseController {
    private final ExerciseService exerciseService;

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<ExerciseDTO>> getExercises(@PathVariable Long userId) {
        List<ExerciseDTO> exercises = exerciseService.getExercisesByUserId(userId);
        return ResponseEntity.ok(exercises);
    }

    @GetMapping("/{exerciseId}/details")
    public ResponseEntity<ExerciseDTO> getExerciseByGoalAndIntensity(
            @PathVariable Long exerciseId,
            @RequestParam Integer exerciseGoal,
            @RequestParam Integer intensity) {
        return ResponseEntity.ok(exerciseService.getExerciseByGoalAndIntensity(exerciseId, exerciseGoal, intensity));
    }

    @PostMapping("/create")
    public ResponseEntity<?> createExercise(@RequestBody ExerciseDTO exerciseDTO) {
        Optional<ExerciseDTO> createdExercise = exerciseService.createExercise(exerciseDTO);

        if (createdExercise.isPresent()) {
            return ResponseEntity.ok(createdExercise.get()); // Trả về ExerciseDTO khi tạo thành công
        } else {
            return ResponseEntity.badRequest().body("Exercise already exists!"); // Trả về lỗi nếu bài tập đã tồn tại
        }
    }

    @DeleteMapping("/{exerciseId}")
    public ResponseEntity<?> deleteExercise(@PathVariable Long exerciseId) {
        try {
            exerciseService.deleteExercise(exerciseId);
            return ResponseEntity.ok().body("Exercise deleted successfully");
        } catch (ResponseStatusException e) {
            return ResponseEntity.status(e.getStatusCode()).body(e.getReason());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error deleting exercise");
        }
    }

    @PutMapping("/{exerciseId}")
    public ResponseEntity<ExerciseDTO> updateExercise(
            @PathVariable Long exerciseId,
            @RequestBody ExerciseDTO updatedExerciseDTO) {

        Optional<ExerciseDTO> updatedExercise = exerciseService.updateExercise(exerciseId, updatedExerciseDTO);

        return updatedExercise
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }
}
