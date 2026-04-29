package com.petdiet.community.controller;

import com.petdiet.community.dto.*;
import com.petdiet.community.service.CommunityService;
import com.petdiet.config.SupabasePrincipal;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/community")
@RequiredArgsConstructor
public class CommunityController {

    private final CommunityService communityService;

    @GetMapping("/posts")
    public ResponseEntity<Page<PostResponse>> getPosts(
            @RequestParam(required = false) String category,
            @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(communityService.getPosts(category, pageable));
    }

    @GetMapping("/posts/{postId}")
    public ResponseEntity<PostResponse> getPost(@PathVariable Integer postId) {
        return ResponseEntity.ok(communityService.getPost(postId));
    }

    @GetMapping("/posts/my")
    public ResponseEntity<Page<PostResponse>> getMyPosts(
            @AuthenticationPrincipal SupabasePrincipal principal,
            @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(communityService.getMyPosts(principal.authUuid(), pageable));
    }

    @PostMapping("/posts")
    public ResponseEntity<PostResponse> createPost(
            @AuthenticationPrincipal SupabasePrincipal principal,
            @RequestBody @Valid PostRequest request) {
        return ResponseEntity.ok(communityService.createPost(principal.authUuid(), request));
    }

    @PatchMapping("/posts/{postId}")
    public ResponseEntity<PostResponse> updatePost(
            @AuthenticationPrincipal SupabasePrincipal principal,
            @PathVariable Integer postId,
            @RequestBody PostRequest request) {
        return ResponseEntity.ok(communityService.updatePost(principal.authUuid(), postId, request));
    }

    @DeleteMapping("/posts/{postId}")
    public ResponseEntity<Void> deletePost(
            @AuthenticationPrincipal SupabasePrincipal principal,
            @PathVariable Integer postId) {
        communityService.deletePost(principal.authUuid(), postId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/posts/{postId}/like")
    public ResponseEntity<Void> toggleLike(
            @AuthenticationPrincipal SupabasePrincipal principal,
            @PathVariable Integer postId) {
        communityService.toggleLike(principal.authUuid(), postId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/posts/{postId}/likes")
    public ResponseEntity<Void> addLike(
            @AuthenticationPrincipal SupabasePrincipal principal,
            @PathVariable Integer postId) {
        communityService.toggleLike(principal.authUuid(), postId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/posts/{postId}/likes")
    public ResponseEntity<Void> cancelLike(
            @AuthenticationPrincipal SupabasePrincipal principal,
            @PathVariable Integer postId) {
        communityService.toggleLike(principal.authUuid(), postId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/posts/{postId}/comments")
    public ResponseEntity<List<CommentResponse>> getComments(@PathVariable Integer postId) {
        return ResponseEntity.ok(communityService.getComments(postId));
    }

    @PostMapping("/posts/{postId}/comments")
    public ResponseEntity<CommentResponse> createComment(
            @AuthenticationPrincipal SupabasePrincipal principal,
            @PathVariable Integer postId,
            @RequestBody @Valid CommentRequest request) {
        return ResponseEntity.ok(communityService.createComment(principal.authUuid(), postId, request));
    }

    @PatchMapping("/posts/{postId}/comments/{commentId}")
    public ResponseEntity<CommentResponse> updateComment(
            @AuthenticationPrincipal SupabasePrincipal principal,
            @PathVariable Integer postId,
            @PathVariable Integer commentId,
            @RequestBody @Valid CommentRequest request) {
        return ResponseEntity.ok(communityService.updateComment(principal.authUuid(), postId, commentId, request));
    }

    @PatchMapping("/comments/{commentId}")
    public ResponseEntity<CommentResponse> updateCommentById(
            @AuthenticationPrincipal SupabasePrincipal principal,
            @PathVariable Integer commentId,
            @RequestBody @Valid CommentRequest request) {
        return ResponseEntity.ok(communityService.updateComment(principal.authUuid(), commentId, request));
    }

    @DeleteMapping("/posts/{postId}/comments/{commentId}")
    public ResponseEntity<Void> deleteComment(
            @AuthenticationPrincipal SupabasePrincipal principal,
            @PathVariable Integer postId,
            @PathVariable Integer commentId) {
        communityService.deleteComment(principal.authUuid(), postId, commentId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/comments/{commentId}")
    public ResponseEntity<Void> deleteCommentById(
            @AuthenticationPrincipal SupabasePrincipal principal,
            @PathVariable Integer commentId) {
        communityService.deleteComment(principal.authUuid(), commentId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/posts/{postId}/tags")
    public ResponseEntity<List<String>> getPostTags(@PathVariable Integer postId) {
        return ResponseEntity.ok(List.of());
    }
}
