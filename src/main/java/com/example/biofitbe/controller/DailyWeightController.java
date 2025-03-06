package com.example.biofitbe.controller;

import com.example.biofitbe.dto.DailyWeightDTO;
import com.example.biofitbe.model.DailyWeight;
import com.example.biofitbe.service.DailyWeightService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/daily-weight")
@RequiredArgsConstructor
public class DailyWeightController {
    private final DailyWeightService dailyWeightService;

    @GetMapping("/user/{userId}/latest")
    public ResponseEntity<DailyWeightDTO> getLatestDailyWeight(@PathVariable Long userId) {
        DailyWeightDTO weight = dailyWeightService.getLatestDailyWeightByUserId(userId);
        return ResponseEntity.ok(weight);
    }

    @GetMapping("/user/{userId}/date/{date}")
    public ResponseEntity<DailyWeightDTO> checkDailyWeight(
            @PathVariable Long userId,
            @PathVariable String date) {
        Optional<DailyWeight> existingWeight = dailyWeightService.findDailyWeightByUserIdAndDate(userId, date);
        return existingWeight.map(weight -> ResponseEntity.ok(new DailyWeightDTO(
                weight.getDailyWeightId(),
                weight.getUser().getUserId(),
                weight.getWeight(),
                weight.getDate()
        ))).orElse(ResponseEntity.noContent().build());
    }

    @PostMapping("/save-or-update")
    public ResponseEntity<DailyWeightDTO> saveOrUpdateDailyWeight(@RequestBody DailyWeightDTO request) {
        DailyWeight savedWeight = dailyWeightService.saveOrUpdateDailyWeight(request.getUserId(), request.getWeight(), request.getDate());
        return ResponseEntity.ok(new DailyWeightDTO(savedWeight));
    }

    @GetMapping("/user/{userId}/history")
    public ResponseEntity<List<DailyWeightDTO>> getWeightHistory(@PathVariable Long userId) {
        List<DailyWeightDTO> weightHistory = dailyWeightService.getWeightHistory(userId);
        return ResponseEntity.ok(weightHistory);
    }
}
