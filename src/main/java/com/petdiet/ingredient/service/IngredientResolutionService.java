package com.petdiet.ingredient.service;

import com.petdiet.ingredient.client.SpoonacularClient;
import com.petdiet.ingredient.entity.Ingredient;
import com.petdiet.ingredient.repository.IngredientRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * AI가 반환한 재료명(한글/영어 혼용)을 Ingredients 테이블의 ingredientId로 변환.
 * 순서: 한글 정확일치 → 한글 LIKE → Spoonacular 검색 후 신규 생성
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class IngredientResolutionService {

    private final IngredientRepository ingredientRepository;
    private final SpoonacularClient spoonacularClient;

    @Transactional
    public Optional<Integer> resolveIngredientId(String name) {
        if (name == null || name.isBlank()) return Optional.empty();

        String trimmed = name.trim();

        // 1. 한글 이름 정확 일치
        Optional<Ingredient> byKo = ingredientRepository.findByIngredientNameKo(trimmed);
        if (byKo.isPresent()) return Optional.of(byKo.get().getIngredientId());

        // 2. 영어 이름 정확 일치
        Optional<Ingredient> byEn = ingredientRepository.findByIngredientName(trimmed);
        if (byEn.isPresent()) return Optional.of(byEn.get().getIngredientId());

        // 3. 한글 이름 LIKE 검색 (첫 번째 결과)
        List<Ingredient> likeResults = ingredientRepository.findByIngredientNameKoContainingIgnoreCase(trimmed);
        if (!likeResults.isEmpty()) {
            return Optional.of(likeResults.get(0).getIngredientId());
        }

        // 4. Spoonacular 검색 → 신규 Ingredient 생성
        return resolveFromSpoonacular(trimmed);
    }

    private Optional<Integer> resolveFromSpoonacular(String name) {
        try {
            List<SpoonacularClient.IngredientResult> results = spoonacularClient.searchIngredients(name, 1);
            if (results.isEmpty()) {
                log.warn("Spoonacular에서 재료를 찾지 못함: {}", name);
                return Optional.empty();
            }

            SpoonacularClient.IngredientResult result = results.get(0);
            String resultName = result.name();

            // 이미 DB에 영어 이름이 있으면 재사용
            Optional<Ingredient> existing = ingredientRepository.findByIngredientName(resultName);
            if (existing.isPresent()) {
                return Optional.of(existing.get().getIngredientId());
            }

            // 신규 저장
            Ingredient saved = ingredientRepository.save(Ingredient.builder()
                    .ingredientName(resultName)
                    .ingredientNameKo(name)
                    .spoonacularId(result.id())
                    .ingredientCategory("기타")
                    .petType("ALL")
                    .ingredientDescription(resultName + " — AI 레시피 생성 시 자동 등록된 식재료")
                    .build());

            log.info("신규 Ingredient 자동 등록: {} (spoonacularId={})", resultName, result.id());
            return Optional.of(saved.getIngredientId());

        } catch (Exception e) {
            log.error("Spoonacular 재료 검색 실패: {} — {}", name, e.getMessage());
            return Optional.empty();
        }
    }
}
