package com.petdiet.master.entity;

import com.petdiet.ingredient.entity.Ingredient;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.OffsetDateTime;

@Entity
@Table(name = "\"AllergyIngredients\"")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AllergyIngredient {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "\"allergyIngredientId\"")
    private Integer allergyIngredientId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "\"allergyId\"", nullable = false)
    private Allergy allergy;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "\"ingredientId\"", nullable = false)
    private Ingredient ingredient;

    @CreationTimestamp
    @Column(name = "\"createdAt\"", nullable = false, updatable = false)
    private OffsetDateTime createdAt;
}
