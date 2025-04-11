/*
package com.example.biofitbe.integration_test.controller;

import com.example.biofitbe.dto.FoodDTO;
import com.example.biofitbe.service.FoodService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class FoodControllerIntegrationTest {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private FoodService foodService;

    @Autowired
    private ObjectMapper objectMapper;

    private FoodDTO testFoodDTO;

    @BeforeEach
    public void setUp() {
        testFoodDTO = new FoodDTO();
        testFoodDTO.setFoodId(1L);
        testFoodDTO.setFoodName("Test Food");
        testFoodDTO.setCalories(100.0F);
        testFoodDTO.setUserId(1L);
    }

    @Test
    public void testGetFoodsByUserId() throws Exception {
        List<FoodDTO> foodList = Arrays.asList(testFoodDTO);
        when(foodService.getFoodsByUserId(1L)).thenReturn(foodList);

        mockMvc.perform(get("/api/food/user/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].foodId").value(1L))
                .andExpect(jsonPath("$[0].foodName").value("Test Food"));
        verify(foodService, times(1)).getFoodsByUserId(1L);
    }

    @Test
    public void testGetFoodWithDetails() throws Exception {
        when(foodService.getFoodByIdWithDetails(1L)).thenReturn(testFoodDTO);

        mockMvc.perform(get("/api/food/1/details")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.foodId").value(1L))
                .andExpect(jsonPath("$.foodName").value("Test Food"));

        verify(foodService, times(1)).getFoodByIdWithDetails(1L);
    }

    @Test
    public void testCreateFood_Success() throws Exception {
        when(foodService.createFood(any(FoodDTO.class))).thenReturn(Optional.of(testFoodDTO));

        mockMvc.perform(post("/api/food/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testFoodDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.foodId").value(1L))
                .andExpect(jsonPath("$.foodName").value("Test Food"));

        verify(foodService, times(1)).createFood(any(FoodDTO.class));
    }

    @Test
    public void testCreateFood_AlreadyExists() throws Exception {
        when(foodService.createFood(any(FoodDTO.class))).thenReturn(Optional.empty());

        mockMvc.perform(post("/api/food/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testFoodDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Food already exists!"));

        verify(foodService, times(1)).createFood(any(FoodDTO.class));
    }

    @Test
    public void testDeleteFood_Success() throws Exception {
        doNothing().when(foodService).deleteFood(1L);

        mockMvc.perform(delete("/api/food/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string("Food deleted successfully"));

        verify(foodService, times(1)).deleteFood(1L);
    }

    @Test
    public void testUpdateFood_Success() throws Exception {
        when(foodService.updateFood(eq(1L), any(FoodDTO.class))).thenReturn(Optional.of(testFoodDTO));

        mockMvc.perform(put("/api/food/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testFoodDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.foodId").value(1L))
                .andExpect(jsonPath("$.foodName").value("Test Food"));

        verify(foodService, times(1)).updateFood(eq(1L), any(FoodDTO.class));
    }

    @Test
    public void testUpdateFood_NotFound() throws Exception {
        when(foodService.updateFood(eq(1L), any(FoodDTO.class))).thenReturn(Optional.empty());

        mockMvc.perform(put("/api/food/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testFoodDTO)))
                .andExpect(status().isNotFound());

        verify(foodService, times(1)).updateFood(eq(1L), any(FoodDTO.class));
    }
}
*/
