package com.petdiet.recipe.entity;

import com.petdiet.auth.entity.User;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.OffsetDateTime;

@Entity
@Table(name = "\"RecipeReviews\"")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RecipeReview {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "\"reviewId\"")
    private Integer reviewId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "\"recipeId\"", nullable = false)
    private Recipe recipe;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "\"userId\"", nullable = false)
    private User user;

    @Column(name = "\"rating\"", nullable = false)
    private Integer rating;

    @Column(name = "\"reviewContent\"", columnDefinition = "TEXT")
    private String reviewContent;

    @CreationTimestamp
    @Column(name = "\"createdAt\"", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "\"updatedAt\"", nullable = false)
    private OffsetDateTime updatedAt;

    public void update(Integer rating, String reviewContent) {
        if (rating != null) this.rating = rating;
        if (reviewContent != null) this.reviewContent = reviewContent;
    }
}
