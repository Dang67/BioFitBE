package com.example.biofitbe.service;

import com.example.biofitbe.dto.DailyWeightDTO;
import com.example.biofitbe.model.DailyWeight;
import com.example.biofitbe.model.User;
import com.example.biofitbe.repository.DailyWeightRepository;
import com.example.biofitbe.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;

@Service
@RequiredArgsConstructor
public class DailyWeightService {
    private final DailyWeightRepository dailyWeightRepository;
    private final UserRepository userRepository;

    public Optional<DailyWeight> findDailyWeightByUserIdAndDate(Long userId, String date) {
        return dailyWeightRepository.findByUserUserIdAndDate(userId, date);
    }

    public DailyWeightDTO getLatestDailyWeightByUserId(Long userId) {
        List<DailyWeight> weights = dailyWeightRepository.findLatestWeightByUserId(userId);
        if (weights.isEmpty()) {
            throw new RuntimeException("No daily weight found");
        }

        DailyWeight weight = weights.get(0);
        return new DailyWeightDTO(
                weight.getDailyWeightId(),
                weight.getUser().getUserId(),
                weight.getWeight(),
                weight.getDate()
        );
    }

    public DailyWeight saveOrUpdateDailyWeight(Long userId, Float newWeight, String date) {
        Optional<DailyWeight> existingWeight = findDailyWeightByUserIdAndDate(userId, date);

        if (existingWeight.isPresent()) {
            // Nếu đã có bản ghi → cập nhật cân nặng
            DailyWeight weightToUpdate = existingWeight.get();
            weightToUpdate.setWeight(newWeight);
            return dailyWeightRepository.save(weightToUpdate);
        } else {
            // Nếu chưa có bản ghi → tạo mới
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            DailyWeight newDailyWeight = DailyWeight.builder()
                    .user(user)
                    .weight(newWeight)
                    .date(date)
                    .build();

            return dailyWeightRepository.save(newDailyWeight);
        }
    }

    public List<DailyWeightDTO> getWeightHistory(Long userId) {
        List<DailyWeight> weightList = dailyWeightRepository.findAllByUserIdOrdered(userId);
        return fillMissingWeightData(userId, weightList);
    }

    private List<DailyWeightDTO> fillMissingWeightData(Long userId, List<DailyWeight> weightList) {
        TreeMap<LocalDate, Float> weightMap = new TreeMap<>();
        Float lastKnownWeight = null;

        for (DailyWeight dw : weightList) {
            lastKnownWeight = dw.getWeight();
            weightMap.put(LocalDate.parse(dw.getDate()), lastKnownWeight);
        }

        if (lastKnownWeight == null) {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("User not found"));
            lastKnownWeight = user.getWeight();
        }

        LocalDate today = LocalDate.now();
        List<DailyWeightDTO> filledList = new ArrayList<>();

        for (int i = 6; i >= 0; i--) {
            LocalDate date = today.minusDays(i);

            // Tìm ngày gần nhất có dữ liệu
            Map.Entry<LocalDate, Float> entry = weightMap.floorEntry(date);
            if (entry != null) {
                lastKnownWeight = entry.getValue();
            }

            filledList.add(new DailyWeightDTO(null, userId, lastKnownWeight, date.toString()));
        }

        return filledList;
    }
}
