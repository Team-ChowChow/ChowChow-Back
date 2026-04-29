package com.petdiet.recipe.repository;

import com.petdiet.auth.entity.User;
import com.petdiet.recipe.entity.Recipe;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RecipeRepository extends JpaRepository<Recipe, Integer> {

    Page<Recipe> findAllByIsPublicTrueAndRecipeStatus(String recipeStatus, Pageable pageable);

    Page<Recipe> findAllByUserAndRecipeStatus(User user, String recipeStatus, Pageable pageable);

    Optional<Recipe> findByRecipeIdAndRecipeStatus(Integer recipeId, String recipeStatus);
}
