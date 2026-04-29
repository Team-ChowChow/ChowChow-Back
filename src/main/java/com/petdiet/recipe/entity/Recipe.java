package com.petdiet.recipe.entity;

import com.petdiet.auth.entity.User;
import com.petdiet.pet.entity.UserPet;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "\"Recipes\"")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Recipe {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "\"recipeId\"")
    private Integer recipeId;

    @Column(name = "\"menuId\"", nullable = false)
    private Integer menuId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "\"userId\"")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "\"petId\"")
    private UserPet pet;

    @Column(name = "\"recipeTitle\"", nullable = false)
    private String recipeTitle;

    @Column(name = "\"recipeDescription\"")
    private String recipeDescription;

    @Column(name = "\"recipePurpose\"")
    private String recipePurpose;

    @Column(name = "\"feedingAmount\"")
    private String feedingAmount;

    @Builder.Default
    @Column(name = "\"isAiGenerated\"", nullable = false)
    private Boolean isAiGenerated = false;

    @Builder.Default
    @Column(name = "\"isPublic\"", nullable = false)
    private Boolean isPublic = true;

    @Builder.Default
    @Column(name = "\"recipeStatus\"", nullable = false)
    private String recipeStatus = "ACTIVE";

    @CreationTimestamp
    @Column(name = "\"createdAt\"", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "\"updatedAt\"", nullable = false)
    private OffsetDateTime updatedAt;

    @Builder.Default
    @OneToMany(mappedBy = "recipe", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<RecipeIngredient> ingredients = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "recipe", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("stepNumber ASC")
    private List<RecipeStep> steps = new ArrayList<>();

    public void update(String recipeTitle, String recipeDescription,
                       String recipePurpose, String feedingAmount, Boolean isPublic) {
        if (recipeTitle != null) this.recipeTitle = recipeTitle;
        if (recipeDescription != null) this.recipeDescription = recipeDescription;
        if (recipePurpose != null) this.recipePurpose = recipePurpose;
        if (feedingAmount != null) this.feedingAmount = feedingAmount;
        if (isPublic != null) this.isPublic = isPublic;
    }

    public void delete() {
        this.recipeStatus = "DELETED";
    }
}
