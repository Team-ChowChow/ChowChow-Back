package com.petdiet.ingredient.service;

import com.petdiet.ingredient.client.SpoonacularClient;
import com.petdiet.ingredient.client.SpoonacularClient.IngredientResult;
import com.petdiet.ingredient.entity.Ingredient;
import com.petdiet.ingredient.repository.IngredientRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class IngredientSyncService {

    private final SpoonacularClient spoonacularClient;
    private final IngredientRepository ingredientRepository;

    private static final List<String> SEARCH_QUERIES = List.of(
            // 육류
            "chicken", "beef", "salmon", "turkey", "duck", "lamb", "tuna", "pork",
            "cod", "sardine", "rabbit", "venison", "herring", "mackerel", "anchovy",
            "tilapia", "shrimp", "egg", "liver", "kidney", "heart",
            // 채소
            "carrot", "broccoli", "spinach", "sweet potato", "pumpkin", "peas",
            "green beans", "zucchini", "celery", "cucumber", "kale", "cabbage",
            "cauliflower", "asparagus", "beet", "bell pepper", "mushroom", "lettuce",
            // 과일
            "apple", "banana", "blueberry", "watermelon", "strawberry", "pear",
            "raspberry", "cranberry", "cantaloupe", "mango", "peach",
            // 곡물
            "rice", "oat", "barley", "quinoa", "millet", "potato", "lentil", "chickpea",
            // 유제품
            "yogurt", "cheese", "cottage cheese", "kefir", "butter", "milk",
            // 기타
            "coconut oil", "olive oil", "flaxseed", "sunflower seeds", "peanut butter",
            "tofu", "tempeh"
    );

    private static final Set<String> FRUIT_KEYWORDS = Set.of(
            "apple", "banana", "blueberry", "strawberry", "watermelon", "mango",
            "pineapple", "peach", "pear", "cherry", "grape", "melon", "raspberry",
            "cranberry", "cantaloupe", "apricot", "plum", "fig", "kiwi"
    );

    @Transactional
    public int sync() {
        log.info("Spoonacular 동기화 시작 — 기존 데이터 초기화");
        ingredientRepository.truncateAndResetSequence();

        int saved = 0;
        Set<String> seenNames = new HashSet<>();

        for (String query : SEARCH_QUERIES) {
            List<IngredientResult> results = spoonacularClient.searchIngredients(query, 20);
            for (IngredientResult result : results) {
                String nameLower = result.name().toLowerCase();
                if (seenNames.contains(nameLower)) {
                    continue;
                }
                seenNames.add(nameLower);

                String category = resolveCategory(result.aisle(), result.name());
                ingredientRepository.save(Ingredient.builder()
                        .ingredientName(result.name())
                        .ingredientNameKo(null)
                        .ingredientCategory(category)
                        .ingredientDescription(generateDescription(result.name(), category))
                        .petType("ALL")
                        .spoonacularId(result.id())
                        .build());
                saved++;
            }
            log.debug("Spoonacular 동기화 [query={}]: {}건 검색", query, results.size());
        }

        log.info("Spoonacular 동기화 완료: {}건 저장", saved);
        return saved;
    }

    private String resolveCategory(String aisle, String name) {
        if (aisle == null) return resolveByName(name);
        String lower = aisle.toLowerCase();

        if (lower.contains("meat") || lower.contains("seafood") || lower.contains("fish")) return "육류";
        if (lower.contains("produce")) {
            String nameLower = name.toLowerCase();
            for (String fruit : FRUIT_KEYWORDS) {
                if (nameLower.contains(fruit)) return "과일";
            }
            return "채소";
        }
        if (lower.contains("pasta") || lower.contains("rice") || lower.contains("cereal")
                || lower.contains("grain") || lower.contains("baking") || lower.contains("bread")) return "곡물";
        if (lower.contains("dairy") || lower.contains("cheese") || lower.contains("milk")
                || lower.contains("egg")) return "유제품";
        if (lower.contains("oil") || lower.contains("nut") || lower.contains("seed")) return "기타";

        return resolveByName(name);
    }

    private String resolveByName(String name) {
        String lower = name.toLowerCase();
        if (lower.contains("chicken") || lower.contains("beef") || lower.contains("salmon")
                || lower.contains("turkey") || lower.contains("duck") || lower.contains("lamb")
                || lower.contains("tuna") || lower.contains("pork") || lower.contains("fish")
                || lower.contains("shrimp") || lower.contains("liver") || lower.contains("heart")) return "육류";
        for (String fruit : FRUIT_KEYWORDS) {
            if (lower.contains(fruit)) return "과일";
        }
        if (lower.contains("carrot") || lower.contains("broccoli") || lower.contains("spinach")
                || lower.contains("potato") || lower.contains("pumpkin") || lower.contains("kale")
                || lower.contains("cabbage") || lower.contains("celery")) return "채소";
        if (lower.contains("rice") || lower.contains("oat") || lower.contains("barley")
                || lower.contains("quinoa") || lower.contains("millet") || lower.contains("lentil")) return "곡물";
        if (lower.contains("yogurt") || lower.contains("cheese") || lower.contains("milk")
                || lower.contains("butter") || lower.contains("kefir")) return "유제품";
        return "기타";
    }

    private String generateDescription(String name, String category) {
        String detail = switch (category) {
            case "육류" -> "단백질이 풍부한 동물성 식재료. 반려동물의 근육 유지와 에너지 공급에 활용됩니다.";
            case "채소" -> "비타민·무기질이 풍부한 채소. 소량씩 골고루 급여하면 건강에 도움이 됩니다.";
            case "곡물" -> "탄수화물·식이섬유를 공급하는 곡물. 에너지원으로 적당량 급여하세요.";
            case "유제품" -> "칼슘·단백질이 풍부한 유제품. 유당 불내증이 없는 경우 소량 급여 가능합니다.";
            case "과일" -> "천연 당분과 비타민을 함유한 과일. 당분이 높으므로 간식으로 소량만 급여하세요.";
            default -> "반려동물 식단에 활용 가능한 식재료. 처음 급여 시 소량부터 시작하세요.";
        };
        return name + " — " + detail;
    }
}
