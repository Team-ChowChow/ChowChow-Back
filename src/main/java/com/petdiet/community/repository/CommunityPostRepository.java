package com.petdiet.community.repository;

import com.petdiet.auth.entity.User;
import com.petdiet.community.entity.CommunityPost;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CommunityPostRepository extends JpaRepository<CommunityPost, Integer> {

    Page<CommunityPost> findAllByPostStatus(String postStatus, Pageable pageable);

    Page<CommunityPost> findAllByPostCategoryAndPostStatus(String postCategory, String postStatus, Pageable pageable);

    Page<CommunityPost> findAllByUserAndPostStatus(User user, String postStatus, Pageable pageable);

    Optional<CommunityPost> findByPostIdAndPostStatus(Integer postId, String postStatus);
}
