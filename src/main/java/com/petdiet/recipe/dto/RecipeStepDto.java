package com.petdiet.recipe.dto;

import com.petdiet.recipe.entity.RecipeStep;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class RecipeStepDto {
    private Integer stepNumber;
    private String stepDescription;
    private String stepImage;

    public static RecipeStepDto from(RecipeStep step) {
        return RecipeStepDto.builder()
                .stepNumber(step.getStepNumber())
                .stepDescription(step.getStepDescription())
                .stepImage(step.getStepImage())
                .build();
    }
}
