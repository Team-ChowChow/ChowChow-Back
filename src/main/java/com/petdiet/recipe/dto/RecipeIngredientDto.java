package com.petdiet.recipe.dto;

import com.petdiet.recipe.entity.RecipeIngredient;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class RecipeIngredientDto {
    private Integer ingredientId;
    private String ingredientName;
    private String amount;
    private String unit;
    private Boolean isSubstitute;

    public static RecipeIngredientDto from(RecipeIngredient ri) {
        return RecipeIngredientDto.builder()
                .ingredientId(ri.getIngredientId())
                .ingredientName(null)
                .amount(ri.getAmount())
                .unit(ri.getUnit())
                .isSubstitute(null)
                .build();
    }
}
