package com.example.biofitbe.unit_test.service;

import com.example.biofitbe.dto.DailyLogDTO;
import com.example.biofitbe.model.DailyLog;
import com.example.biofitbe.model.User;
import com.example.biofitbe.repository.DailyLogRepository;
import com.example.biofitbe.repository.UserRepository;
import com.example.biofitbe.service.DailyLogService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class DailyLogServiceUnitTest {

    @Mock
    private DailyLogRepository dailyLogRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private DailyLogService dailyLogService;

    @Test
    void testFindDailyLogByUserIdAndDate() {
        Long userId = 1L;
        String date = "2024-04-07";

        DailyLog dailyLog = new DailyLog();
        when(dailyLogRepository.findByUserUserIdAndDate(eq(userId), eq(date)))
                .thenReturn(Optional.of(dailyLog));

        Optional<DailyLog> result = dailyLogService.findDailyLogByUserIdAndDate(userId, date);
        assertTrue(result.isPresent());
        assertEquals(dailyLog, result.get());
    }

    @Test
    void testGetLatestDailyWeightByUserId_Success() {
        Long userId = 1L;
        User user = User.builder().userId(userId).build();
        DailyLog log = DailyLog.builder()
                .dailyLogId(1L)
                .user(user)
                .weight(65.5f)
                .water(2.0f)
                .date("2024-04-07")
                .build();

        when(dailyLogRepository.findLatestWeightByUserId(userId)).thenReturn(List.of(log));

        DailyLogDTO dto = dailyLogService.getLatestDailyWeightByUserId(userId);

        assertEquals(userId, dto.getUserId());
        assertEquals(65.5f, dto.getWeight());
        assertEquals(2.0f, dto.getWater());
        assertEquals("2024-04-07", dto.getDate());
    }

    @Test
    void testGetLatestDailyWeightByUserId_EmptyList() {
        when(dailyLogRepository.findLatestWeightByUserId(anyLong())).thenReturn(Collections.emptyList());

        assertThrows(RuntimeException.class, () -> dailyLogService.getLatestDailyWeightByUserId(1L));
    }

    @Test
    void testSaveOrUpdateDailyWeight_Update() {
        Long userId = 1L;
        String date = "2024-04-07";

        DailyLog existingLog = new DailyLog();
        existingLog.setWeight(60f);
        existingLog.setWater(2f);

        when(dailyLogRepository.findByUserUserIdAndDate(userId, date)).thenReturn(Optional.of(existingLog));
        when(dailyLogRepository.save(any(DailyLog.class))).thenReturn(existingLog);

        DailyLog result = dailyLogService.saveOrUpdateDailyWeight(userId, 65f, 2.5f, date);

        assertEquals(65f, result.getWeight());
        assertEquals(2.5f, result.getWater());
        verify(dailyLogRepository).save(existingLog);
    }

    @Test
    void testSaveOrUpdateDailyWeight_InsertNew() {
        Long userId = 1L;
        String date = "2024-04-07";

        User user = User.builder().userId(userId).build();

        when(dailyLogRepository.findByUserUserIdAndDate(userId, date)).thenReturn(Optional.empty());
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(dailyLogRepository.save(any(DailyLog.class))).thenAnswer(invocation -> invocation.getArgument(0));

        DailyLog result = dailyLogService.saveOrUpdateDailyWeight(userId, 70f, 3f, date);

        assertEquals(70f, result.getWeight());
        assertEquals(3f, result.getWater());
        assertEquals(user, result.getUser());
        assertEquals(date, result.getDate());
    }

    @Test
    void testGetWeightHistory_FillMissingDays() {
        Long userId = 1L;
        LocalDate today = LocalDate.now();
        User user = User.builder().userId(userId).weight(60f).build();

        List<DailyLog> logs = List.of(
                DailyLog.builder().date(today.minusDays(2).toString()).weight(65f).build(),
                DailyLog.builder().date(today.toString()).weight(66f).build()
        );

        when(dailyLogRepository.findAllByUserIdOrdered(userId)).thenReturn(logs);
        lenient().when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        List<DailyLogDTO> result = dailyLogService.getWeightHistory(userId);

        assertEquals(7, result.size()); // 7 ngày
        assertEquals(today.minusDays(6).toString(), result.get(0).getDate()); // Ngày xa nhất
        assertEquals(today.toString(), result.get(6).getDate()); // Ngày gần nhất
    }
}