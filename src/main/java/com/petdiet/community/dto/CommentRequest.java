package com.petdiet.community.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class CommentRequest {
    private Integer parentCommentId;

    @NotBlank
    private String commentContent;

    private String commentStatus;
}
