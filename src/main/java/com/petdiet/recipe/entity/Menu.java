package com.petdiet.recipe.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.OffsetDateTime;

@Entity
@Table(name = "\"Menus\"")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Menu {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "\"menuId\"")
    private Integer menuId;

    @Column(name = "\"menuName\"", nullable = false, unique = true, length = 200)
    private String menuName;

    @Column(name = "\"menuDescription\"", columnDefinition = "TEXT")
    private String menuDescription;

    @Column(name = "\"petType\"", nullable = false, length = 10)
    private String petType;

    @Column(name = "\"menuCategory\"", length = 30)
    private String menuCategory;

    @Builder.Default
    @Column(name = "\"menuStatus\"", nullable = false, length = 10)
    private String menuStatus = "ACTIVE";

    @CreationTimestamp
    @Column(name = "\"createdAt\"", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "\"updatedAt\"", nullable = false)
    private OffsetDateTime updatedAt;
}
