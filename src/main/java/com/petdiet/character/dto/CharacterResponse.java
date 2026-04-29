package com.petdiet.character.dto;

import com.petdiet.character.entity.PetCharacter;
import lombok.Builder;
import lombok.Getter;

import java.time.OffsetDateTime;
import java.util.List;

@Getter
@Builder
public class CharacterResponse {
    private Integer characterId;
    private Integer petId;
    private String characterName;
    private String characterType;
    private Integer characterLevel;
    private Integer currentExp;
    private Integer requiredExp;
    private String characterState;
    private Boolean isPrimary;
    private OffsetDateTime updatedAt;
    private List<GrowthLogResponse> growthLogs;
    private OffsetDateTime createdAt;

    public static CharacterResponse from(PetCharacter character) {
        return CharacterResponse.builder()
                .characterId(character.getCharacterId())
                .petId(character.getPet().getPetId())
                .characterName(character.getCharacterName())
                .characterType(null)
                .characterLevel(character.getCharacterLevel())
                .currentExp(character.getCurrentExp())
                .requiredExp(character.getCharacterLevel() * 100)
                .characterState(character.getCharacterStatus())
                .isPrimary(false)
                .updatedAt(character.getUpdatedAt())
                .growthLogs(List.of())
                .createdAt(character.getCreatedAt())
                .build();
    }
}
