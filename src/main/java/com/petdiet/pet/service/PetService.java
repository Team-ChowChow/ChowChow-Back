package com.petdiet.pet.service;

import com.petdiet.auth.entity.User;
import com.petdiet.auth.repository.UserRepository;
import com.petdiet.pet.dto.PetRequest;
import com.petdiet.pet.dto.PetResponse;
import com.petdiet.pet.entity.PetAllergy;
import com.petdiet.pet.entity.PetDisease;
import com.petdiet.pet.entity.UserPet;
import com.petdiet.pet.repository.UserPetRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PetService {

    private final UserPetRepository userPetRepository;
    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public List<PetResponse> getMyPets(UUID authUuid) {
        User user = findUser(authUuid);
        return userPetRepository.findAllByUser(user).stream()
                .map(PetResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public PetResponse getPet(UUID authUuid, Integer petId) {
        User user = findUser(authUuid);
        UserPet pet = findPet(petId, user);
        return PetResponse.from(pet);
    }

    @Transactional
    public PetResponse createPet(UUID authUuid, PetRequest req) {
        User user = findUser(authUuid);
        UserPet pet = userPetRepository.save(UserPet.builder()
                .user(user)
                .petName(req.getPetName())
                .petType(req.getPetType())
                .breedId(req.getBreedId())
                .petGender(req.getPetGender())
                .petBirthdate(req.getPetBirthdate())
                .petWeight(req.getPetWeight())
                .isNeutered(req.getIsNeutered())
                .petProfileImg(req.getPetProfileImg())
                .build());

        addAllergies(pet, req.getAllergyIds());
        addDiseases(pet, req.getDiseaseIds());
        return PetResponse.from(userPetRepository.save(pet));
    }

    @Transactional
    public PetResponse updatePet(UUID authUuid, Integer petId, PetRequest req) {
        User user = findUser(authUuid);
        UserPet pet = findPet(petId, user);
        pet.update(req.getPetName(), req.getPetGender(), req.getPetBirthdate(),
                req.getPetWeight(), req.getIsNeutered(), req.getPetProfileImg(), req.getBreedId());

        if (req.getAllergyIds() != null) {
            pet.getAllergies().clear();
            addAllergies(pet, req.getAllergyIds());
        }
        if (req.getDiseaseIds() != null) {
            pet.getDiseases().clear();
            addDiseases(pet, req.getDiseaseIds());
        }
        return PetResponse.from(userPetRepository.save(pet));
    }

    @Transactional
    public void deletePet(UUID authUuid, Integer petId) {
        User user = findUser(authUuid);
        UserPet pet = findPet(petId, user);
        userPetRepository.delete(pet);
    }

    private void addAllergies(UserPet pet, List<Integer> allergyIds) {
        if (allergyIds == null) return;
        allergyIds.forEach(id -> pet.getAllergies().add(
                PetAllergy.builder().pet(pet).allergyId(id).build()));
    }

    private void addDiseases(UserPet pet, List<Integer> diseaseIds) {
        if (diseaseIds == null) return;
        diseaseIds.forEach(id -> pet.getDiseases().add(
                PetDisease.builder().pet(pet).diseaseId(id).build()));
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
