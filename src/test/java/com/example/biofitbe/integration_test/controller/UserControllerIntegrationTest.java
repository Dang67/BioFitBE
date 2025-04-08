package com.example.biofitbe.integration_test.controller;

import com.example.biofitbe.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import com.example.biofitbe.dto.RegisterRequest;
import com.example.biofitbe.dto.LoginRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;

import static org.assertj.core.api.Assertions.assertThat;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class UserControllerIntegrationTest {
    @LocalServerPort
    private int port;
    @Autowired
    private UserRepository userRepository;

    private String getBaseUrl() {
        return "http://localhost:" + port + "/api/user";
    }

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    // tạo ra một email duy nhất cho mỗi lần chạy test
    private static final String testEmail = "test" + System.currentTimeMillis() + "@example.com";
    private static final String testPassword = "test" + System.currentTimeMillis() + "Test2@password";

    @Test
    @Order(1)
    @Transactional
    @Rollback
    void testRegisterUser() {

        if (userRepository.findByEmail(testEmail).isPresent()) {
            userRepository.deleteByEmail(testEmail);
        }

        RegisterRequest register = new RegisterRequest();
        register.setEmail(testEmail);
        register.setPassword(testPassword);
        register.setConfirmPassword(testPassword);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<RegisterRequest> request = new HttpEntity<>(register, headers);

        ResponseEntity<String> response = restTemplate.postForEntity(
                getBaseUrl() + "/register", request, String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    @Order(2)
    void testLoginUser() {
        LoginRequest login = new LoginRequest();
        login.setEmail(testEmail);
        login.setPassword(testPassword);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<LoginRequest> request = new HttpEntity<>(login, headers);

        ResponseEntity<String> response = restTemplate.postForEntity(
                getBaseUrl() + "/login", request, String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).contains("email");
    }

    @Test
    @Order(3)
    void testGetUserByEmail() {
        ResponseEntity<String> response = restTemplate.getForEntity(
                getBaseUrl() + "/" + testEmail, String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).contains(testEmail);
    }
}
