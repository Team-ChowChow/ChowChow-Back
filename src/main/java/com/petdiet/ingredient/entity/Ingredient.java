package com.petdiet.ingredient.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Entity
@Table(name = "\"Ingredients\"")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Ingredient {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "\"ingredientId\"")
    private Integer ingredientId;

    @Column(name = "\"ingredientName\"", nullable = false, unique = true, length = 100)
    private String ingredientName;

    @Column(name = "\"ingredientNameKo\"", length = 200)
    private String ingredientNameKo;

    @Column(name = "\"ingredientDescription\"")
    private String ingredientDescription;

    @Column(name = "\"ingredientCategory\"", length = 50)
    private String ingredientCategory;

    @Column(name = "\"petType\"", length = 10)
    private String petType;

    @Column(name = "\"spoonacularId\"")
    private Integer spoonacularId;

    @Builder.Default
    @Column(name = "\"isToxicToDog\"", nullable = false)
    private Boolean isToxicToDog = false;

    @Builder.Default
    @Column(name = "\"isToxicToCat\"", nullable = false)
    private Boolean isToxicToCat = false;

    @Column(name = "\"toxicityNote\"")
    private String toxicityNote;

    @Column(name = "\"caloriesPer100g\"", precision = 8, scale = 2)
    private BigDecimal caloriesPer100g;

    @Column(name = "\"proteinG\"", precision = 8, scale = 2)
    private BigDecimal proteinG;

    @Column(name = "\"fatG\"", precision = 8, scale = 2)
    private BigDecimal fatG;

    @Column(name = "\"carbohydrateG\"", precision = 8, scale = 2)
    private BigDecimal carbohydrateG;

    @Column(name = "\"fiberG\"", precision = 8, scale = 2)
    private BigDecimal fiberG;

    @CreationTimestamp
    @Column(name = "\"createdAt\"", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "\"updatedAt\"", nullable = false)
    private OffsetDateTime updatedAt;

    public void updateSpoonacularId(Integer spoonacularId) {
        this.spoonacularId = spoonacularId;
    }

    public void updateKoreanName(String ingredientNameKo) {
        this.ingredientNameKo = ingredientNameKo;
    }

    public void updateNutrition(BigDecimal calories, BigDecimal protein,
                                BigDecimal fat, BigDecimal carbohydrate, BigDecimal fiber) {
        this.caloriesPer100g = calories;
        this.proteinG = protein;
        this.fatG = fat;
        this.carbohydrateG = carbohydrate;
        this.fiberG = fiber;
    }

    public void updateToxicity(boolean toxicToDog, boolean toxicToCat, String note) {
        this.isToxicToDog = toxicToDog;
        this.isToxicToCat = toxicToCat;
        this.toxicityNote = note;
    }
}
