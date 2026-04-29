package com.petdiet.recipe.repository;

import com.petdiet.auth.entity.User;
import com.petdiet.recipe.entity.Recipe;
import com.petdiet.recipe.entity.RecipeReview;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RecipeReviewRepository extends JpaRepository<RecipeReview, Integer> {

    List<RecipeReview> findAllByRecipe(Recipe recipe);

    Optional<RecipeReview> findByRecipeAndUser(Recipe recipe, User user);

    boolean existsByRecipeAndUser(Recipe recipe, User user);
}
