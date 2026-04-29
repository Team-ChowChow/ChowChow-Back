package com.petdiet.recipe.entity;

import jakarta.persistence.*;
import lombok.*;

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

    @Column(name = "\"amount\"")
    private String amount;

    @Column(name = "\"unit\"")
    private String unit;
}
