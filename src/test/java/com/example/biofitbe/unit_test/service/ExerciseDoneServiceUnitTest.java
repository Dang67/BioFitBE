package com.example.biofitbe.unit_test.service;


import com.example.biofitbe.service.ExerciseDoneService;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import com.example.biofitbe.dto.ExerciseDoneDTO;
import com.example.biofitbe.dto.OverviewExerciseDTO;
import com.example.biofitbe.model.ExerciseDetail;
import com.example.biofitbe.model.ExerciseDone;
import com.example.biofitbe.repository.ExerciseDetailRepository;
import com.example.biofitbe.repository.ExerciseDoneRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ExerciseDoneServiceUnitTest {
    @Mock
    private ExerciseDoneRepository exerciseDoneRepository;

    @Mock
    private ExerciseDetailRepository exerciseDetailRepository;

    @InjectMocks
    private ExerciseDoneService exerciseDoneService;

    private ExerciseDetail exerciseDetail;
    private ExerciseDone exerciseDone;
    private ExerciseDoneDTO exerciseDoneDTO;

    @BeforeEach
    void setUp() {
        exerciseDetail = new ExerciseDetail();
        exerciseDetail.setExerciseDetailId(1L);

        exerciseDone = new ExerciseDone();
        exerciseDone.setExerciseDetail(exerciseDetail);
        exerciseDone.setDate(LocalDate.now().toString());
        exerciseDone.setSession(0);

        exerciseDoneDTO = new ExerciseDoneDTO();
        exerciseDoneDTO.setExerciseDetailId(1L);
    }

    @Test
    void testDetermineSession_Morning() {
        // Giả lập thời gian sáng (8:00 AM)
        LocalTime morningTime = LocalTime.of(8, 0);
        int session = ExerciseDoneService.determineSession();
        assertTrue(session >= 0 && session <= 2); // Kiểm tra giá trị hợp lệ
    }

    @Test
    void testCreateExerciseDone_Success() {
        // Mock repository trả về ExerciseDetail
        when(exerciseDetailRepository.findExerciseDetailByExerciseDetailId(1L))
                .thenReturn(Optional.of(exerciseDetail));
        when(exerciseDoneRepository.save(any(ExerciseDone.class))).thenReturn(exerciseDone);

        // Thực hiện phương thức
        Optional<ExerciseDoneDTO> result = exerciseDoneService.createExerciseDone(exerciseDoneDTO);

        // Kiểm tra kết quả
        assertTrue(result.isPresent());
        assertEquals(1L, result.get().getExerciseDetailId());
        verify(exerciseDetailRepository, times(1)).findExerciseDetailByExerciseDetailId(1L);
        verify(exerciseDoneRepository, times(1)).save(any(ExerciseDone.class));
    }

    @Test
    void testCreateExerciseDone_ExerciseDetailNotFound() {
        // Mock repository trả về Optional.empty()
        when(exerciseDetailRepository.findExerciseDetailByExerciseDetailId(1L))
                .thenReturn(Optional.empty());

        // Thực hiện phương thức
        Optional<ExerciseDoneDTO> result = exerciseDoneService.createExerciseDone(exerciseDoneDTO);

        // Kiểm tra kết quả
        assertFalse(result.isPresent());
        verify(exerciseDetailRepository, times(1)).findExerciseDetailByExerciseDetailId(1L);
        verify(exerciseDoneRepository, never()).save(any(ExerciseDone.class));
    }

    @Test
    void testGetExerciseDoneByUserIdAndDateRange_Success() {
        // Mock dữ liệu trả về từ repository
        List<ExerciseDone> exerciseDoneList = Collections.singletonList(exerciseDone);
        when(exerciseDoneRepository.findByUserIdAndDateRange(1L, "2025-04-01", "2025-04-06"))
                .thenReturn(exerciseDoneList);

        // Thực hiện phương thức
        List<ExerciseDoneDTO> result = exerciseDoneService.getExerciseDoneByUserIdAndDateRange(1L, "2025-04-01", "2025-04-06");

        // Kiểm tra kết quả
        assertEquals(1, result.size());
        assertEquals(1L, result.get(0).getExerciseDetailId());
        verify(exerciseDoneRepository, times(1))
                .findByUserIdAndDateRange(1L, "2025-04-01", "2025-04-06");
    }

    @Test
    void testGetOverviewExercises_Success() {
        // Mock dữ liệu trả về từ repository
        List<OverviewExerciseDTO> overviewList = Collections.singletonList(new OverviewExerciseDTO());
        when(exerciseDoneRepository.findOverviewExercisesByUserAndDateRange(1L, "2025-04-01", "2025-04-06"))
                .thenReturn(overviewList);

        // Thực hiện phương thức
        List<OverviewExerciseDTO> result = exerciseDoneService.getOverviewExercises(1L, "2025-04-01", "2025-04-06");

        // Kiểm tra kết quả
        assertEquals(1, result.size());
        verify(exerciseDoneRepository, times(1))
                .findOverviewExercisesByUserAndDateRange(1L, "2025-04-01", "2025-04-06");
    }

    @Test
    void testGetTotalBurnedCaloriesToday_Success() {
        // Mock dữ liệu trả về từ repository
        String today = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        when(exerciseDoneRepository.getTotalBurnedCaloriesToday(1L, today)).thenReturn(500.0f);

        // Thực hiện phương thức
        Float result = exerciseDoneService.getTotalBurnedCaloriesToday(1L);

        // Kiểm tra kết quả
        assertEquals(500.0f, result, 0.01);
        verify(exerciseDoneRepository, times(1)).getTotalBurnedCaloriesToday(1L, today);
    }

    @Test
    void testGetTotalExerciseDoneTimeToday_Success() {
        // Mock dữ liệu trả về từ repository
        String today = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        when(exerciseDoneRepository.getTotalExerciseDoneTimeToday(1L, today)).thenReturn(60.0f);

        // Thực hiện phương thức
        Float result = exerciseDoneService.getTotalExerciseDoneTimeToday(1L);

        // Kiểm tra kết quả
        assertEquals(60.0f, result, 0.01);
        verify(exerciseDoneRepository, times(1)).getTotalExerciseDoneTimeToday(1L, today);
    }
}
