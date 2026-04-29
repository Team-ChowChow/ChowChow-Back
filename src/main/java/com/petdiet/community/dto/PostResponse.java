package com.petdiet.community.dto;

import com.petdiet.community.entity.CommunityPost;
import lombok.Builder;
import lombok.Getter;

import java.time.OffsetDateTime;
import java.util.List;

@Getter
@Builder
public class PostResponse {
    private Integer postId;
    private Integer userId;
    private Integer petId;
    private Integer recipeId;
    private String postTitle;
    private String postContentPreview;
    private String postContent;
    private String postImageUrl;
    private String postCategory;
    private Integer viewCount;
    private long likeCount;
    private Integer commentCount;
    private String postStatus;
    private Boolean likedByMe;
    private List<String> tagNames;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;

    public static PostResponse from(CommunityPost post, long likeCount) {
        return PostResponse.builder()
                .postId(post.getPostId())
                .userId(post.getUser().getUserId())
                .postTitle(post.getPostTitle())
                .postContentPreview(post.getPostContent() == null ? null :
                        (post.getPostContent().length() > 50 ? post.getPostContent().substring(0, 50) + "..." : post.getPostContent()))
                .postContent(post.getPostContent())
                .postImageUrl(post.getPostImageUrl())
                .postCategory(post.getPostCategory())
                .viewCount(post.getViewCount())
                .likeCount(likeCount)
                .commentCount(post.getComments() == null ? 0 : post.getComments().size())
                .postStatus(post.getPostStatus())
                .likedByMe(false)
                .tagNames(List.of())
                .createdAt(post.getCreatedAt())
                .updatedAt(post.getUpdatedAt())
                .build();
    }
}
