package com.petdiet.recipe.repository;

import com.petdiet.recipe.entity.RecipeNutritionSummary;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RecipeNutritionSummaryRepository extends JpaRepository<RecipeNutritionSummary, Integer> {
    Optional<RecipeNutritionSummary> findByRecipeRecipeId(Integer recipeId);
}
