package com.petdiet.ai.image.controller;

import com.petdiet.ai.image.dto.CharacterImageRequest;
import com.petdiet.ai.image.dto.ImageGenerateResponse;
import com.petdiet.ai.image.dto.RecipeImageRequest;
import com.petdiet.ai.image.service.ImageGenerateService;
import com.petdiet.config.SupabasePrincipal;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/ai/image")
@RequiredArgsConstructor
public class ImageGenerateController {

    private final ImageGenerateService imageGenerateService;

    @PostMapping("/character")
    public ResponseEntity<ImageGenerateResponse> generateCharacter(
            @AuthenticationPrincipal SupabasePrincipal principal,
            @RequestBody @Valid CharacterImageRequest request) {
        return ResponseEntity.ok(imageGenerateService.generateCharacterImage(
                principal.authUuid(), request.getPetId(), request.getStyle()));
    }

    @PostMapping("/recipe")
    public ResponseEntity<ImageGenerateResponse> generateRecipeImage(
            @RequestBody @Valid RecipeImageRequest request) {
        return ResponseEntity.ok(imageGenerateService.generateRecipeImage(
                request.getRecipeName(), request.getIngredients(), request.getDescription()));
    }
}
