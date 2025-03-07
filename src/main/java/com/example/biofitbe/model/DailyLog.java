package com.example.biofitbe.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "daily_log")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DailyLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "daily_log_id")
    private Long dailyWeightId;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    @JsonBackReference
    private User user;

    @Column(name = "weight", nullable = true)
    private Float weight;

    @Column(name = "water", nullable = true)
    private Float water;

    @Column(name = "date", nullable = false)
    private String date;
}
