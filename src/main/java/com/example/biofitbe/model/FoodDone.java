package com.example.biofitbe.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "food_done")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FoodDone {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "food_done_id", nullable = false)
    private Long foodDoneId;

    @ManyToOne
    @JoinColumn(name = "food_id", nullable = false)
    @JsonBackReference
    private Food food;

    @Column(name = "date", nullable = false)
    private String date;

    @Column(name = "session", nullable = false)
    private String session;
}
