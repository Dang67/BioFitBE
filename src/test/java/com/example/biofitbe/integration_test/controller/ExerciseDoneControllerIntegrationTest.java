package com.example.biofitbe.integration_test.controller;

import com.example.biofitbe.dto.ExerciseDoneDTO;
import com.example.biofitbe.dto.OverviewExerciseDTO;
import com.example.biofitbe.model.ExerciseDetail;
import com.example.biofitbe.model.ExerciseDone;
import com.example.biofitbe.service.ExerciseDoneService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class ExerciseDoneControllerIntegrationTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private ExerciseDoneService exerciseDoneService;

    private ExerciseDetail createExerciseDetail(Long id) {
        ExerciseDetail detail = new ExerciseDetail();
        detail.setExerciseDetailId(id);
        return detail;
    }

    private ExerciseDone createExerciseDone(Long id, Long detailId, String date, Integer session) {
        ExerciseDone exerciseDone = new ExerciseDone();
        exerciseDone.setExerciseDoneId(id);
        exerciseDone.setExerciseDetail(createExerciseDetail(detailId));
        exerciseDone.setDate(date);
        exerciseDone.setSession(session);
        return exerciseDone;
    }

    private OverviewExerciseDTO createOverviewExerciseDTO(String name, Integer level, Integer intensity, Float time, Float burnedCalories, String date, Integer session) {
        OverviewExerciseDTO overviewExerciseDTO = new OverviewExerciseDTO();
        overviewExerciseDTO.setExerciseName(name);
        overviewExerciseDTO.setLevel(level);
        overviewExerciseDTO.setIntensity(intensity);
        overviewExerciseDTO.setTime(time);
        overviewExerciseDTO.setBurnedCalories(burnedCalories);
        overviewExerciseDTO.setDate(date);
        overviewExerciseDTO.setSession(session);
        return overviewExerciseDTO;
    }

    @Test
    void getOverviewExercises_ShouldReturnOverviewData() throws Exception {
        // Arrange
        String startDate = LocalDate.now().minusDays(7).toString();
        String endDate = LocalDate.now().toString();

        List<OverviewExerciseDTO> overviewList = Arrays.asList(
                createOverviewExerciseDTO("Exercise 1", 1, 2, 120.0f, 800.0f, LocalDate.now().toString(), 1),
                createOverviewExerciseDTO("Exercise 2", 2, 3, 90.0f, 600.0f, LocalDate.now().toString(), 2)
        );

        when(exerciseDoneService.getOverviewExercises(eq(1L), eq(startDate), eq(endDate)))
                .thenReturn(overviewList);

        // Act & Assert
        mockMvc.perform(get("/api/exercise-done/overview")
                        .param("userId", "1")
                        .param("startDate", startDate)
                        .param("endDate", endDate))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].exerciseName").value("Exercise 1"))
                .andExpect(jsonPath("$[0].level").value(1))
                .andExpect(jsonPath("$[0].intensity").value(2))
                .andExpect(jsonPath("$[0].time").value(120.0))
                .andExpect(jsonPath("$[0].burnedCalories").value(800.0))
                .andExpect(jsonPath("$[0].session").value(1))
                .andExpect(jsonPath("$[1].exerciseName").value("Exercise 2"))
                .andExpect(jsonPath("$[1].level").value(2))
                .andExpect(jsonPath("$[1].intensity").value(3))
                .andExpect(jsonPath("$[1].time").value(90.0))
                .andExpect(jsonPath("$[1].burnedCalories").value(600.0))
                .andExpect(jsonPath("$[1].session").value(2));
    }

    @Test
    void createExerciseDone_ShouldReturnCreatedExercise_WhenSuccessful() throws Exception {
        // Arrange
        ExerciseDoneDTO inputDto = new ExerciseDoneDTO(null, 1L, LocalDate.now().toString(), 1);
        ExerciseDone savedEntity = createExerciseDone(1L, 1L, LocalDate.now().toString(), 1);
        ExerciseDoneDTO outputDto = new ExerciseDoneDTO(savedEntity);

        when(exerciseDoneService.createExerciseDone(any(ExerciseDoneDTO.class)))
                .thenReturn(Optional.of(outputDto));

        // Act & Assert
        mockMvc.perform(post("/api/exercise-done/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(inputDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.exerciseDoneId").value(1L))
                .andExpect(jsonPath("$.exerciseDetailId").value(1L))
                .andExpect(jsonPath("$.date").exists())
                .andExpect(jsonPath("$.session").value(1));
    }

    @Test
    void createExerciseDone_ShouldReturnBadRequest_WhenExerciseAlreadyExists() throws Exception {
        // Arrange
        ExerciseDoneDTO inputDto = new ExerciseDoneDTO(null, 1L, LocalDate.now().toString(), 1);

        when(exerciseDoneService.createExerciseDone(any(ExerciseDoneDTO.class)))
                .thenReturn(Optional.empty());

        // Act & Assert
        mockMvc.perform(post("/api/exercise-done/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(inputDto)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Exercise already exists!"));
    }

    @Test
    void getExerciseDoneByUserAndDateRange_ShouldReturnListOfExercises() throws Exception {
        // Arrange
        String startDate = LocalDate.now().minusDays(7).toString();
        String endDate = LocalDate.now().toString();

        List<ExerciseDoneDTO> exerciseList = Arrays.asList(
                new ExerciseDoneDTO(createExerciseDone(1L, 1L, LocalDate.now().minusDays(1).toString(), 1)),
                new ExerciseDoneDTO(createExerciseDone(2L, 2L, LocalDate.now().toString(), 2))
        );

        when(exerciseDoneService.getExerciseDoneByUserIdAndDateRange(eq(1L), eq(startDate), eq(endDate)))
                .thenReturn(exerciseList);

        // Act & Assert
        mockMvc.perform(get("/api/exercise-done/user/1")
                        .param("startDate", startDate)
                        .param("endDate", endDate))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].exerciseDoneId").value(1L))
                .andExpect(jsonPath("$[1].exerciseDoneId").value(2L));
    }

    @Test
    void getExerciseDoneByUserAndDateRange_ShouldReturnEmptyList_WhenNoExercisesFound() throws Exception {
        // Arrange
        String startDate = LocalDate.now().minusDays(7).toString();
        String endDate = LocalDate.now().toString();

        when(exerciseDoneService.getExerciseDoneByUserIdAndDateRange(eq(1L), eq(startDate), eq(endDate)))
                .thenReturn(Collections.emptyList());

        // Act & Assert
        mockMvc.perform(get("/api/exercise-done/user/1")
                        .param("startDate", startDate)
                        .param("endDate", endDate))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    void getTotalBurnedCaloriesToday_ShouldReturnValue() throws Exception {
        // Arrange
        when(exerciseDoneService.getTotalBurnedCaloriesToday(eq(1L)))
                .thenReturn(500.0f);

        // Act & Assert
        mockMvc.perform(get("/api/exercise-done/burned-calories/today")
                        .param("userId", "1"))
                .andExpect(status().isOk())
                .andExpect(content().string("500.0"));
    }

    @Test
    void getTotalBurnedCaloriesToday_ShouldReturnZero_WhenNoExercises() throws Exception {
        // Arrange
        when(exerciseDoneService.getTotalBurnedCaloriesToday(eq(1L)))
                .thenReturn(null);

        // Act & Assert
        mockMvc.perform(get("/api/exercise-done/burned-calories/today")
                        .param("userId", "1"))
                .andExpect(status().isOk())
                .andExpect(content().string("0.0"));
    }

    @Test
    void getTotalExerciseDoneTimeToday_ShouldReturnValue() throws Exception {
        // Arrange
        when(exerciseDoneService.getTotalExerciseDoneTimeToday(eq(1L)))
                .thenReturn(60.0f);

        // Act & Assert
        mockMvc.perform(get("/api/exercise-done/time/today")
                        .param("userId", "1"))
                .andExpect(status().isOk())
                .andExpect(content().string("60.0"));
    }

    @Test
    void getTotalExerciseDoneTimeToday_ShouldReturnZero_WhenNoExercises() throws Exception {
        // Arrange
        when(exerciseDoneService.getTotalExerciseDoneTimeToday(eq(1L)))
                .thenReturn(null);

        // Act & Assert
        mockMvc.perform(get("/api/exercise-done/time/today")
                        .param("userId", "1"))
                .andExpect(status().isOk())
                .andExpect(content().string("0.0"));
    }
}
