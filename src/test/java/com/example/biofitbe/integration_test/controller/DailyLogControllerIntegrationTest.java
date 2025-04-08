package com.example.biofitbe.integration_test.controller;

import com.example.biofitbe.dto.DailyLogDTO;
import com.example.biofitbe.model.DailyLog;
import com.example.biofitbe.model.User;
import com.example.biofitbe.service.DailyLogService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
public class DailyLogControllerIntegrationTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @MockitoBean
    private DailyLogService dailyLogService;

    private DailyLog sampleDailyLog;
    private DailyLogDTO sampleDailyLogDTO;

    @BeforeEach
    void setUp() {
        // Tạo đối tượng User để tránh NPE
        User user = new User();
        user.setUserId(1L);

        // Khởi tạo sampleDailyLog với một Người dùng
        sampleDailyLog = new DailyLog();
        sampleDailyLog.setDailyLogId(1L);
        sampleDailyLog.setUser(user); // Thiết lập Người dùng để tránh NPE
        sampleDailyLog.setWeight(70.5F);
        sampleDailyLog.setWater(2.0F);
        sampleDailyLog.setDate("2025-04-06");

        // Khởi tạo sampleDailyLogDTO
        sampleDailyLogDTO = new DailyLogDTO(
                1L,
                1L,
                70.5F,
                2.0F,
                "2025-04-06"
        );
    }

    @Test
    void testGetLatestDailyWeight() {
        when(dailyLogService.getLatestDailyWeightByUserId(1L))
                .thenReturn(sampleDailyLogDTO);


        ResponseEntity<DailyLogDTO> response = restTemplate.getForEntity(
                "/api/daily-log/user/1/latest",
                DailyLogDTO.class
        );


        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(sampleDailyLogDTO.getWeight(), response.getBody().getWeight());
        assertEquals(sampleDailyLogDTO.getDate(), response.getBody().getDate());
    }

    @Test
    void testCheckDailyWeight_Found() {

        when(dailyLogService.findDailyLogByUserIdAndDate(1L, "2025-04-06"))
                .thenReturn(Optional.of(sampleDailyLog));


        ResponseEntity<DailyLogDTO> response = restTemplate.getForEntity(
                "/api/daily-log/user/1/date/2025-04-06",
                DailyLogDTO.class
        );


        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(sampleDailyLog.getWeight(), response.getBody().getWeight());
    }

    @Test
    void testCheckDailyWeight_NotFound() {

        when(dailyLogService.findDailyLogByUserIdAndDate(1L, "2025-04-06"))
                .thenReturn(Optional.empty());

        ResponseEntity<DailyLogDTO> response = restTemplate.getForEntity(
                "/api/daily-log/user/1/date/2025-04-06",
                DailyLogDTO.class
        );

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        assertNull(response.getBody());
    }

    @Test
    void testSaveOrUpdateDailyWeight() {

        when(dailyLogService.saveOrUpdateDailyWeight(any(Long.class), any(Float.class),
                any(Float.class), any(String.class)))
                .thenReturn(sampleDailyLog);

        HttpEntity<DailyLogDTO> request = new HttpEntity<>(sampleDailyLogDTO);

        ResponseEntity<DailyLogDTO> response = restTemplate.postForEntity(
                "/api/daily-log/save-or-update",
                request,
                DailyLogDTO.class
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(sampleDailyLogDTO.getWeight(), response.getBody().getWeight());
        assertEquals(sampleDailyLogDTO.getDate(), response.getBody().getDate());
    }

    @Test
    void testGetWeightHistory() {

        List<DailyLogDTO> historyList = Collections.singletonList(sampleDailyLogDTO);
        when(dailyLogService.getWeightHistory(1L)).thenReturn(historyList);

        ResponseEntity<DailyLogDTO[]> response = restTemplate.getForEntity(
                "/api/daily-log/user/1/history",
                DailyLogDTO[].class
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().length);
        assertEquals(sampleDailyLogDTO.getWeight(), response.getBody()[0].getWeight());
    }
}
