package com.petdiet.recipe.dto;

import com.petdiet.recipe.entity.RecipeReview;
import lombok.Builder;
import lombok.Getter;

import java.time.OffsetDateTime;

@Getter
@Builder
public class ReviewResponse {
    private Integer reviewId;
    private Integer recipeId;
    private Integer userId;
    private Integer rating;
    private Integer starRating;
    private String userNickname;
    private String reviewContent;
    private OffsetDateTime createdAt;

    public static ReviewResponse from(RecipeReview review) {
        return ReviewResponse.builder()
                .reviewId(review.getReviewId())
                .recipeId(review.getRecipe().getRecipeId())
                .userId(review.getUser().getUserId())
                .rating(review.getRating())
                .starRating(review.getRating())
                .userNickname(review.getUser().getUserNickname())
                .reviewContent(review.getReviewContent())
                .createdAt(review.getCreatedAt())
                .build();
    }
}
