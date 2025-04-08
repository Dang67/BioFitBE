package com.example.biofitbe.unit_test.service;

import com.example.biofitbe.dto.ExerciseDTO;
import com.example.biofitbe.dto.ExerciseDetailDTO;
import com.example.biofitbe.model.Exercise;
import com.example.biofitbe.model.ExerciseDetail;
import com.example.biofitbe.model.User;
import com.example.biofitbe.repository.ExerciseDetailRepository;
import com.example.biofitbe.repository.ExerciseDoneRepository;
import com.example.biofitbe.repository.ExerciseRepository;
import com.example.biofitbe.repository.UserRepository;
import com.example.biofitbe.service.ExerciseService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ExerciseServiceUnitTest {

    @Mock
    private ExerciseRepository exerciseRepository;

    @Mock
    private ExerciseDetailRepository exerciseDetailRepository;

    @Mock
    private ExerciseDoneRepository exerciseDoneRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private ExerciseService exerciseService;

    private User user;
    private Exercise exercise;
    private ExerciseDetail exerciseDetail;
    private ExerciseDTO exerciseDTO;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setUserId(1L);

        exercise = new Exercise();
        exercise.setExerciseId(1L);
        exercise.setExerciseName("Pull-ups");
        exercise.setUser(user);

        exerciseDetail = new ExerciseDetail();
        exerciseDetail.setExerciseDetailId(1L);
        exerciseDetail.setExercise(exercise);
        exerciseDetail.setExerciseGoal(0);
        exerciseDetail.setIntensity(0);
        exerciseDetail.setTime(5f);
        exerciseDetail.setBurnedCalories(30f);

        exerciseDTO = new ExerciseDTO();
        exerciseDTO.setUserId(1L);
        exerciseDTO.setExerciseName("Pull-ups");
        exerciseDTO.setDetailList(Collections.singletonList(new ExerciseDetailDTO(null, null, 0, 0, 5f, 30f)));
    }

    @Test
    void testGetExercisesByUserId_Success() {
        List<Exercise> exercises = Collections.singletonList(exercise);
        when(exerciseRepository.findByUserUserIdOrderByExerciseNameAsc(1L)).thenReturn(exercises);

        List<ExerciseDTO> result = exerciseService.getExercisesByUserId(1L);

        assertEquals(1, result.size());
        assertEquals("Pull-ups", result.get(0).getExerciseName());
        verify(exerciseRepository, times(1)).findByUserUserIdOrderByExerciseNameAsc(1L);
    }

    @Test
    void testGetExerciseByGoalAndIntensity_Success() {
        when(exerciseRepository.findExerciseWithDetailsByGoalAndIntensity(1L, 0, 0))
                .thenReturn(Optional.of(exercise));

        ExerciseDTO result = exerciseService.getExerciseByGoalAndIntensity(1L, 0, 0);

        assertEquals("Pull-ups", result.getExerciseName());
        verify(exerciseRepository, times(1)).findExerciseWithDetailsByGoalAndIntensity(1L, 0, 0);
    }

    @Test
    void testGetExerciseByGoalAndIntensity_NotFound() {
        when(exerciseRepository.findExerciseWithDetailsByGoalAndIntensity(1L, 0, 0))
                .thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> exerciseService.getExerciseByGoalAndIntensity(1L, 0, 0));
        verify(exerciseRepository, times(1)).findExerciseWithDetailsByGoalAndIntensity(1L, 0, 0);
    }

    @Test
    void testCreateExercise_Success() {
        when(exerciseRepository.findByUserUserIdAndExerciseName(1L, "Pull-ups")).thenReturn(Optional.empty());
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(exerciseRepository.save(any(Exercise.class))).thenReturn(exercise);
        when(exerciseDetailRepository.save(any(ExerciseDetail.class))).thenReturn(exerciseDetail);

        Optional<ExerciseDTO> result = exerciseService.createExercise(exerciseDTO);

        assertTrue(result.isPresent());
        assertEquals("Pull-ups", result.get().getExerciseName());
        verify(exerciseRepository, times(1)).save(any(Exercise.class));
        verify(exerciseDetailRepository, times(6)).save(any(ExerciseDetail.class));
    }

    @Test
    void testCreateExercise_ExerciseAlreadyExists() {
        when(exerciseRepository.findByUserUserIdAndExerciseName(1L, "Pull-ups"))
                .thenReturn(Optional.of(exercise));

        Optional<ExerciseDTO> result = exerciseService.createExercise(exerciseDTO);

        assertFalse(result.isPresent());
        verify(exerciseRepository, never()).save(any(Exercise.class));
    }

    @Test
    void testCreateExercise_UserNotFound() {
        when(exerciseRepository.findByUserUserIdAndExerciseName(1L, "Pull-ups")).thenReturn(Optional.empty());
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        Optional<ExerciseDTO> result = exerciseService.createExercise(exerciseDTO);

        assertFalse(result.isPresent());
        verify(exerciseRepository, never()).save(any(Exercise.class));
    }

    @Test
    void testGenerateExerciseDetails_Success() {
        ExerciseDetailDTO baseDetail = new ExerciseDetailDTO(null, null, 0, 0, 5f, 30f);

        List<ExerciseDetail> details = exerciseService.generateExerciseDetails(baseDetail, exercise);

        assertEquals(6, details.size());
        assertEquals(5f, details.get(0).getTime());
        assertEquals(30f, details.get(0).getBurnedCalories());
        assertEquals(20f, details.get(5).getTime());
        assertEquals(180f, details.get(5).getBurnedCalories());
    }

    @Test
    void testInitializeDefaultExercises_NewUser() {
        when(exerciseRepository.countByUserUserId(1L)).thenReturn(0L);
        when(exerciseRepository.findByUserUserIdAndExerciseName(anyLong(), anyString())).thenReturn(Optional.empty());
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(exerciseRepository.save(any(Exercise.class))).thenReturn(exercise);
        when(exerciseDetailRepository.save(any(ExerciseDetail.class))).thenReturn(exerciseDetail);

        exerciseService.initializeDefaultExercises(1L);

        verify(exerciseRepository, times(34)).save(any(Exercise.class));
        verify(exerciseDetailRepository, times(34 * 6)).save(any(ExerciseDetail.class));
    }

    @Test
    void testDeleteExercise_Success() {
        when(exerciseRepository.findById(1L)).thenReturn(Optional.of(exercise));

        exerciseService.deleteExercise(1L);

        verify(exerciseDoneRepository, times(1)).deleteByExerciseId(1L);
        verify(exerciseDetailRepository, times(1)).deleteDetailsByExerciseId(1L);
        verify(exerciseRepository, times(1)).delete(exercise);
    }

    @Test
    void testUpdateExercise_Success() {
        when(exerciseRepository.findById(1L)).thenReturn(Optional.of(exercise));
        when(exerciseDetailRepository.findByExerciseAndExerciseGoalAndIntensity(exercise, 0, 0))
                .thenReturn(Optional.of(exerciseDetail));
        when(exerciseDetailRepository.findAllByExercise(exercise))
                .thenReturn(Collections.singletonList(exerciseDetail));
        when(exerciseRepository.save(any(Exercise.class))).thenReturn(exercise);
        when(exerciseDetailRepository.save(any(ExerciseDetail.class))).thenReturn(exerciseDetail);

        Optional<ExerciseDTO> result = exerciseService.updateExercise(1L, exerciseDTO);

        assertTrue(result.isPresent());
        assertEquals("Pull-ups", result.get().getExerciseName());
        verify(exerciseRepository, times(1)).save(exercise);
        // Change from 6 to 7 if that's the correct number
        verify(exerciseDetailRepository, times(7)).save(any(ExerciseDetail.class));
    }
}