package com.petdiet.community.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

import java.util.List;

@Getter
public class PostRequest {
    private Integer petId;
    private Integer recipeId;

    @NotBlank
    private String postTitle;

    @NotBlank
    private String postContent;

    private String postImageUrl;
    private String postCategory;
    private String postStatus;
    private List<String> tagNames;
}
