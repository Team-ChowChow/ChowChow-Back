package com.petdiet.pet.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.OffsetDateTime;

@Entity
@Table(name = "\"PetAllergies\"")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PetAllergy {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "\"petAllergyId\"")
    private Integer petAllergyId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "\"petId\"", nullable = false)
    private UserPet pet;

    @Column(name = "\"allergyId\"", nullable = false)
    private Integer allergyId;

    @CreationTimestamp
    @Column(name = "\"createdAt\"", nullable = false, updatable = false)
    private OffsetDateTime createdAt;
}
