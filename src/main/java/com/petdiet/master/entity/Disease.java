package com.petdiet.master.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "\"Diseases\"")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Disease {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "\"diseaseId\"")
    private Integer diseaseId;

    @Column(name = "\"diseaseName\"", nullable = false, unique = true, length = 100)
    private String diseaseName;

    @Column(name = "\"diseaseDescription\"")
    private String diseaseDescription;
}
