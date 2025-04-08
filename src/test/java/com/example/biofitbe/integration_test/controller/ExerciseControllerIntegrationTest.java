package com.example.biofitbe.integration_test.controller;


import com.example.biofitbe.dto.ExerciseDTO;
import com.example.biofitbe.dto.ExerciseDetailDTO;
import com.example.biofitbe.service.ExerciseService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.server.ResponseStatusException;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.http.HttpStatus.*;

@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("Exercise Controller Integration Tests")
public class ExerciseControllerIntegrationTest {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ExerciseService exerciseService;

    @Autowired
    private ObjectMapper objectMapper;

    private ExerciseDTO createSampleExerciseDTO(Long exerciseId, String name, int goal, int intensity, float time, float calories) {
        ExerciseDetailDTO detailDTO = new ExerciseDetailDTO();
        detailDTO.setExerciseDetailId(1L);
        detailDTO.setExerciseId(exerciseId);
        detailDTO.setExerciseGoal(goal);
        detailDTO.setIntensity(intensity);
        detailDTO.setTime(time);
        detailDTO.setBurnedCalories(calories);

        ExerciseDTO exerciseDTO = new ExerciseDTO();
        exerciseDTO.setExerciseId(exerciseId);
        exerciseDTO.setUserId(1L);
        exerciseDTO.setExerciseName(name);
        exerciseDTO.setDetailList(Collections.singletonList(detailDTO));

        return exerciseDTO;
    }

    @Nested
    @DisplayName("GET /api/exercise/user/{userId}")
    class GetExercisesByUserTests {
        @Test
        @DisplayName("Should return exercises for valid user ID")
        void shouldReturnExercisesForValidUserId() throws Exception {
            ExerciseDTO exercise1 = createSampleExerciseDTO(1L, "Push Up", 1, 2, 10.5f, 100.0f);
            ExerciseDTO exercise2 = createSampleExerciseDTO(2L, "Squat", 2, 3, 15.0f, 150.0f);
            List<ExerciseDTO> exercises = Arrays.asList(exercise1, exercise2);

            Mockito.when(exerciseService.getExercisesByUserId(1L)).thenReturn(exercises);

            mockMvc.perform(MockMvcRequestBuilders.get("/api/exercise/user/1"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(2)))
                    .andExpect(jsonPath("$[0].exerciseName", is("Push Up")))
                    .andExpect(jsonPath("$[0].detailList[0].burnedCalories", is(100.0)))
                    .andExpect(jsonPath("$[1].exerciseName", is("Squat")))
                    .andExpect(jsonPath("$[1].detailList[0].time", is(15.0)));
        }

        @Test
        @DisplayName("Should return empty list for user with no exercises")
        void shouldReturnEmptyListForUserWithNoExercises() throws Exception {
            Mockito.when(exerciseService.getExercisesByUserId(99L)).thenReturn(Collections.emptyList());

            mockMvc.perform(MockMvcRequestBuilders.get("/api/exercise/user/99"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(0)));
        }
    }

