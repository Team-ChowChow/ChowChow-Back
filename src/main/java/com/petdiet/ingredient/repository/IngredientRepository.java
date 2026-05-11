package com.petdiet.ingredient.repository;

import com.petdiet.ingredient.entity.Ingredient;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface IngredientRepository extends JpaRepository<Ingredient, Integer> {

    boolean existsByIngredientName(String ingredientName);

    Optional<Ingredient> findByIngredientName(String ingredientName);

    Optional<Ingredient> findByIngredientNameKo(String ingredientNameKo);

    List<Ingredient> findByIngredientNameKoContainingIgnoreCase(String keyword);

    List<Ingredient> findByIngredientNameKoIsNull(Pageable pageable);

    List<Ingredient> findBySpoonacularIdIsNotNullAndCaloriesPer100gIsNull(Pageable pageable);

    @Modifying
    @Query(value = "TRUNCATE TABLE \"Ingredients\" RESTART IDENTITY CASCADE", nativeQuery = true)
    void truncateAndResetSequence();
}
