package com.petdiet.ingredient.controller;

import com.petdiet.ingredient.service.IngredientEnrichService;
import com.petdiet.ingredient.service.IngredientSyncService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/admin/ingredients")
@RequiredArgsConstructor
public class AdminIngredientController {

    private final IngredientSyncService ingredientSyncService;
    private final IngredientEnrichService ingredientEnrichService;

    @PostMapping("/sync")
    public ResponseEntity<Map<String, Object>> sync() {
        int saved = ingredientSyncService.sync();
        return ResponseEntity.ok(Map.of("message", "Spoonacular 동기화 완료", "saved", saved));
    }

    @PostMapping("/enrich/translate")
    public ResponseEntity<Map<String, Object>> translate(
            @RequestParam(defaultValue = "200") int batchSize) {
        int count = ingredientEnrichService.translateToKorean(batchSize);
        return ResponseEntity.ok(Map.of("message", "한글 번역 완료", "translated", count));
    }

    @PostMapping("/enrich/nutrition")
    public ResponseEntity<Map<String, Object>> nutrition(
            @RequestParam(defaultValue = "100") int batchSize) {
        int count = ingredientEnrichService.enrichNutrition(batchSize);
        return ResponseEntity.ok(Map.of("message", "영양소 보강 완료", "enriched", count));
    }

    @PostMapping("/enrich/spoonacular-ids")
    public ResponseEntity<Map<String, Object>> backfillIds(
            @RequestParam(defaultValue = "50") int batchSize) {
        int count = ingredientEnrichService.backfillSpoonacularIds(batchSize);
        return ResponseEntity.ok(Map.of("message", "SpoonacularId 역채움 완료", "updated", count));
    }
}