    @Nested
    @DisplayName("GET /api/exercise/{exerciseId}/details")
    class GetExerciseDetailsTests {
        @Test
        @DisplayName("Should return exercise details with valid parameters")
        void shouldReturnExerciseDetailsWithValidParameters() throws Exception {
            ExerciseDTO exercise = createSampleExerciseDTO(1L, "Pull Up", 3, 2, 20.0f, 200.0f);
            Mockito.when(exerciseService.getExerciseByGoalAndIntensity(1L, 3, 2)).thenReturn(exercise);

            mockMvc.perform(MockMvcRequestBuilders.get("/api/exercise/1/details")
                            .param("exerciseGoal", "3")
                            .param("intensity", "2"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.exerciseName", is("Pull Up")))
                    .andExpect(jsonPath("$.detailList[0].exerciseGoal", is(3)))
                    .andExpect(jsonPath("$.detailList[0].intensity", is(2)))
                    .andExpect(jsonPath("$.detailList[0].burnedCalories", is(200.0)));
        }
    }

    @Nested
    @DisplayName("POST /api/exercise/create")
    class CreateExerciseTests {
        @Test
        @DisplayName("Should create new exercise successfully")
        void shouldCreateNewExerciseSuccessfully() throws Exception {
            ExerciseDTO newExercise = createSampleExerciseDTO(null, "New Exercise", 1, 1, 5.0f, 50.0f);
            ExerciseDTO createdExercise = createSampleExerciseDTO(3L, "New Exercise", 1, 1, 5.0f, 50.0f);

            Mockito.when(exerciseService.createExercise(any(ExerciseDTO.class)))
                    .thenReturn(Optional.of(createdExercise));

            mockMvc.perform(MockMvcRequestBuilders.post("/api/exercise/create")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(newExercise)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.exerciseId", is(3)))
                    .andExpect(jsonPath("$.exerciseName", is("New Exercise")))
                    .andExpect(jsonPath("$.detailList[0].time", is(5.0)));
        }

        @Test
        @DisplayName("Should return 400 for duplicate exercise")
        void shouldReturn400ForDuplicateExercise() throws Exception {
            ExerciseDTO duplicateExercise = createSampleExerciseDTO(null, "Duplicate", 1, 1, 5.0f, 50.0f);

            Mockito.when(exerciseService.createExercise(any(ExerciseDTO.class)))
                    .thenReturn(Optional.empty());

            mockMvc.perform(MockMvcRequestBuilders.post("/api/exercise/create")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(duplicateExercise)))
                    .andExpect(status().isBadRequest())
                    .andExpect(content().string("Exercise already exists!"));
        }

    }

    @Nested
    @DisplayName("PUT /api/exercise/{exerciseId}")
    class UpdateExerciseTests {
        @Test
        @DisplayName("Should update existing exercise successfully")
        void shouldUpdateExistingExerciseSuccessfully() throws Exception {
            ExerciseDTO updatedExercise = createSampleExerciseDTO(1L, "Updated Exercise", 2, 3, 25.0f, 250.0f);
            Mockito.when(exerciseService.updateExercise(eq(1L), any(ExerciseDTO.class)))
                    .thenReturn(Optional.of(updatedExercise));

            mockMvc.perform(MockMvcRequestBuilders.put("/api/exercise/1")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(updatedExercise)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.exerciseName", is("Updated Exercise")))
                    .andExpect(jsonPath("$.detailList[0].exerciseGoal", is(2)))
                    .andExpect(jsonPath("$.detailList[0].burnedCalories", is(250.0)));
        }

        @Test
        @DisplayName("Should return 404 for non-existent exercise")
        void shouldReturn404ForNonExistentExercise() throws Exception {
            ExerciseDTO nonExistentExercise = createSampleExerciseDTO(999L, "Non-existent", 1, 1, 5.0f, 50.0f);
            Mockito.when(exerciseService.updateExercise(eq(999L), any(ExerciseDTO.class)))
                    .thenReturn(Optional.empty());

            mockMvc.perform(MockMvcRequestBuilders.put("/api/exercise/999")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(nonExistentExercise)))
                    .andExpect(status().isNotFound());
        }
    }

    @Nested
    @DisplayName("DELETE /api/exercise/{exerciseId}")
    class DeleteExerciseTests {
        @Test
        @DisplayName("Should delete exercise successfully")
        void shouldDeleteExerciseSuccessfully() throws Exception {
            mockMvc.perform(MockMvcRequestBuilders.delete("/api/exercise/1"))
                    .andExpect(status().isOk())
                    .andExpect(content().string("Exercise deleted successfully"));

            Mockito.verify(exerciseService).deleteExercise(1L);
        }

        @Test
        @DisplayName("Should return 404 when deleting non-existent exercise")
        void shouldReturn404WhenDeletingNonExistentExercise() throws Exception {
            Mockito.doThrow(new ResponseStatusException(NOT_FOUND, "Exercise not found"))
                    .when(exerciseService).deleteExercise(999L);

            mockMvc.perform(MockMvcRequestBuilders.delete("/api/exercise/999"))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$", is("Exercise not found")));
        }
    }
}
