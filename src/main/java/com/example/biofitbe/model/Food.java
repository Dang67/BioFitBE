package com.example.biofitbe.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "food")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Food {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "food_id", nullable = false)
    private Long foodId;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    @JsonBackReference
    private User user;

    @Column(name = "food_name", nullable = false)
    private String foodName;

    @Column(name = "date")
    private String date;

    @Column(name = "session", nullable = false)
    private String session;

    @Lob
    @Column(name = "food_image", columnDefinition = "LONGBLOB")
    private byte[] foodImage;

    @Column(name = "serving_size", nullable = false)
    private Float servingSize;

    @Column(name = "serving_size_unit", nullable = false, length = 20)
    private String servingSizeUnit;

    @Column(name = "mass", nullable = false)
    private Float mass;

    @Column(name = "calories", nullable = false)
    private Float calories;

    @Column(name = "protein", nullable = false)
    private Float protein;

    @Column(name = "carbohydrate", nullable = false)
    private Float carbohydrate;

    @Column(name = "fat", nullable = false)
    private Float fat;

    @Column(name = "sodium", nullable = true)
    private Float sodium;

    @Override
    public String toString() {
        return "Food{" +
                "foodId=" + foodId +
                ", foodName='" + foodName + '\'' +
                ", date='" + date + '\'' +
                ", session='" + session + '\'' +
                ", foodImage='" + foodImage + '\'' +
                ", servingSize=" + servingSize + " " + servingSizeUnit +
                ", mass=" + mass +
                ", calories=" + calories +
                ", protein=" + protein +
                ", carbohydrate=" + carbohydrate +
                ", fat=" + fat +
                ", sodium=" + sodium +
                '}';
    }
}
