package com.petdiet.ingredient.service;

import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;
import com.petdiet.ingredient.client.SpoonacularClient;
import com.petdiet.ingredient.client.SpoonacularClient.IngredientInfo;
import com.petdiet.ingredient.entity.Ingredient;
import com.petdiet.ingredient.repository.IngredientRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class IngredientEnrichService {

    private static final int TRANSLATE_BATCH = 40;

    private final IngredientRepository ingredientRepository;
    private final SpoonacularClient spoonacularClient;
    private final ObjectMapper objectMapper;
    private final WebClient openAiClient;
    private final String openAiModel;

    public IngredientEnrichService(
            IngredientRepository ingredientRepository,
            SpoonacularClient spoonacularClient,
            ObjectMapper objectMapper,
            @Value("${openai.api-key}") String apiKey,
            @Value("${openai.base-url:https://api.openai.com}") String baseUrl,
            @Value("${openai.model:gpt-4o}") String model) {
        this.ingredientRepository = ingredientRepository;
        this.spoonacularClient = spoonacularClient;
        this.objectMapper = objectMapper;
        this.openAiModel = model;
        this.openAiClient = WebClient.builder()
                .baseUrl(baseUrl)
                .defaultHeader("Authorization", "Bearer " + apiKey)
                .defaultHeader("Content-Type", "application/json")
                .build();
    }

    /**
     * Spoonacular에서 영양소 데이터를 가져와 저장 (batchSize건씩).
     * spoonacularId가 있고 caloriesPer100g가 없는 재료 대상.
     */
    @Transactional
    public int enrichNutrition(int batchSize) {
        List<Ingredient> targets = ingredientRepository
                .findBySpoonacularIdIsNotNullAndCaloriesPer100gIsNull(PageRequest.of(0, batchSize));
        int count = 0;
        for (Ingredient ingredient : targets) {
            IngredientInfo info = spoonacularClient.getIngredientInfo(ingredient.getSpoonacularId());
            if (info != null && info.calories() != null) {
                ingredient.updateNutrition(info.calories(), info.protein(), info.fat(),
                        info.carbohydrates(), info.fiber());
                ingredientRepository.save(ingredient);
                count++;
            }
            try { Thread.sleep(100); } catch (InterruptedException ignored) {}
        }
        log.info("영양소 보강 완료: {}건", count);
        return count;
    }

    /**
     * OpenAI로 한글 이름 번역 (TRANSLATE_BATCH건씩).
     * ingredientNameKo가 없는 재료 대상.
     */
    @Transactional
    public int translateToKorean(int batchSize) {
        int total = 0;
        int pages = (int) Math.ceil((double) batchSize / TRANSLATE_BATCH);

        for (int page = 0; page < pages; page++) {
            List<Ingredient> targets = ingredientRepository
                    .findByIngredientNameKoIsNull(PageRequest.of(0, TRANSLATE_BATCH));
            if (targets.isEmpty()) break;

            List<String> names = targets.stream().map(Ingredient::getIngredientName).toList();
            Map<String, String> translations = callGptTranslate(names);

            for (Ingredient ingredient : targets) {
                String ko = translations.get(ingredient.getIngredientName());
                if (ko != null && !ko.isBlank()) {
                    ingredient.updateKoreanName(ko);
                    ingredientRepository.save(ingredient);
                    total++;
                }
            }
            log.info("한글 번역 진행: {}건 완료 (누적 {}건)", targets.size(), total);
        }
        return total;
    }

    private Map<String, String> callGptTranslate(List<String> names) {
        String nameList = String.join("\n", names);
        String prompt = """
                다음 영어 식재료 이름들을 반려동물 식품 맥락에서 한국어로 번역해주세요.
                JSON 형식으로만 응답하세요. 형식: {"translations": {"영어이름": "한국어이름", ...}}
                번역할 재료:
                """ + nameList;

        Map<String, Object> body = Map.of(
                "model", openAiModel,
                "max_tokens", 2048,
                "messages", List.of(Map.of("role", "user", "content", prompt)),
                "response_format", Map.of("type", "json_object")
        );

        try {
            String response = openAiClient.post()
                    .uri("/v1/chat/completions")
                    .bodyValue(body)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            JsonNode root = objectMapper.readTree(response);
            String content = root.path("choices").get(0).path("message").path("content").stringValue();
            JsonNode translationsNode = objectMapper.readTree(content).path("translations");

            @SuppressWarnings("unchecked")
            Map<String, String> result = objectMapper.convertValue(translationsNode, Map.class);
            return result != null ? result : Map.of();

        } catch (Exception e) {
            log.error("GPT 번역 실패: {}", e.getMessage());
            return Map.of();
        }
    }

    /**
     * spoonacularId가 없는 재료에 대해 이름으로 재검색해서 ID 저장.
     */
    @Transactional
    public int backfillSpoonacularIds(int batchSize) {
        List<Ingredient> targets = ingredientRepository.findAll().stream()
                .filter(i -> i.getSpoonacularId() == null)
                .limit(batchSize)
                .toList();

        int count = 0;
        for (Ingredient ingredient : targets) {
            List<SpoonacularClient.IngredientResult> results =
                    spoonacularClient.searchIngredients(ingredient.getIngredientName(), 1);
            if (!results.isEmpty() && results.get(0).id() != null) {
                ingredient.updateSpoonacularId(results.get(0).id());
                ingredientRepository.save(ingredient);
                count++;
            }
            try { Thread.sleep(200); } catch (InterruptedException ignored) {}
        }
        log.info("SpoonacularId 역채움 완료: {}건", count);
        return count;
    }
}
