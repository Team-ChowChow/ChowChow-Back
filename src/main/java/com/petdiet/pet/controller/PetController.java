package com.petdiet.pet.controller;

import com.petdiet.config.SupabasePrincipal;
import com.petdiet.pet.dto.PetRequest;
import com.petdiet.pet.dto.PetResponse;
import com.petdiet.pet.service.PetService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/pets")
@RequiredArgsConstructor
public class PetController {

    private final PetService petService;

    @GetMapping
    public ResponseEntity<List<PetResponse>> getMyPets(
            @AuthenticationPrincipal SupabasePrincipal principal) {
        return ResponseEntity.ok(petService.getMyPets(principal.authUuid()));
    }

    @GetMapping("/{petId}")
    public ResponseEntity<PetResponse> getPet(
            @AuthenticationPrincipal SupabasePrincipal principal,
            @PathVariable Integer petId) {
        return ResponseEntity.ok(petService.getPet(principal.authUuid(), petId));
    }

    @PostMapping
    public ResponseEntity<PetResponse> createPet(
            @AuthenticationPrincipal SupabasePrincipal principal,
            @RequestBody @Valid PetRequest request) {
        return ResponseEntity.ok(petService.createPet(principal.authUuid(), request));
    }

    @PatchMapping("/{petId}")
    public ResponseEntity<PetResponse> updatePet(
            @AuthenticationPrincipal SupabasePrincipal principal,
            @PathVariable Integer petId,
            @RequestBody PetRequest request) {
        return ResponseEntity.ok(petService.updatePet(principal.authUuid(), petId, request));
    }

    @DeleteMapping("/{petId}")
    public ResponseEntity<Void> deletePet(
            @AuthenticationPrincipal SupabasePrincipal principal,
            @PathVariable Integer petId) {
        petService.deletePet(principal.authUuid(), petId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/breeds")
    public ResponseEntity<?> getBreeds() {
        return ResponseEntity.ok(List.of());
    }

    @GetMapping("/{petId}/allergies")
    public ResponseEntity<?> getPetAllergies(
            @AuthenticationPrincipal SupabasePrincipal principal,
            @PathVariable Integer petId) {
        PetResponse pet = petService.getPet(principal.authUuid(), petId);
        return ResponseEntity.ok(Map.of("petId", petId, "allergyIds", pet.getAllergyIds()));
    }

    @PatchMapping("/{petId}/allergies")
    public ResponseEntity<?> updatePetAllergies(
            @AuthenticationPrincipal SupabasePrincipal principal,
            @PathVariable Integer petId,
            @RequestBody PetRequest request) {
        PetResponse pet = petService.updatePet(principal.authUuid(), petId, request);
        return ResponseEntity.ok(Map.of("petId", petId, "allergyIds", pet.getAllergyIds(), "message", "반려동물 알레르기 정보가 수정되었습니다."));
    }

    @GetMapping("/{petId}/diseases")
    public ResponseEntity<?> getPetDiseases(
            @AuthenticationPrincipal SupabasePrincipal principal,
            @PathVariable Integer petId) {
        PetResponse pet = petService.getPet(principal.authUuid(), petId);
        return ResponseEntity.ok(Map.of("petId", petId, "diseaseIds", pet.getDiseaseIds()));
    }

    @PatchMapping("/{petId}/diseases")
    public ResponseEntity<?> updatePetDiseases(
            @AuthenticationPrincipal SupabasePrincipal principal,
            @PathVariable Integer petId,
            @RequestBody PetRequest request) {
        PetResponse pet = petService.updatePet(principal.authUuid(), petId, request);
        return ResponseEntity.ok(Map.of("petId", petId, "diseaseIds", pet.getDiseaseIds(), "message", "반려동물 질환 정보가 수정되었습니다."));
    }
}
