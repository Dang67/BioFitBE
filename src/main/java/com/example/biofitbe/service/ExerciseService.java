package com.example.biofitbe.service;

import com.example.biofitbe.dto.ExerciseDTO;
import com.example.biofitbe.dto.ExerciseDetailDTO;
import com.example.biofitbe.model.Exercise;
import com.example.biofitbe.model.ExerciseDetail;
import com.example.biofitbe.model.User;
import com.example.biofitbe.repository.ExerciseDetailRepository;
import com.example.biofitbe.repository.ExerciseRepository;
import com.example.biofitbe.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ExerciseService {
    @Autowired
    private ExerciseRepository exerciseRepository;

    @Autowired
    private ExerciseDetailRepository exerciseDetailRepository;

    private final UserRepository userRepository;

    public List<ExerciseDTO> getExercisesByUserId(Long userId) {
        List<Exercise> exercises = exerciseRepository.findByUserUserIdOrderByExerciseNameAsc(userId);
        return exercises.stream().map(ExerciseDTO::new).collect(Collectors.toList());
    }

    public ExerciseDTO getExerciseByGoalAndIntensity(Long exerciseId, Integer exerciseGoal, Integer intensity) {
        Exercise exercise = exerciseRepository.findExerciseWithDetailsByGoalAndIntensity(exerciseId, exerciseGoal, intensity)
                .orElseThrow(() -> new RuntimeException("Exercise not found or no matching details"));

        return new ExerciseDTO(exercise);
    }

    @Transactional
    public Optional<ExerciseDTO> createExercise(ExerciseDTO exerciseDTO) {
        // Kiểm tra xem bài tập đã tồn tại chưa
        if (exerciseRepository.findByUserUserIdAndExerciseName(exerciseDTO.getUserId(), exerciseDTO.getExerciseName()).isPresent()) {
            return Optional.empty();
        }

        // Tạo mới Exercise
        Exercise exercise = new Exercise();
        Optional<User> userOpt = userRepository.findById(exerciseDTO.getUserId());
        if (userOpt.isEmpty()) {
            return Optional.empty();
        }
        User user = userOpt.get();
        exercise.setUser(user); // Gán user
        exercise.setExerciseName(exerciseDTO.getExerciseName());

        // Lấy bài tập chi tiết đầu tiên
        if (exerciseDTO.getDetailList().isEmpty()) {
            return Optional.empty(); // Nếu không có chi tiết nào, trả về lỗi
        }

        ExerciseDetailDTO baseDetail = exerciseDTO.getDetailList().get(0);

        // ✅ Tạo danh sách chi tiết dựa trên công thức nhân hệ số
        List<ExerciseDetail> exerciseDetails = generateExerciseDetails(baseDetail, exercise);

        // Lưu Exercise vào database
        exercise = exerciseRepository.save(exercise);

        // Gán Exercise vào từng ExerciseDetail rồi lưu vào database
        for (ExerciseDetail detail : exerciseDetails) {
            detail.setExercise(exercise);
            exerciseDetailRepository.save(detail);
        }

        // Chuyển đổi lại Exercise thành DTO để trả về
        ExerciseDTO createdExerciseDTO = new ExerciseDTO(exercise, exerciseDetails);
        return Optional.of(createdExerciseDTO);
    }

    /**
     * ✅ Hàm tự động sinh danh sách ExerciseDetail dựa trên detail đầu tiên
     */
    private List<ExerciseDetail> generateExerciseDetails(ExerciseDetailDTO baseDetail, Exercise exercise) {
        List<ExerciseDetail> details = new ArrayList<>();

        // Hệ số nhân để tính toán `time` và `burnedCalories`
        float[][] factors = {
                {1.0f, 1.0f},  // Goal 0, Intensity 0 (giữ nguyên)
                {2.0f, 2.0f},  // Goal 0, Intensity 1
                {3.0f, 3.0f},  // Goal 0, Intensity 2
                {2.0f, 3.0f},  // Goal 1, Intensity 0
                {3.0f, 4.5f},  // Goal 1, Intensity 1
                {4.0f, 6.0f}   // Goal 1, Intensity 2
        };

        for (int goal = 0; goal <= 1; goal++) {
            for (int intensity = 0; intensity <= 2; intensity++) {
                float timeFactor = factors[goal * 3 + intensity][0];
                float calFactor = factors[goal * 3 + intensity][1];

                ExerciseDetail detail = new ExerciseDetail();
                detail.setExerciseGoal(goal);
                detail.setIntensity(intensity);
                detail.setTime(baseDetail.getTime() * timeFactor);
                detail.setBurnedCalories(baseDetail.getBurnedCalories() * calFactor);
                detail.setExercise(exercise);

                details.add(detail);
            }
        }

        return details;
    }

    public void deleteExercise(Long exerciseId) {
        Exercise exercise = exerciseRepository.findById(exerciseId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Exercise not found with ID: " + exerciseId));

        exerciseRepository.delete(exercise);
    }

    @Transactional
    public Optional<ExerciseDTO> updateExercise(Long exerciseId, ExerciseDTO updatedExerciseDTO) {
        Exercise exercise = exerciseRepository.findById(exerciseId)
                .orElseThrow(() -> new RuntimeException("Exercise not found"));

        // Cập nhật tên bài tập
        exercise.setExerciseName(updatedExerciseDTO.getExerciseName());

        // Tìm `ExerciseDetail` có `exerciseGoal = 0` và `intensity = 0`
        Optional<ExerciseDetail> baseDetailOpt = exerciseDetailRepository.findByExerciseAndExerciseGoalAndIntensity(exercise, 0, 0);

        if (baseDetailOpt.isEmpty()) {
            return Optional.empty(); // Không tìm thấy bài tập gốc
        }

        ExerciseDetail baseDetail = baseDetailOpt.get();

        // Cập nhật `time` và `burnedCalories` của bài tập gốc
        baseDetail.setTime(updatedExerciseDTO.getDetailList().get(0).getTime());
        baseDetail.setBurnedCalories(updatedExerciseDTO.getDetailList().get(0).getBurnedCalories());

        // Lưu lại bài tập gốc vào database
        exerciseDetailRepository.save(baseDetail);

        // ✅ Cập nhật lại tất cả `ExerciseDetail` dựa trên bài tập gốc bằng công thức nhân hệ số
        List<ExerciseDetail> updatedDetails = generateExerciseDetails(new ExerciseDetailDTO(baseDetail), exercise);

        // Xóa các `ExerciseDetail` cũ (trừ bài tập gốc)
        exerciseDetailRepository.deleteByExerciseAndNotBaseDetail(exercise, 0, 0);

        // ✅ Lưu lại toàn bộ danh sách mới (không lưu trùng bài tập gốc)
        for (ExerciseDetail detail : updatedDetails) {
            if (!(detail.getExerciseGoal() == 0 && detail.getIntensity() == 0)) {
                exerciseDetailRepository.save(detail);
            }
        }

        return Optional.of(new ExerciseDTO(exercise, updatedDetails));
    }

}
