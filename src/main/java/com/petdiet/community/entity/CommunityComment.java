package com.petdiet.community.entity;

import com.petdiet.auth.entity.User;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.OffsetDateTime;

@Entity
@Table(name = "\"CommunityComments\"")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommunityComment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "\"commentId\"")
    private Integer commentId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "\"postId\"", nullable = false)
    private CommunityPost post;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "\"userId\"", nullable = false)
    private User user;

    @Column(name = "\"commentContent\"", nullable = false, columnDefinition = "TEXT")
    private String commentContent;

    @Builder.Default
    @Column(name = "\"commentStatus\"", nullable = false)
    private String commentStatus = "ACTIVE";

    @CreationTimestamp
    @Column(name = "\"createdAt\"", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "\"updatedAt\"", nullable = false)
    private OffsetDateTime updatedAt;

    public void update(String commentContent) {
        if (commentContent != null) this.commentContent = commentContent;
    }

    public void delete() {
        this.commentStatus = "DELETED";
    }
}
