package com.petdiet.character.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.OffsetDateTime;

@Entity
@Table(name = "\"CharacterGrowthLogs\"")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CharacterGrowthLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "\"growthLogId\"")
    private Integer growthLogId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "\"characterId\"", nullable = false)
    private PetCharacter character;

    @Column(name = "\"activityType\"", nullable = false)
    private String activityType;

    @Column(name = "\"expGained\"", nullable = false)
    private Integer expGained;

    @Column(name = "\"currentExp\"", nullable = false)
    private Integer currentExp;

    @Column(name = "\"currentLevel\"", nullable = false)
    private Integer currentLevel;

    @CreationTimestamp
    @Column(name = "\"createdAt\"", nullable = false, updatable = false)
    private OffsetDateTime createdAt;
}
