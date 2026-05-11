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

    @Column(name = "\"userId\"")
    private Integer userId;

    @Column(name = "\"activityType\"", nullable = false)
    private String activityType;

    @Builder.Default
    @Column(name = "\"expAmount\"", nullable = false)
    private Integer expAmount = 0;

    @Column(name = "\"activityDescription\"")
    private String activityDescription;

    @Column(name = "\"expGained\"")
    private Integer expGained;

    @Column(name = "\"currentExp\"")
    private Integer currentExp;

    @Column(name = "\"currentLevel\"")
    private Integer currentLevel;

    @CreationTimestamp
    @Column(name = "\"createdAt\"", nullable = false, updatable = false)
    private OffsetDateTime createdAt;
}
