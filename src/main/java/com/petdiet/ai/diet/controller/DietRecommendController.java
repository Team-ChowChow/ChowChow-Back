package com.petdiet.ai.diet.controller;

import com.petdiet.ai.diet.dto.DietRecommendRequest;
import com.petdiet.ai.diet.dto.DietRecommendResponse;
import com.petdiet.ai.diet.service.DietRecommendService;
import com.petdiet.ai.diet.service.DietRecommendService.RecommendContext;
import com.petdiet.ai.diet.service.DietRecipeSaveService;
import com.petdiet.ai.image.service.ImageGenerateService;
import com.petdiet.config.SupabasePrincipal;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/ai/diet")
@RequiredArgsConstructor
public class DietRecommendController {

    private final DietRecommendService dietRecommendService;
    private final DietRecipeSaveService dietRecipeSaveService;
    private final ImageGenerateService imageGenerateService;

    /**
     * 식단 추천만 반환 (레시피 저장 없음)
     */
    @PostMapping("/recommend")
    public ResponseEntity<DietRecommendResponse> recommend(
            @AuthenticationPrincipal SupabasePrincipal principal,
            @RequestBody @Valid DietRecommendRequest request) {
        return ResponseEntity.ok(dietRecommendService.recommend(
                principal.authUuid(), request.getPetId(), request.getUserNotes()));
    }

    /**
     * 식단 추천 + 레시피 자동 저장 + 이미지 생성 (옵션)
     */
    @PostMapping("/recommend-and-save")
    public ResponseEntity<DietRecommendResponse> recommendAndSave(
            @AuthenticationPrincipal SupabasePrincipal principal,
            @RequestBody @Valid DietRecommendRequest request,
            @RequestParam(defaultValue = "false") boolean generateImage) {

        RecommendContext ctx = dietRecommendService.recommendWithContext(
                principal.authUuid(), request.getPetId(), request.getUserNotes());

        String imageUrl = null;
        if (generateImage && ctx.response().getIngredients() != null) {
            List<String> ingredientNames = ctx.response().getIngredients().stream()
                    .map(i -> i.getName())
                    .toList();
            try {
                imageUrl = imageGenerateService.generateRecipeImage(
                        ctx.response().getTitle(), ingredientNames, ctx.response().getDescription())
                        .getImageUrl();
            } catch (Exception e) {
                // 이미지 생성 실패해도 레시피 저장은 계속 진행
            }
        }

        dietRecipeSaveService.saveAiRecipe(ctx.user(), ctx.pet(), ctx.response(), imageUrl);

        return ResponseEntity.ok(ctx.response());
    }
}
