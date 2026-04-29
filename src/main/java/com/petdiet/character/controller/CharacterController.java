package com.petdiet.character.controller;

import com.petdiet.character.dto.*;
import com.petdiet.character.service.CharacterService;
import com.petdiet.config.SupabasePrincipal;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/characters")
@RequiredArgsConstructor
public class CharacterController {

    private final CharacterService characterService;

    @GetMapping("/pets/{petId}")
    public ResponseEntity<CharacterResponse> getCharacter(
            @AuthenticationPrincipal SupabasePrincipal principal,
            @PathVariable Integer petId) {
        return ResponseEntity.ok(characterService.getCharacter(principal.authUuid(), petId));
    }

    @GetMapping
    public ResponseEntity<List<CharacterResponse>> getCharacters(
            @AuthenticationPrincipal SupabasePrincipal principal,
            @RequestParam(required = false) Integer petId) {
        if (petId != null) {
            return ResponseEntity.ok(List.of(characterService.getCharacter(principal.authUuid(), petId)));
        }
        return ResponseEntity.ok(characterService.getCharacters(principal.authUuid()));
    }

    @GetMapping("/{characterId}")
    public ResponseEntity<CharacterResponse> getCharacterById(
            @AuthenticationPrincipal SupabasePrincipal principal,
            @PathVariable Integer characterId) {
        return ResponseEntity.ok(characterService.getCharacterById(principal.authUuid(), characterId));
    }

    @PostMapping
    public ResponseEntity<CharacterResponse> createCharacter(
            @AuthenticationPrincipal SupabasePrincipal principal,
            @RequestBody @Valid CharacterRequest request) {
        return ResponseEntity.ok(characterService.createCharacter(principal.authUuid(), request));
    }

    @PatchMapping("/pets/{petId}/name")
    public ResponseEntity<CharacterResponse> updateCharacterName(
            @AuthenticationPrincipal SupabasePrincipal principal,
            @PathVariable Integer petId,
            @RequestBody Map<String, String> body) {
        return ResponseEntity.ok(characterService.updateCharacterName(
                principal.authUuid(), petId, body.get("characterName")));
    }

    @PostMapping("/pets/{petId}/exp")
    public ResponseEntity<CharacterResponse> gainExp(
            @AuthenticationPrincipal SupabasePrincipal principal,
            @PathVariable Integer petId,
            @RequestBody Map<String, String> body) {
        return ResponseEntity.ok(characterService.gainExp(
                principal.authUuid(), petId, body.get("activityType")));
    }

    @GetMapping("/pets/{petId}/logs")
    public ResponseEntity<List<GrowthLogResponse>> getGrowthLogs(
            @AuthenticationPrincipal SupabasePrincipal principal,
            @PathVariable Integer petId) {
        return ResponseEntity.ok(characterService.getGrowthLogs(principal.authUuid(), petId));
    }

    @PatchMapping("/{characterId}")
    public ResponseEntity<CharacterResponse> updateCharacter(
            @AuthenticationPrincipal SupabasePrincipal principal,
            @PathVariable Integer characterId,
            @RequestBody CharacterRequest request) {
        return ResponseEntity.ok(characterService.updateCharacter(principal.authUuid(), characterId, request));
    }

    @DeleteMapping("/{characterId}")
    public ResponseEntity<?> deleteCharacter(
            @AuthenticationPrincipal SupabasePrincipal principal,
            @PathVariable Integer characterId) {
        characterService.deleteCharacter(principal.authUuid(), characterId);
        return ResponseEntity.ok(Map.of("message", "캐릭터가 삭제되었습니다.", "characterId", characterId));
    }

    @PatchMapping("/{characterId}/primary")
    public ResponseEntity<?> setPrimaryCharacter(@PathVariable Integer characterId) {
        return ResponseEntity.ok(Map.of("characterId", characterId, "isPrimary", true, "message", "대표 캐릭터로 설정되었습니다."));
    }

    @GetMapping("/{characterId}/growth-logs")
    public ResponseEntity<List<GrowthLogResponse>> getGrowthLogsByCharacter(
            @AuthenticationPrincipal SupabasePrincipal principal,
            @PathVariable Integer characterId) {
        return ResponseEntity.ok(characterService.getGrowthLogsByCharacterId(principal.authUuid(), characterId));
    }

    @GetMapping("/{characterId}/assets")
    public ResponseEntity<?> getCharacterAssets(@PathVariable Integer characterId) {
        return ResponseEntity.ok(Map.of("characterId", characterId, "assets", List.of()));
    }

    @PatchMapping("/assets/{assetId}/equip")
    public ResponseEntity<?> equipAsset(
            @PathVariable Integer assetId,
            @RequestBody Map<String, Object> body) {
        return ResponseEntity.ok(Map.of(
                "assetId", assetId,
                "isEquipped", body.getOrDefault("isEquipped", true),
                "message", "에셋 장착 상태가 변경되었습니다."
        ));
    }
}
