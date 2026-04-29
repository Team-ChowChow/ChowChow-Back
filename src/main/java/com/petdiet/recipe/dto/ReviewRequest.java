package com.petdiet.recipe.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class ReviewRequest {
    @NotNull
    @Min(1) @Max(5)
    private Integer rating;
    @Min(1) @Max(5)
    private Integer starRating;

    private String reviewContent;

    public Integer getRating() {
        return rating != null ? rating : starRating;
    }
}
