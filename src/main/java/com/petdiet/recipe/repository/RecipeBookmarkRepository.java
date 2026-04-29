package com.petdiet.recipe.repository;

import com.petdiet.auth.entity.User;
import com.petdiet.recipe.entity.Recipe;
import com.petdiet.recipe.entity.RecipeBookmark;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RecipeBookmarkRepository extends JpaRepository<RecipeBookmark, Integer> {

    Optional<RecipeBookmark> findByRecipeAndUser(Recipe recipe, User user);

    boolean existsByRecipeAndUser(Recipe recipe, User user);
}
