package com.petdiet.ingredient.client;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.node.JsonNodeType;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
public class SpoonacularClient {

    private final WebClient webClient;
    private final String apiKey;
    private final ObjectMapper objectMapper;

    public SpoonacularClient(
            @Value("${spoonacular.base-url}") String baseUrl,
            @Value("${spoonacular.api-key}") String apiKey,
            ObjectMapper objectMapper) {
        this.webClient = WebClient.builder().baseUrl(baseUrl).build();
        this.apiKey = apiKey;
        this.objectMapper = objectMapper;
    }

    public List<IngredientResult> searchIngredients(String query, int number) {
        try {
            String response = webClient.get()
                    .uri(uri -> uri.path("/food/ingredients/search")
                            .queryParam("query", query)
                            .queryParam("number", number)
                            .queryParam("metaInformation", true)
                            .queryParam("apiKey", apiKey)
                            .build())
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            JsonNode root = objectMapper.readTree(response);
            JsonNode results = root.path("results");

            List<IngredientResult> list = new ArrayList<>();
            for (JsonNode item : results) {
                String name = str(item.path("name"));
                String aisle = str(item.path("aisle"));
                Integer id = item.path("id").getNodeType() == JsonNodeType.NUMBER
                        ? item.path("id").intValue() : null;
                if (name != null && !name.isBlank()) {
                    list.add(new IngredientResult(id, name, aisle));
                }
            }
            return list;

        } catch (Exception e) {
            log.warn("Spoonacular 검색 실패 [query={}]: {}", query, e.getMessage());
            return List.of();
        }
    }

    public IngredientInfo getIngredientInfo(int spoonacularId) {
        try {
            String response = webClient.get()
                    .uri(uri -> uri.path("/food/ingredients/{id}/information")
                            .queryParam("amount", 100)
                            .queryParam("unit", "grams")
                            .queryParam("apiKey", apiKey)
                            .build(spoonacularId))
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            JsonNode root = objectMapper.readTree(response);
            JsonNode nutrients = root.path("nutrition").path("nutrients");

            BigDecimal calories = null, protein = null, fat = null, carbs = null, fiber = null;
            for (JsonNode n : nutrients) {
                String name = str(n.path("name"));
                if (name == null) continue;
                BigDecimal amount = n.path("amount").getNodeType() == JsonNodeType.NUMBER
                        ? BigDecimal.valueOf(n.path("amount").doubleValue()) : null;
                switch (name) {
                    case "Calories"       -> calories = amount;
                    case "Protein"        -> protein  = amount;
                    case "Fat"            -> fat      = amount;
                    case "Carbohydrates"  -> carbs    = amount;
                    case "Fiber"          -> fiber    = amount;
                }
            }
            return new IngredientInfo(calories, protein, fat, carbs, fiber);

        } catch (Exception e) {
            log.warn("Spoonacular 영양소 조회 실패 [id={}]: {}", spoonacularId, e.getMessage());
            return null;
        }
    }

    private static String str(JsonNode node) {
        return node.getNodeType() == JsonNodeType.STRING ? node.stringValue() : null;
    }

    public record IngredientResult(Integer id, String name, String aisle) {}

    public record IngredientInfo(
            BigDecimal calories,
            BigDecimal protein,
            BigDecimal fat,
            BigDecimal carbohydrates,
            BigDecimal fiber) {}
}
