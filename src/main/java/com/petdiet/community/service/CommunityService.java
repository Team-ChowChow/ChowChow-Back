package com.petdiet.community.service;

import com.petdiet.auth.entity.User;
import com.petdiet.auth.repository.UserRepository;
import com.petdiet.community.dto.*;
import com.petdiet.community.entity.*;
import com.petdiet.community.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CommunityService {

    private final CommunityPostRepository postRepository;
    private final CommunityCommentRepository commentRepository;
    private final CommunityLikeRepository likeRepository;
    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public Page<PostResponse> getPosts(String category, Pageable pageable) {
        Page<CommunityPost> posts = (category != null)
                ? postRepository.findAllByPostCategoryAndPostStatus(category, "ACTIVE", pageable)
                : postRepository.findAllByPostStatus("ACTIVE", pageable);
        return posts.map(p -> PostResponse.from(p, likeRepository.countByPost(p)));
    }

    @Transactional
    public PostResponse getPost(Integer postId) {
        CommunityPost post = findActivePost(postId);
        post.incrementViewCount();
        return PostResponse.from(post, likeRepository.countByPost(post));
    }

    @Transactional(readOnly = true)
    public Page<PostResponse> getMyPosts(UUID authUuid, Pageable pageable) {
        User user = findUser(authUuid);
        return postRepository.findAllByUserAndPostStatus(user, "ACTIVE", pageable)
                .map(p -> PostResponse.from(p, likeRepository.countByPost(p)));
    }

    @Transactional
    public PostResponse createPost(UUID authUuid, PostRequest req) {
        User user = findUser(authUuid);
        CommunityPost post = postRepository.save(CommunityPost.builder()
                .user(user)
                .postTitle(req.getPostTitle())
                .postContent(req.getPostContent())
                .postImageUrl(req.getPostImageUrl())
                .postCategory(req.getPostCategory())
                .build());
        return PostResponse.from(post, 0L);
    }

    @Transactional
    public PostResponse updatePost(UUID authUuid, Integer postId, PostRequest req) {
        User user = findUser(authUuid);
        CommunityPost post = findOwnedActivePost(postId, user);
        post.update(req.getPostTitle(), req.getPostContent(), req.getPostImageUrl(), req.getPostCategory());
        return PostResponse.from(post, likeRepository.countByPost(post));
    }

    @Transactional
    public void deletePost(UUID authUuid, Integer postId) {
        User user = findUser(authUuid);
        CommunityPost post = findOwnedActivePost(postId, user);
        post.delete();
    }

    @Transactional
    public void toggleLike(UUID authUuid, Integer postId) {
        User user = findUser(authUuid);
        CommunityPost post = findActivePost(postId);
        likeRepository.findByPostAndUser(post, user).ifPresentOrElse(
                likeRepository::delete,
                () -> likeRepository.save(CommunityLike.builder().post(post).user(user).build())
        );
    }

    @Transactional(readOnly = true)
    public List<CommentResponse> getComments(Integer postId) {
        CommunityPost post = findActivePost(postId);
        return commentRepository.findAllByPostAndCommentStatus(post, "ACTIVE").stream()
                .map(CommentResponse::from)
                .toList();
    }

    @Transactional
    public CommentResponse createComment(UUID authUuid, Integer postId, CommentRequest req) {
        User user = findUser(authUuid);
        CommunityPost post = findActivePost(postId);
        CommunityComment comment = commentRepository.save(CommunityComment.builder()
                .post(post)
                .user(user)
                .commentContent(req.getCommentContent())
                .build());
        return CommentResponse.from(comment);
    }

    @Transactional
    public CommentResponse updateComment(UUID authUuid, Integer postId, Integer commentId, CommentRequest req) {
        User user = findUser(authUuid);
        CommunityComment comment = findOwnedActiveComment(commentId, user);
        if (!comment.getPost().getPostId().equals(postId)) {
            throw new IllegalArgumentException("댓글을 찾을 수 없습니다.");
        }
        comment.update(req.getCommentContent());
        return CommentResponse.from(comment);
    }

    @Transactional
    public CommentResponse updateComment(UUID authUuid, Integer commentId, CommentRequest req) {
        User user = findUser(authUuid);
        CommunityComment comment = findOwnedActiveComment(commentId, user);
        comment.update(req.getCommentContent());
        return CommentResponse.from(comment);
    }

    @Transactional
    public void deleteComment(UUID authUuid, Integer postId, Integer commentId) {
        User user = findUser(authUuid);
        CommunityComment comment = findOwnedActiveComment(commentId, user);
        if (!comment.getPost().getPostId().equals(postId)) {
            throw new IllegalArgumentException("댓글을 찾을 수 없습니다.");
        }
        comment.delete();
    }

    @Transactional
    public void deleteComment(UUID authUuid, Integer commentId) {
        User user = findUser(authUuid);
        CommunityComment comment = findOwnedActiveComment(commentId, user);
        comment.delete();
    }

    private User findUser(UUID authUuid) {
        return userRepository.findByAuthUuid(authUuid)
                .orElseThrow(() -> new IllegalStateException("유저를 찾을 수 없습니다."));
    }

    private CommunityPost findActivePost(Integer postId) {
        return postRepository.findByPostIdAndPostStatus(postId, "ACTIVE")
                .orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없습니다."));
    }

    private CommunityPost findOwnedActivePost(Integer postId, User user) {
        CommunityPost post = findActivePost(postId);
        if (!post.getUser().getUserId().equals(user.getUserId())) {
            throw new IllegalArgumentException("게시글을 수정할 권한이 없습니다.");
        }
        return post;
    }

    private CommunityComment findOwnedActiveComment(Integer commentId, User user) {
        CommunityComment comment = commentRepository.findByCommentIdAndCommentStatus(commentId, "ACTIVE")
                .orElseThrow(() -> new IllegalArgumentException("댓글을 찾을 수 없습니다."));
        if (!comment.getUser().getUserId().equals(user.getUserId())) {
            throw new IllegalArgumentException("댓글을 수정할 권한이 없습니다.");
        }
        return comment;
    }
}
