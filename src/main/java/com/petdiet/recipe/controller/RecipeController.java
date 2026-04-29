package com.petdiet.recipe.controller;

import com.petdiet.config.SupabasePrincipal;
import com.petdiet.recipe.dto.*;
import com.petdiet.recipe.service.RecipeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/recipes")
@RequiredArgsConstructor
public class RecipeController {

    private final RecipeService recipeService;

    @GetMapping
    public ResponseEntity<Page<RecipeResponse>> getPublicRecipes(
            @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(recipeService.getPublicRecipes(pageable));
    }

    @GetMapping("/my")
    public ResponseEntity<Page<RecipeResponse>> getMyRecipes(
            @AuthenticationPrincipal SupabasePrincipal principal,
            @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(recipeService.getMyRecipes(principal.authUuid(), pageable));
    }

    @GetMapping("/{recipeId}")
    public ResponseEntity<RecipeResponse> getRecipe(@PathVariable Integer recipeId) {
        return ResponseEntity.ok(recipeService.getRecipe(recipeId));
    }

    @PostMapping
    public ResponseEntity<RecipeResponse> createRecipe(
            @AuthenticationPrincipal SupabasePrincipal principal,
            @RequestBody @Valid RecipeRequest request) {
        return ResponseEntity.ok(recipeService.createRecipe(principal.authUuid(), request));
    }

    @PatchMapping("/{recipeId}")
    public ResponseEntity<RecipeResponse> updateRecipe(
            @AuthenticationPrincipal SupabasePrincipal principal,
            @PathVariable Integer recipeId,
            @RequestBody RecipeRequest request) {
        return ResponseEntity.ok(recipeService.updateRecipe(principal.authUuid(), recipeId, request));
    }

    @DeleteMapping("/{recipeId}")
    public ResponseEntity<Void> deleteRecipe(
            @AuthenticationPrincipal SupabasePrincipal principal,
            @PathVariable Integer recipeId) {
        recipeService.deleteRecipe(principal.authUuid(), recipeId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{recipeId}/bookmark")
    public ResponseEntity<Void> toggleBookmark(
            @AuthenticationPrincipal SupabasePrincipal principal,
            @PathVariable Integer recipeId) {
        recipeService.toggleBookmark(principal.authUuid(), recipeId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{recipeId}/reviews")
    public ResponseEntity<List<ReviewResponse>> getReviews(@PathVariable Integer recipeId) {
        return ResponseEntity.ok(recipeService.getReviews(recipeId));
    }

    @PostMapping("/{recipeId}/reviews")
    public ResponseEntity<ReviewResponse> createReview(
            @AuthenticationPrincipal SupabasePrincipal principal,
            @PathVariable Integer recipeId,
            @RequestBody @Valid ReviewRequest request) {
        return ResponseEntity.ok(recipeService.createReview(principal.authUuid(), recipeId, request));
    }

    @PatchMapping("/{recipeId}/reviews/{reviewId}")
    public ResponseEntity<ReviewResponse> updateReview(
            @AuthenticationPrincipal SupabasePrincipal principal,
            @PathVariable Integer recipeId,
            @PathVariable Integer reviewId,
            @RequestBody @Valid ReviewRequest request) {
        return ResponseEntity.ok(recipeService.updateReview(principal.authUuid(), recipeId, reviewId, request));
    }

    @DeleteMapping("/{recipeId}/reviews/{reviewId}")
    public ResponseEntity<Void> deleteReview(
            @AuthenticationPrincipal SupabasePrincipal principal,
            @PathVariable Integer recipeId,
            @PathVariable Integer reviewId) {
        recipeService.deleteReview(principal.authUuid(), recipeId, reviewId);
        return ResponseEntity.noContent().build();
    }
}
