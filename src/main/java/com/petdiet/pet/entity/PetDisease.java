package com.petdiet.pet.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.OffsetDateTime;

@Entity
@Table(name = "\"PetDiseases\"")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PetDisease {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "\"petDiseaseId\"")
    private Integer petDiseaseId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "\"petId\"", nullable = false)
    private UserPet pet;

    @Column(name = "\"diseaseId\"", nullable = false)
    private Integer diseaseId;

    @CreationTimestamp
    @Column(name = "\"createdAt\"", nullable = false, updatable = false)
    private OffsetDateTime createdAt;
}
