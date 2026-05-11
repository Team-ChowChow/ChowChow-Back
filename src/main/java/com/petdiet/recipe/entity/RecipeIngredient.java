package com.petdiet.recipe.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "\"RecipeIngredients\"")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RecipeIngredient {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "\"recipeIngredientId\"")
    private Integer recipeIngredientId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "\"recipeId\"", nullable = false)
    private Recipe recipe;

    @Column(name = "\"ingredientId\"", nullable = false)
    private Integer ingredientId;

    @Column(name = "\"ingredientAmount\"", precision = 10, scale = 2)
    private BigDecimal ingredientAmount;

    @Column(name = "\"ingredientUnit\"", length = 20)
    private String ingredientUnit;

    @Column(name = "\"ingredientNote\"", length = 200)
    private String ingredientNote;
}
