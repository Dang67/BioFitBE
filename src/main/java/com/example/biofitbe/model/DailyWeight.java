package com.example.biofitbe.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "daily_weight")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DailyWeight {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "daily_weight_id")
    private Long dailyWeightId;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    @JsonBackReference
    private User user;

    @Column(name = "weight", nullable = false)
    private Float weight;

    @Column(name = "date", nullable = false)
    private String date;
}
