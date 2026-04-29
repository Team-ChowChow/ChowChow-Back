package com.petdiet.community.repository;

import com.petdiet.auth.entity.User;
import com.petdiet.community.entity.CommunityLike;
import com.petdiet.community.entity.CommunityPost;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CommunityLikeRepository extends JpaRepository<CommunityLike, Integer> {

    Optional<CommunityLike> findByPostAndUser(CommunityPost post, User user);

    boolean existsByPostAndUser(CommunityPost post, User user);

    long countByPost(CommunityPost post);
}
