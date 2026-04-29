package com.petdiet.character.service;

import com.petdiet.auth.entity.User;
import com.petdiet.auth.repository.UserRepository;
import com.petdiet.character.dto.*;
import com.petdiet.character.entity.*;
import com.petdiet.character.repository.*;
import com.petdiet.pet.entity.UserPet;
import com.petdiet.pet.repository.UserPetRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CharacterService {

    private final PetCharacterRepository characterRepository;
    private final CharacterGrowthLogRepository growthLogRepository;
    private final UserPetRepository userPetRepository;
    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public CharacterResponse getCharacter(UUID authUuid, Integer petId) {
        User user = findUser(authUuid);
        UserPet pet = findPet(petId, user);
        PetCharacter character = characterRepository.findByPet(pet)
                .orElseThrow(() -> new IllegalArgumentException("캐릭터를 찾을 수 없습니다."));
        return CharacterResponse.from(character);
    }

    @Transactional(readOnly = true)
    public List<CharacterResponse> getCharacters(UUID authUuid) {
        User user = findUser(authUuid);
        return characterRepository.findAllByPet_User(user).stream()
                .map(CharacterResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public CharacterResponse getCharacterById(UUID authUuid, Integer characterId) {
        User user = findUser(authUuid);
        PetCharacter character = characterRepository.findByCharacterIdAndPet_User(characterId, user)
                .orElseThrow(() -> new IllegalArgumentException("캐릭터를 찾을 수 없습니다."));
        return CharacterResponse.from(character);
    }

    @Transactional
    public CharacterResponse createCharacter(UUID authUuid, CharacterRequest req) {
        User user = findUser(authUuid);
        UserPet pet = findPet(req.getPetId(), user);
        if (characterRepository.existsByPet(pet)) {
            throw new IllegalStateException("이미 캐릭터가 존재합니다.");
        }
        PetCharacter character = characterRepository.save(PetCharacter.builder()
                .pet(pet)
                .characterName(req.getCharacterName())
                .build());
        return CharacterResponse.from(character);
    }

    @Transactional
    public CharacterResponse updateCharacterName(UUID authUuid, Integer petId, String characterName) {
        User user = findUser(authUuid);
        UserPet pet = findPet(petId, user);
        PetCharacter character = characterRepository.findByPet(pet)
                .orElseThrow(() -> new IllegalArgumentException("캐릭터를 찾을 수 없습니다."));
        character.updateName(characterName);
        return CharacterResponse.from(character);
    }

    @Transactional
    public CharacterResponse gainExp(UUID authUuid, Integer petId, String activityType) {
        User user = findUser(authUuid);
        UserPet pet = findPet(petId, user);
        PetCharacter character = characterRepository.findByPet(pet)
                .orElseThrow(() -> new IllegalArgumentException("캐릭터를 찾을 수 없습니다."));

        int exp = ActivityExp.getExp(activityType);
        character.gainExp(exp);

        growthLogRepository.save(CharacterGrowthLog.builder()
                .character(character)
                .activityType(activityType)
                .expGained(exp)
                .currentExp(character.getCurrentExp())
                .currentLevel(character.getCharacterLevel())
                .build());

        return CharacterResponse.from(character);
    }

    @Transactional(readOnly = true)
    public List<GrowthLogResponse> getGrowthLogs(UUID authUuid, Integer petId) {
        User user = findUser(authUuid);
        UserPet pet = findPet(petId, user);
        PetCharacter character = characterRepository.findByPet(pet)
                .orElseThrow(() -> new IllegalArgumentException("캐릭터를 찾을 수 없습니다."));
        return growthLogRepository.findAllByCharacterOrderByCreatedAtDesc(character).stream()
                .map(GrowthLogResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<GrowthLogResponse> getGrowthLogsByCharacterId(UUID authUuid, Integer characterId) {
        User user = findUser(authUuid);
        PetCharacter character = characterRepository.findByCharacterIdAndPet_User(characterId, user)
                .orElseThrow(() -> new IllegalArgumentException("캐릭터를 찾을 수 없습니다."));
        return growthLogRepository.findAllByCharacterOrderByCreatedAtDesc(character).stream()
                .map(GrowthLogResponse::from)
                .toList();
    }

    @Transactional
    public CharacterResponse updateCharacter(UUID authUuid, Integer characterId, CharacterRequest request) {
        User user = findUser(authUuid);
        PetCharacter character = characterRepository.findByCharacterIdAndPet_User(characterId, user)
                .orElseThrow(() -> new IllegalArgumentException("캐릭터를 찾을 수 없습니다."));
        character.updateMeta(request.getCharacterName(), request.getCharacterState());
        return CharacterResponse.from(character);
    }

    @Transactional
    public void deleteCharacter(UUID authUuid, Integer characterId) {
        User user = findUser(authUuid);
        PetCharacter character = characterRepository.findByCharacterIdAndPet_User(characterId, user)
                .orElseThrow(() -> new IllegalArgumentException("캐릭터를 찾을 수 없습니다."));
        character.delete();
    }

    private User findUser(UUID authUuid) {
        return userRepository.findByAuthUuid(authUuid)
                .orElseThrow(() -> new IllegalStateException("유저를 찾을 수 없습니다."));
    }

    private UserPet findPet(Integer petId, User user) {
        return userPetRepository.findByPetIdAndUser(petId, user)
                .orElseThrow(() -> new IllegalArgumentException("반려동물을 찾을 수 없습니다."));
    }
}
