package com.petdiet.recipe.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Entity
@Table(name = "\"RecipeNutritionSummaries\"")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RecipeNutritionSummary {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "\"nutritionSummaryId\"")
    private Integer nutritionSummaryId;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "\"recipeId\"", nullable = false, unique = true)
    private Recipe recipe;

    @Column(name = "\"totalWeight\"", precision = 8, scale = 2)
    private BigDecimal totalWeight;

    @Column(name = "\"totalCalories\"", precision = 10, scale = 2)
    private BigDecimal totalCalories;

    @Column(name = "\"proteinG\"", precision = 7, scale = 2)
    private BigDecimal proteinG;

    @Column(name = "\"fatG\"", precision = 7, scale = 2)
    private BigDecimal fatG;

    @Column(name = "\"carbohydrateG\"", precision = 7, scale = 2)
    private BigDecimal carbohydrateG;

    @Column(name = "\"sodiumMg\"", precision = 8, scale = 2)
    private BigDecimal sodiumMg;

    @Column(name = "\"nutritionComment\"", columnDefinition = "TEXT")
    private String nutritionComment;

    @CreationTimestamp
    @Column(name = "\"createdAt\"", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "\"updatedAt\"", nullable = false)
    private OffsetDateTime updatedAt;
}
