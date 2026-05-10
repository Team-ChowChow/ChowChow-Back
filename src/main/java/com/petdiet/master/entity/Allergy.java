package com.petdiet.master.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "\"Allergies\"")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Allergy {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "\"allergyId\"")
    private Integer allergyId;

    @Column(name = "\"allergyName\"", nullable = false, unique = true, length = 100)
    private String allergyName;

    @Column(name = "\"allergyDescription\"")
    private String allergyDescription;
}
