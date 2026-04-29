package com.petdiet.recipe.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

import java.util.List;

@Getter
public class RecipeRequest {
    @NotNull
    private Integer menuId;

    private Integer petId;

    @NotBlank
    private String recipeTitle;

    private String recipeDescription;
    private String recipePurpose;
    private String feedingAmount;
    private String difficulty;
    private Integer cookingTime;
    private String requestNote;
    private Boolean isPublic;

    private List<RecipeIngredientDto> ingredients;
    private List<RecipeStepDto> steps;
}
