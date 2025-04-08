package com.example.biofitbe.unit_test.service;

import com.example.biofitbe.dto.FoodDTO;
import com.example.biofitbe.model.Food;
import com.example.biofitbe.model.User;
import com.example.biofitbe.repository.FoodRepository;
import com.example.biofitbe.repository.UserRepository;
import com.example.biofitbe.service.FoodService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class FoodServiceUnitTest {
    @Mock
    private FoodRepository foodRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private FoodService foodService;

    private Food food;
    private User user;
    private FoodDTO foodDTO;

    @BeforeEach
    void setUp() {
        // Khởi tạo dữ liệu mẫu
        user = new User();
        user.setUserId(1L);

        food = new Food();
        food.setFoodId(1L);
        food.setUser(user);
        food.setFoodName("Chicken Salad");
        food.setSession("Lunch");
        food.setDate(String.valueOf(LocalDate.of(2025, 4, 7)));
        food.setCalories(300.0F);
        food.setProtein(25.0F);
        food.setCarbohydrate(10.0F);
        food.setFat(15.0F);

        foodDTO = new FoodDTO();
        foodDTO.setUserId(1L);
        foodDTO.setFoodName("Chicken Salad");
        foodDTO.setSession("Lunch");
        foodDTO.setDate(String.valueOf(LocalDate.of(2025, 4, 7)));
        foodDTO.setCalories(300.0F);
        foodDTO.setProtein(25.0F);
        foodDTO.setCarbohydrate(10.0F);
        foodDTO.setFat(15.0F);
    }

    @Test
    void testGetFoodsByUserId() {
        when(foodRepository.findByUserUserIdOrderByFoodNameAsc(1L)).thenReturn(Collections.singletonList(food));

        List<FoodDTO> result = foodService.getFoodsByUserId(1L);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Chicken Salad", result.get(0).getFoodName());
        verify(foodRepository, times(1)).findByUserUserIdOrderByFoodNameAsc(1L);
    }

    @Test
    void testGetFoodByIdWithDetails_Success() {
        when(foodRepository.findById(1L)).thenReturn(Optional.of(food));

        FoodDTO result = foodService.getFoodByIdWithDetails(1L);

        assertNotNull(result);
        assertEquals("Chicken Salad", result.getFoodName());
        verify(foodRepository, times(1)).findById(1L);
    }

    @Test
    void testGetFoodByIdWithDetails_NotFound() {
        when(foodRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> foodService.getFoodByIdWithDetails(1L));
        verify(foodRepository, times(1)).findById(1L);
    }

    @Test
    void testCreateFood_Success() {
        when(foodRepository.findByUserUserIdAndFoodName(1L, "Chicken Salad")).thenReturn(Optional.empty());
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(foodRepository.save(any(Food.class))).thenReturn(food);

        Optional<FoodDTO> result = foodService.createFood(foodDTO);

        assertTrue(result.isPresent());
        assertEquals("Chicken Salad", result.get().getFoodName());
        verify(foodRepository, times(1)).save(any(Food.class));
    }

    @Test
    void testCreateFood_FoodExists() {
        when(foodRepository.findByUserUserIdAndFoodName(1L, "Chicken Salad")).thenReturn(Optional.of(food));

        Optional<FoodDTO> result = foodService.createFood(foodDTO);

        assertFalse(result.isPresent());
        verify(foodRepository, never()).save(any(Food.class));
    }

    @Test
    void testCreateFood_UserNotFound() {
        when(foodRepository.findByUserUserIdAndFoodName(1L, "Chicken Salad")).thenReturn(Optional.empty());
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        Optional<FoodDTO> result = foodService.createFood(foodDTO);

        assertFalse(result.isPresent());
        verify(foodRepository, never()).save(any(Food.class));
    }

    @Test
    void testDeleteFood_Success() {
        when(foodRepository.findById(1L)).thenReturn(Optional.of(food));

        foodService.deleteFood(1L);

        verify(foodRepository, times(1)).delete(food);
    }

    @Test
    void testDeleteFood_NotFound() {
        when(foodRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResponseStatusException.class, () -> foodService.deleteFood(1L));
        verify(foodRepository, never()).delete(any(Food.class));
    }

    @Test
    void testUpdateFood_Success() {
        FoodDTO updatedFoodDTO = new FoodDTO();
        updatedFoodDTO.setFoodName("Updated Salad");
        updatedFoodDTO.setSession("Dinner");
        updatedFoodDTO.setDate(String.valueOf(LocalDate.of(2025, 4, 8)));

        when(foodRepository.findById(1L)).thenReturn(Optional.of(food));
        when(foodRepository.save(any(Food.class))).thenReturn(food);

        Optional<FoodDTO> result = foodService.updateFood(1L, updatedFoodDTO);

        assertTrue(result.isPresent());
        assertEquals("Updated Salad", result.get().getFoodName());
        verify(foodRepository, times(1)).save(food);
    }

    @Test
    void testUpdateFood_NotFound() {
        when(foodRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> foodService.updateFood(1L, foodDTO));
        verify(foodRepository, never()).save(any(Food.class));
    }
}
