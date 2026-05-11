package com.petdiet.ai.diet.service;

import com.petdiet.ai.diet.dto.DietIngredientDto;
import com.petdiet.ai.diet.dto.DietRecommendResponse;
import com.petdiet.auth.entity.User;
import com.petdiet.ingredient.service.IngredientResolutionService;
import com.petdiet.pet.entity.UserPet;
import com.petdiet.recipe.entity.Menu;
import com.petdiet.recipe.entity.Recipe;
import com.petdiet.recipe.entity.RecipeIngredient;
import com.petdiet.recipe.entity.RecipeStep;
import com.petdiet.recipe.repository.MenuRepository;
import com.petdiet.recipe.repository.RecipeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * AI 식단 추천 결과를 Recipes 테이블에 저장.
 * DietRecommendService가 GPT 응답을 받은 후 이 서비스를 호출.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DietRecipeSaveService {

    private final RecipeRepository recipeRepository;
    private final MenuRepository menuRepository;
    private final IngredientResolutionService ingredientResolutionService;

    private static final Pattern AMOUNT_PATTERN = Pattern.compile("([\\d.]+)\\s*(.*)");

    @Transactional
    public Recipe saveAiRecipe(User user, UserPet pet, DietRecommendResponse response, String imageUrl) {
        Integer menuId = resolveMenuId(pet, response);

        String warningsJson = buildWarningsText(response.getWarnings());

        Recipe recipe = Recipe.builder()
                .user(user)
                .pet(pet)
                .menuId(menuId)
                .recipeTitle(response.getTitle())
                .recipeDescription(response.getDescription())
                .feedingAmount(response.getFeedingAmount())
                .imageUrl(imageUrl)
                .warnings(warningsJson)
                .isAiGenerated(true)
                .isPublic(false)
                .build();

        addIngredients(recipe, response.getIngredients());
        addSteps(recipe, response.getSteps());

        Recipe saved = recipeRepository.save(recipe);
        log.info("AI 레시피 저장 완료: recipeId={}, title={}", saved.getRecipeId(), saved.getRecipeTitle());
        return saved;
    }

    private Integer resolveMenuId(UserPet pet, DietRecommendResponse response) {
        String petType = pet.getPetType();
        String category = inferMenuCategory(response);

        Optional<Menu> menu = menuRepository.findFirstByPetTypeAndMenuCategoryAndMenuStatus(
                petType, category, "ACTIVE");

        if (menu.isPresent()) return menu.get().getMenuId();

        // fallback: 해당 petType의 첫 번째 메뉴
        List<Menu> menus = menuRepository.findAllByPetTypeAndMenuStatusOrderByMenuIdAsc(petType, "ACTIVE");
        if (!menus.isEmpty()) return menus.get(0).getMenuId();

        log.warn("menuId 결정 실패 — petType={}, category={}", petType, category);
        return 1;
    }

    private String inferMenuCategory(DietRecommendResponse response) {
        String title = response.getTitle() != null ? response.getTitle().toLowerCase() : "";
        String desc = response.getDescription() != null ? response.getDescription().toLowerCase() : "";
        String combined = title + " " + desc;

        if (combined.contains("간식") || combined.contains("treat") || combined.contains("snack")) {
            return "간식";
        }
        if (response.getWarnings() != null && !response.getWarnings().isEmpty()) {
            return "특수식";
        }
        return "일반식";
    }

    private void addIngredients(Recipe recipe, List<DietIngredientDto> dtos) {
        if (dtos == null) return;
        for (int i = 0; i < dtos.size(); i++) {
            DietIngredientDto dto = dtos.get(i);
            Optional<Integer> ingredientId = ingredientResolutionService.resolveIngredientId(dto.getName());

            BigDecimal amount = null;
            String unit = null;
            if (dto.getAmount() != null) {
                Matcher m = AMOUNT_PATTERN.matcher(dto.getAmount().trim());
                if (m.matches()) {
                    try { amount = new BigDecimal(m.group(1)); } catch (NumberFormatException ignored) {}
                    unit = m.group(2).trim().isEmpty() ? null : m.group(2).trim();
                }
            }

            recipe.getIngredients().add(RecipeIngredient.builder()
                    .recipe(recipe)
                    .ingredientId(ingredientId.orElse(null))
                    .ingredientAmount(amount)
                    .ingredientUnit(unit)
                    .ingredientNote(ingredientId.isEmpty() ? dto.getName() : null)
                    .build());
        }
    }

    private void addSteps(Recipe recipe, List<String> steps) {
        if (steps == null) return;
        for (int i = 0; i < steps.size(); i++) {
            recipe.getSteps().add(RecipeStep.builder()
                    .recipe(recipe)
                    .stepNumber(i + 1)
                    .stepDescription(steps.get(i))
                    .build());
        }
    }

    private String buildWarningsText(List<String> warnings) {
        if (warnings == null || warnings.isEmpty()) return null;
        return String.join("\n", warnings);
    }
}
