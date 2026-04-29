package com.petdiet.ai.diet.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class DietRecommendResponse {
    private String title;
    private String description;
    private List<DietIngredientDto> ingredients;
    private List<String> steps;
    private String feedingAmount;
    private List<String> warnings;
}
