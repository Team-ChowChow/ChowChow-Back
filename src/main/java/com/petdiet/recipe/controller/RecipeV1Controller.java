package com.petdiet.recipe.controller;

import com.petdiet.config.SupabasePrincipal;
import com.petdiet.recipe.dto.RecipeRequest;
import com.petdiet.recipe.dto.RecipeResponse;
import com.petdiet.recipe.dto.ReviewRequest;
import com.petdiet.recipe.dto.ReviewResponse;
import com.petdiet.recipe.service.RecipeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class RecipeV1Controller {

    private final RecipeService recipeService;

    @PostMapping("/recipes/convert")
    public ResponseEntity<?> convertRecipe() {
        return ResponseEntity.ok(Map.of("message", "AI 레시피 생성이 완료되었습니다."));
    }

    @GetMapping("/recipes/{recipeId}")
    public ResponseEntity<RecipeResponse> getRecipe(@PathVariable Integer recipeId) {
        return ResponseEntity.ok(recipeService.getRecipe(recipeId));
    }

    @PostMapping("/recipes/{recipeId}/bookmark")
    public ResponseEntity<?> bookmarkRecipe(
            @AuthenticationPrincipal SupabasePrincipal principal,
            @PathVariable Integer recipeId) {
        recipeService.toggleBookmark(principal.authUuid(), recipeId);
        return ResponseEntity.ok(Map.of("recipeId", recipeId, "isBookmarked", true));
    }

    @GetMapping("/recipes")
    public ResponseEntity<?> listRecipes(
            @RequestParam(defaultValue = "latest") String sort,
            Pageable pageable) {
        return ResponseEntity.ok(Map.of(
                "sortType", sort,
                "data", recipeService.getPublicRecipes(pageable).getContent()
        ));
    }

    @PostMapping("/recipes")
    public ResponseEntity<RecipeResponse> createRecipe(
            @AuthenticationPrincipal SupabasePrincipal principal,
            @RequestBody @Valid RecipeRequest request) {
        return ResponseEntity.ok(recipeService.createRecipe(principal.authUuid(), request));
    }

    @GetMapping("/recipes/search")
    public ResponseEntity<?> searchRecipes(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String petType,
            @RequestParam(required = false) List<Integer> allergyIds,
            @RequestParam(required = false) List<Integer> diseaseIds,
            @RequestParam(defaultValue = "false") Boolean useMyPetFilter,
            Pageable pageable) {
        return ResponseEntity.ok(Map.of(
                "keyword", keyword == null ? "" : keyword,
                "petType", petType == null ? "" : petType,
                "allergyIds", allergyIds == null ? List.of() : allergyIds,
                "diseaseIds", diseaseIds == null ? List.of() : diseaseIds,
                "useMyPetFilter", useMyPetFilter,
                "data", recipeService.getPublicRecipes(pageable).getContent()
        ));
    }

    @PostMapping("/recipes/{recipeId}/reviews")
    public ResponseEntity<ReviewResponse> createReview(
            @AuthenticationPrincipal SupabasePrincipal principal,
            @PathVariable Integer recipeId,
            @RequestBody @Valid ReviewRequest request) {
        return ResponseEntity.ok(recipeService.createReview(principal.authUuid(), recipeId, request));
    }

    @GetMapping("/recipes/{recipeId}/nutrition")
    public ResponseEntity<?> getNutrition(@PathVariable Integer recipeId) {
        return ResponseEntity.ok(Map.of(
                "recipeId", recipeId,
                "totalCalories", 0,
                "protein", 0,
                "fat", 0,
                "carbo", 0
        ));
    }

    @GetMapping("/recipes/me/bookmarks")
    public ResponseEntity<?> getMyBookmarks() {
        return ResponseEntity.ok(Map.of("bookmarks", List.of(), "totalCount", 0));
    }

    @GetMapping("/recipes/{recipeId}/reviews")
    public ResponseEntity<List<ReviewResponse>> getReviews(@PathVariable Integer recipeId) {
        return ResponseEntity.ok(recipeService.getReviews(recipeId));
    }

    @GetMapping("/allergies")
    public ResponseEntity<List<Object>> getAllergies() {
        return ResponseEntity.ok(List.of());
    }

    @GetMapping("/diseases")
    public ResponseEntity<List<Object>> getDiseases() {
        return ResponseEntity.ok(List.of());
    }

    @GetMapping("/ingredients/categories")
    public ResponseEntity<List<Object>> getIngredientCategories() {
        return ResponseEntity.ok(List.of());
    }

    @GetMapping("/menus")
    public ResponseEntity<List<Object>> getMenus() {
        return ResponseEntity.ok(List.of());
    }
}
