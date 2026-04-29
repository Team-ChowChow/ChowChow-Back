package com.petdiet.recipe.dto;

import com.petdiet.recipe.entity.Recipe;
import lombok.Builder;
import lombok.Getter;

import java.time.OffsetDateTime;
import java.util.List;

@Getter
@Builder
public class RecipeResponse {
    private Integer recipeId;
    private Integer menuId;
    private Integer petId;
    private String recipeTitle;
    private String recipeDescription;
    private String recipePurpose;
    private String feedingAmount;
    private String difficulty;
    private Integer cookingTime;
    private Boolean isAiGenerated;
    private Boolean isPublic;
    private OffsetDateTime createdAt;
    private List<RecipeIngredientDto> ingredients;
    private List<RecipeStepDto> steps;

    public static RecipeResponse from(Recipe recipe) {
        return RecipeResponse.builder()
                .recipeId(recipe.getRecipeId())
                .menuId(recipe.getMenuId())
                .petId(recipe.getPet() != null ? recipe.getPet().getPetId() : null)
                .recipeTitle(recipe.getRecipeTitle())
                .recipeDescription(recipe.getRecipeDescription())
                .recipePurpose(recipe.getRecipePurpose())
                .feedingAmount(recipe.getFeedingAmount())
                .difficulty(null)
                .cookingTime(null)
                .isAiGenerated(recipe.getIsAiGenerated())
                .isPublic(recipe.getIsPublic())
                .createdAt(recipe.getCreatedAt())
                .ingredients(recipe.getIngredients().stream().map(RecipeIngredientDto::from).toList())
                .steps(recipe.getSteps().stream().map(RecipeStepDto::from).toList())
                .build();
    }
}
