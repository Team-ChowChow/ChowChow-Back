package com.petdiet.recipe.entity;

import com.petdiet.auth.entity.User;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.OffsetDateTime;

@Entity
@Table(name = "\"RecipeBookmarks\"",
        uniqueConstraints = @UniqueConstraint(columnNames = {"\"recipeId\"", "\"userId\""}))
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RecipeBookmark {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "\"bookmarkId\"")
    private Integer bookmarkId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "\"recipeId\"", nullable = false)
    private Recipe recipe;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "\"userId\"", nullable = false)
    private User user;

    @CreationTimestamp
    @Column(name = "\"createdAt\"", nullable = false, updatable = false)
    private OffsetDateTime createdAt;
}
