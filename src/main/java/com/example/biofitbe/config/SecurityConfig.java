package com.example.biofitbe.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable()) // Tắt CSRF để tránh lỗi khi gửi request từ Postman
                .cors(cors -> cors.disable()) // Nếu test trên Postman thì tắt CORS
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/**").permitAll() // Cho phép tất cả request API
                        .anyRequest().authenticated() // Các request khác vẫn cần xác thực
                );

        return http.build();
    }
}