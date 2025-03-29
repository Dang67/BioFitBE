package com.example.biofitbe.controller;

import com.example.biofitbe.dto.ExerciseDoneDTO;
import com.example.biofitbe.dto.OverviewExerciseDTO;
import com.example.biofitbe.service.ExerciseDoneService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/exercise-done")
@RequiredArgsConstructor
public class ExerciseDoneController {
    private final ExerciseDoneService exerciseDoneService;

    @PostMapping("/create")
    public ResponseEntity<?> createExerciseDone(@RequestBody ExerciseDoneDTO exerciseDoneDTO) {
        Optional<ExerciseDoneDTO> createdExerciseDone = exerciseDoneService.createExerciseDone(exerciseDoneDTO);

        if (createdExerciseDone.isPresent()) {
            return ResponseEntity.ok(createdExerciseDone.get()); // Trả về ExerciseDoneDTO khi tạo thành công
        } else {
            return ResponseEntity.badRequest().body("Exercise already exists!"); // Trả về lỗi nếu bài tập đã tồn tại
        }
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<ExerciseDoneDTO>> getExerciseDoneByUserAndDateRange(
            @PathVariable Long userId,
            @RequestParam String startDate,
            @RequestParam String endDate) {
        List<ExerciseDoneDTO> exerciseDoneDTOList = exerciseDoneService.getExerciseDoneByUserIdAndDateRange(userId, startDate, endDate);
        return ResponseEntity.ok(exerciseDoneDTOList);
    }

    @GetMapping("/overview")
    public ResponseEntity<List<OverviewExerciseDTO>> getOverviewExercises(
            @RequestParam Long userId,
            @RequestParam String startDate,
            @RequestParam String endDate) {
        List<OverviewExerciseDTO> overviewList = exerciseDoneService.getOverviewExercises(userId, startDate, endDate);
        return ResponseEntity.ok(overviewList);
    }

    @GetMapping("/burned-calories/today")
    public ResponseEntity<Double> getTotalBurnedCaloriesToday(@RequestParam Long userId) {
        Float totalBurnedCalories = exerciseDoneService.getTotalBurnedCaloriesToday(userId);
        return ResponseEntity.ok(totalBurnedCalories != null ? totalBurnedCalories : 0.0);
    }
}
