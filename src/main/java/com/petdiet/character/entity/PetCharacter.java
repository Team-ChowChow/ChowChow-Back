package com.petdiet.character.entity;

import com.petdiet.pet.entity.UserPet;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "\"PetCharacters\"")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PetCharacter {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "\"characterId\"")
    private Integer characterId;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "\"petId\"", nullable = false, unique = true)
    private UserPet pet;

    @Column(name = "\"characterName\"", nullable = false)
    private String characterName;

    @Builder.Default
    @Column(name = "\"characterLevel\"", nullable = false)
    private Integer characterLevel = 1;

    @Builder.Default
    @Column(name = "\"currentExp\"", nullable = false)
    private Integer currentExp = 0;

    @Builder.Default
    @Column(name = "\"characterStatus\"", nullable = false)
    private String characterStatus = "ACTIVE";

    @CreationTimestamp
    @Column(name = "\"createdAt\"", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "\"updatedAt\"", nullable = false)
    private OffsetDateTime updatedAt;

    @Builder.Default
    @OneToMany(mappedBy = "character", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CharacterGrowthLog> growthLogs = new ArrayList<>();

    public void gainExp(int exp) {
        this.currentExp += exp;
        int requiredExp = this.characterLevel * 100;
        while (this.currentExp >= requiredExp) {
            this.currentExp -= requiredExp;
            this.characterLevel++;
            requiredExp = this.characterLevel * 100;
        }
    }

    public void updateName(String characterName) {
        if (characterName != null) this.characterName = characterName;
    }

    public void updateMeta(String characterName, String characterStatus) {
        if (characterName != null) this.characterName = characterName;
        if (characterStatus != null) this.characterStatus = characterStatus;
    }

    public void delete() {
        this.characterStatus = "DELETED";
    }
}
