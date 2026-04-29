package com.petdiet.ai.diet.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class DietRecommendRequest {

    @NotNull
    private Integer petId;

    private String userNotes;
}
