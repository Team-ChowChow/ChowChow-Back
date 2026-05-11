package com.petdiet.recipe.dto;

import com.petdiet.recipe.entity.RecipeIngredient;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@Builder
public class RecipeIngredientDto {
    private Integer ingredientId;
    private String ingredientName;
    private BigDecimal amount;
    private String unit;
    private String note;

    public static RecipeIngredientDto from(RecipeIngredient ri) {
        return RecipeIngredientDto.builder()
                .ingredientId(ri.getIngredientId())
                .amount(ri.getIngredientAmount())
                .unit(ri.getIngredientUnit())
                .note(ri.getIngredientNote())
                .build();
    }
}
