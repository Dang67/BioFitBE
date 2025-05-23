package com.example.biofitbe.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "user")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long userId;

    @Column(name = "full_name", nullable = true)
    private String fullName;

    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Column(name = "hash_password", nullable = false)
    private String hashPassword;

    @Column(name = "gender", nullable = true)
    private Integer gender;

    @Column(name = "height", nullable = true)
    private Float height;

    @Column(name = "weight", nullable = true)
    private Float weight;

    @Column(name = "target_weight", nullable = true)
    private Float targetWeight;

    @Column(name = "date_of_birth", nullable = true)
    private String dateOfBirth;

    /*@Column(name = "avatar", nullable = true)
    private String avatar;*/
    @Lob // Chỉ định lưu dữ liệu lớn (Large Object)
    @Column(name = "avatar", columnDefinition = "LONGBLOB")
    private byte[] avatar;  // Lưu ảnh dưới dạng mảng byte

    @Column(name = "created_account", nullable = false)
    private String createdAccount;

    @Column(name = "reset_code", nullable = true)
    private String resetCode;

    @Column(name = "reset_code_expiry", nullable = true)
    private LocalDateTime resetCodeExpiry;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<DailyLog> dailyLogs = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Payment> payments = new ArrayList<>();
}
