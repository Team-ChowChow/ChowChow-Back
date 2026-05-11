package com.petdiet.community.service;

import com.petdiet.community.dto.CommentRequest;
import com.petdiet.community.dto.CommentResponse;
import com.petdiet.community.dto.PostRequest;
import com.petdiet.community.dto.PostResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CommunityService {

    public Page<PostResponse> getPosts(String category, Pageable pageable) {
        throw new UnsupportedOperationException("미구현");
    }

    public PostResponse getPost(Integer postId) {
        throw new UnsupportedOperationException("미구현");
    }

    public Page<PostResponse> getMyPosts(UUID authUuid, Pageable pageable) {
        throw new UnsupportedOperationException("미구현");
    }

    public PostResponse createPost(UUID authUuid, PostRequest req) {
        throw new UnsupportedOperationException("미구현");
    }

    public PostResponse updatePost(UUID authUuid, Integer postId, PostRequest req) {
        throw new UnsupportedOperationException("미구현");
    }

    public void deletePost(UUID authUuid, Integer postId) {
        throw new UnsupportedOperationException("미구현");
    }

    public void toggleLike(UUID authUuid, Integer postId) {
        throw new UnsupportedOperationException("미구현");
    }

    public List<CommentResponse> getComments(Integer postId) {
        throw new UnsupportedOperationException("미구현");
    }

    public CommentResponse createComment(UUID authUuid, Integer postId, CommentRequest req) {
        throw new UnsupportedOperationException("미구현");
    }

    public CommentResponse updateComment(UUID authUuid, Integer postId, Integer commentId, CommentRequest req) {
        throw new UnsupportedOperationException("미구현");
    }

    public CommentResponse updateComment(UUID authUuid, Integer commentId, CommentRequest req) {
        throw new UnsupportedOperationException("미구현");
    }

    public void deleteComment(UUID authUuid, Integer postId, Integer commentId) {
        throw new UnsupportedOperationException("미구현");
    }

    public void deleteComment(UUID authUuid, Integer commentId) {
        throw new UnsupportedOperationException("미구현");
    }
}
