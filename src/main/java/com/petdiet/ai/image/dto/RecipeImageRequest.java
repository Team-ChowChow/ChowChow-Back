package com.petdiet.ai.image.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

import java.util.List;

@Getter
public class RecipeImageRequest {

    @NotBlank
    private String recipeName;

    private List<String> ingredients;

    private String description;
}
