package com.petdiet.character.service;

import com.petdiet.character.dto.CharacterRequest;
import com.petdiet.character.dto.CharacterResponse;
import com.petdiet.character.dto.GrowthLogResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CharacterService {

    public CharacterResponse getCharacter(UUID authUuid, Integer petId) {
        throw new UnsupportedOperationException("미구현");
    }

    public List<CharacterResponse> getCharacters(UUID authUuid) {
        throw new UnsupportedOperationException("미구현");
    }

    public CharacterResponse getCharacterById(UUID authUuid, Integer characterId) {
        throw new UnsupportedOperationException("미구현");
    }

    public CharacterResponse createCharacter(UUID authUuid, CharacterRequest req) {
        throw new UnsupportedOperationException("미구현");
    }

    public CharacterResponse updateCharacterName(UUID authUuid, Integer petId, String characterName) {
        throw new UnsupportedOperationException("미구현");
    }

    public CharacterResponse gainExp(UUID authUuid, Integer petId, String activityType) {
        throw new UnsupportedOperationException("미구현");
    }

    public List<GrowthLogResponse> getGrowthLogs(UUID authUuid, Integer petId) {
        throw new UnsupportedOperationException("미구현");
    }

    public List<GrowthLogResponse> getGrowthLogsByCharacterId(UUID authUuid, Integer characterId) {
        throw new UnsupportedOperationException("미구현");
    }

    public CharacterResponse updateCharacter(UUID authUuid, Integer characterId, CharacterRequest request) {
        throw new UnsupportedOperationException("미구현");
    }

    public void deleteCharacter(UUID authUuid, Integer characterId) {
        throw new UnsupportedOperationException("미구현");
    }
}
