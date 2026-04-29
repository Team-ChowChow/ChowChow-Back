package com.petdiet.community.repository;

import com.petdiet.community.entity.CommunityComment;
import com.petdiet.community.entity.CommunityPost;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CommunityCommentRepository extends JpaRepository<CommunityComment, Integer> {

    List<CommunityComment> findAllByPostAndCommentStatus(CommunityPost post, String commentStatus);

    Optional<CommunityComment> findByCommentIdAndCommentStatus(Integer commentId, String commentStatus);
}
