package com.petdiet.recipe.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "\"RecipeSteps\"")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RecipeStep {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "\"recipeStepId\"")
    private Integer recipeStepId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "\"recipeId\"", nullable = false)
    private Recipe recipe;

    @Column(name = "\"stepNumber\"", nullable = false)
    private Integer stepNumber;

    @Column(name = "\"stepDescription\"", nullable = false, columnDefinition = "TEXT")
    private String stepDescription;

    @Column(name = "\"stepImageUrl\"")
    private String stepImageUrl;
}
