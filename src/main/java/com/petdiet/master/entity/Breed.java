package com.petdiet.master.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "\"Breeds\"")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Breed {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "\"breedId\"")
    private Integer breedId;

    @Column(name = "\"petType\"", nullable = false, length = 10)
    private String petType;

    @Column(name = "\"breedName\"", nullable = false, unique = true, length = 100)
    private String breedName;

    @Column(name = "\"breedDescription\"")
    private String breedDescription;
}
