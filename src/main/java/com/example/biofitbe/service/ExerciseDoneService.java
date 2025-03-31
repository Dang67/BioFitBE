package com.example.biofitbe.service;


import com.example.biofitbe.dto.ExerciseDoneDTO;
import com.example.biofitbe.dto.OverviewExerciseDTO;
import com.example.biofitbe.model.ExerciseDetail;
import com.example.biofitbe.model.ExerciseDone;
import com.example.biofitbe.repository.ExerciseDetailRepository;
import com.example.biofitbe.repository.ExerciseDoneRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ExerciseDoneService {
    @Autowired
    private ExerciseDoneRepository exerciseDoneRepository;

    String today = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
    @Autowired
    private ExerciseDetailRepository exerciseDetailRepository;

    public static int determineSession() {
        LocalTime now = LocalTime.now();

        // Morning: 5:00 AM to 11:59 AM
        if (now.isAfter(LocalTime.of(5, 0)) && now.isBefore(LocalTime.of(12, 0))) {
            return 0; // Morning
        }
        // Afternoon: 12:00 PM to 5:59 PM
        else if (now.isAfter(LocalTime.of(11, 59)) && now.isBefore(LocalTime.of(18, 0))) {
            return 1; // Afternoon
        }
        // Evening: 6:00 PM to 4:59 AM
        else if (now.isAfter(LocalTime.of(17, 59)) || now.isBefore(LocalTime.of(5, 0))) {
            return 2; // Evening
        }

        // Fallback (though this should never be reached with the above conditions)
        return -1;
    }

    @Transactional
    public Optional<ExerciseDoneDTO> createExerciseDone(ExerciseDoneDTO exerciseDoneDTO) {
        // Lấy thông tin ExerciseDetail theo exerciseDetailId
        Optional<ExerciseDetail> exerciseDetailOpt = exerciseDetailRepository
                .findExerciseDetailByExerciseDetailId(exerciseDoneDTO.getExerciseDetailId());

        if (exerciseDetailOpt.isEmpty()) {
            return Optional.empty();
        }
        ExerciseDetail exerciseDetail = exerciseDetailOpt.get();

        // Tạo mới ExerciseDone và gán các thuộc tính
        ExerciseDone exerciseDone = new ExerciseDone();
        exerciseDone.setExerciseDetail(exerciseDetail);
        exerciseDone.setDate(today);
        exerciseDone.setSession(determineSession());

        // Lưu ExerciseDone vào database
        exerciseDone = exerciseDoneRepository.save(exerciseDone);

        // Chuyển đổi sang DTO để trả về
        ExerciseDoneDTO createdExerciseDoneDTO = new ExerciseDoneDTO(exerciseDone);
        return Optional.of(createdExerciseDoneDTO);
    }

    public List<ExerciseDoneDTO> getExerciseDoneByUserIdAndDateRange(Long userId, String startDate, String endDate) {
        List<ExerciseDone> exerciseDoneList = exerciseDoneRepository.findByUserIdAndDateRange(userId, startDate, endDate);
        return exerciseDoneList.stream().map(ExerciseDoneDTO::new).collect(Collectors.toList());
    }

    public List<OverviewExerciseDTO> getOverviewExercises(Long userId, String startDate, String endDate) {
        return exerciseDoneRepository.findOverviewExercisesByUserAndDateRange(userId, startDate, endDate);
    }

    public Float getTotalBurnedCaloriesToday(Long userId) {
        String today = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        return exerciseDoneRepository.getTotalBurnedCaloriesToday(userId, today);
    }

    public Float getTotalExerciseDoneTimeToday(Long userId) {
        String today = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        return exerciseDoneRepository.getTotalExerciseDoneTimeToday(userId, today);
    }
}
