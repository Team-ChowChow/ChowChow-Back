package com.petdiet.recipe.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class ReviewRequest {
    @NotNull
    @Min(1) @Max(5)
    private Double rating;
    @Min(1) @Max(5)
    private Double starRating;

    private String reviewContent;

    public Double getRating() {
        return rating != null ? rating : starRating;
    }
}
