package com.petdiet.ai.diet.controller;

import com.petdiet.ai.diet.dto.DietRecommendRequest;
import com.petdiet.ai.diet.dto.DietRecommendResponse;
import com.petdiet.ai.diet.service.DietRecommendService;
import com.petdiet.config.SupabasePrincipal;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/ai/diet")
@RequiredArgsConstructor
public class DietRecommendController {

    private final DietRecommendService dietRecommendService;

    @PostMapping("/recommend")
    public ResponseEntity<DietRecommendResponse> recommend(
            @AuthenticationPrincipal SupabasePrincipal principal,
            @RequestBody @Valid DietRecommendRequest request) {
        return ResponseEntity.ok(dietRecommendService.recommend(
                principal.authUuid(), request.getPetId(), request.getUserNotes()));
    }
}
