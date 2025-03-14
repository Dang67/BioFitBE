package com.example.biofitbe.controller;

import com.example.biofitbe.dto.DailyLogDTO;
import com.example.biofitbe.model.DailyLog;
import com.example.biofitbe.service.DailyLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/daily-log")
@RequiredArgsConstructor
public class DailyLogController {
    private final DailyLogService dailyLogService;

    @GetMapping("/user/{userId}/latest")
    public ResponseEntity<DailyLogDTO> getLatestDailyWeight(@PathVariable Long userId) {
        DailyLogDTO weight = dailyLogService.getLatestDailyWeightByUserId(userId);
        return ResponseEntity.ok(weight);
    }

    @GetMapping("/user/{userId}/date/{date}")
    public ResponseEntity<DailyLogDTO> checkDailyWeight(
            @PathVariable Long userId,
            @PathVariable String date) {
        Optional<DailyLog> existingWeight = dailyLogService.findDailyLogByUserIdAndDate(userId, date);
        return existingWeight.map(weight -> ResponseEntity.ok(new DailyLogDTO(
                weight.getDailyLogId(),
                weight.getUser().getUserId(),
                weight.getWeight(),
                weight.getWater(),
                weight.getDate()
        ))).orElse(ResponseEntity.noContent().build());
    }

    @PostMapping("/save-or-update")
    public ResponseEntity<DailyLogDTO> saveOrUpdateDailyWeight(@RequestBody DailyLogDTO request) {
        DailyLog savedWeight = dailyLogService.saveOrUpdateDailyWeight(request.getUserId(), request.getWeight(), request.getWater(), request.getDate());
        return ResponseEntity.ok(new DailyLogDTO(savedWeight));
    }

    @GetMapping("/user/{userId}/history")
    public ResponseEntity<List<DailyLogDTO>> getWeightHistory(@PathVariable Long userId) {
        List<DailyLogDTO> weightHistory = dailyLogService.getWeightHistory(userId);
        return ResponseEntity.ok(weightHistory);
    }
}
