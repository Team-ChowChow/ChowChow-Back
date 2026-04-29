package com.petdiet.community.entity;

import com.petdiet.auth.entity.User;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "\"CommunityPosts\"")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommunityPost {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "\"postId\"")
    private Integer postId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "\"userId\"", nullable = false)
    private User user;

    @Column(name = "\"postTitle\"", nullable = false)
    private String postTitle;

    @Column(name = "\"postContent\"", nullable = false, columnDefinition = "TEXT")
    private String postContent;

    @Column(name = "\"postImageUrl\"")
    private String postImageUrl;

    @Column(name = "\"postCategory\"")
    private String postCategory;

    @Builder.Default
    @Column(name = "\"viewCount\"", nullable = false)
    private Integer viewCount = 0;

    @Builder.Default
    @Column(name = "\"postStatus\"", nullable = false)
    private String postStatus = "ACTIVE";

    @CreationTimestamp
    @Column(name = "\"createdAt\"", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "\"updatedAt\"", nullable = false)
    private OffsetDateTime updatedAt;

    @Builder.Default
    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CommunityComment> comments = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CommunityLike> likes = new ArrayList<>();

    public void update(String postTitle, String postContent, String postImageUrl, String postCategory) {
        if (postTitle != null) this.postTitle = postTitle;
        if (postContent != null) this.postContent = postContent;
        if (postImageUrl != null) this.postImageUrl = postImageUrl;
        if (postCategory != null) this.postCategory = postCategory;
    }

    public void delete() {
        this.postStatus = "DELETED";
    }

    public void incrementViewCount() {
        this.viewCount++;
    }
}
