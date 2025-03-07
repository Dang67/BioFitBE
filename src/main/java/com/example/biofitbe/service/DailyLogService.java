package com.example.biofitbe.service;

import com.example.biofitbe.dto.DailyLogDTO;
import com.example.biofitbe.model.DailyLog;
import com.example.biofitbe.model.User;
import com.example.biofitbe.repository.DailyLogRepository;
import com.example.biofitbe.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;

@Service
@RequiredArgsConstructor
public class DailyLogService {
    private final DailyLogRepository dailyLogRepository;
    private final UserRepository userRepository;

    public Optional<DailyLog> findDailyLogByUserIdAndDate(Long userId, String date) {
        return dailyLogRepository.findByUserUserIdAndDate(userId, date);
    }

    public DailyLogDTO getLatestDailyWeightByUserId(Long userId) {
        List<DailyLog> weights = dailyLogRepository.findLatestWeightByUserId(userId);
        if (weights.isEmpty()) {
            throw new RuntimeException("No daily weight found");
        }

        DailyLog weight = weights.get(0);
        return new DailyLogDTO(
                weight.getDailyWeightId(),
                weight.getUser().getUserId(),
                weight.getWeight(),
                weight.getWater(),
                weight.getDate()
        );
    }

    public DailyLog saveOrUpdateDailyWeight(Long userId, Float newWeight, Float newWater, String date) {
        Optional<DailyLog> existingLog = findDailyLogByUserIdAndDate(userId, date);

        if (existingLog.isPresent()) {
            // Nếu đã có bản ghi → cập nhật
            DailyLog logToUpdate = existingLog.get();
            logToUpdate.setWeight(newWeight);
            logToUpdate.setWater(newWater);
            return dailyLogRepository.save(logToUpdate);
        } else {
            System.out.println("🔍 Ngày nhận từ front-end: " + date);
            existingLog.ifPresent(log -> System.out.println("📌 Dữ liệu cũ: " + log.getDate() + ", Water: " + log.getWater()));
            System.out.println("Dữ liệu từ front-end: " + newWeight + ", Water: " + newWater);
            // Nếu chưa có bản ghi → tạo mới
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            DailyLog newDailyLog = DailyLog.builder()
                    .user(user)
                    .weight(newWeight)
                    .water(newWater)
                    .date(date)
                    .build();

            return dailyLogRepository.save(newDailyLog);
        }
    }

    public List<DailyLogDTO> getWeightHistory(Long userId) {
        List<DailyLog> weightList = dailyLogRepository.findAllByUserIdOrdered(userId);
        return fillMissingWeightData(userId, weightList);
    }

    private List<DailyLogDTO> fillMissingWeightData(Long userId, List<DailyLog> weightList) {
        TreeMap<LocalDate, Float> weightMap = new TreeMap<>();
        Float lastKnownWeight = null;

        for (DailyLog dw : weightList) {
            lastKnownWeight = dw.getWeight();
            weightMap.put(LocalDate.parse(dw.getDate()), lastKnownWeight);
        }

        if (lastKnownWeight == null) {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("User not found"));
            lastKnownWeight = user.getWeight();
        }

        LocalDate today = LocalDate.now();
        List<DailyLogDTO> filledList = new ArrayList<>();

        for (int i = 6; i >= 0; i--) {
            LocalDate date = today.minusDays(i);

            // Tìm ngày gần nhất có dữ liệu
            Map.Entry<LocalDate, Float> entry = weightMap.floorEntry(date);
            if (entry != null) {
                lastKnownWeight = entry.getValue();
            }

            filledList.add(new DailyLogDTO(null, userId, lastKnownWeight, null, date.toString()));
        }

        return filledList;
    }
}
