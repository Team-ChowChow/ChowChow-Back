package com.petdiet.auth.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.OffsetDateTime;

@Entity
@Table(name = "\"AuthAccounts\"")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthAccount {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "\"authId\"")
    private Integer authId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "\"userId\"", nullable = false)
    private User user;

    @Column(name = "\"authProvider\"", nullable = false)
    private String authProvider;

    @Column(name = "\"authEmail\"")
    private String authEmail;

    @Column(name = "\"providerUserId\"")
    private String providerUserId;

    @Builder.Default
    @Column(name = "\"authStatus\"", nullable = false)
    private String authStatus = "ACTIVE";

    @Column(name = "\"authLoginAt\"")
    private OffsetDateTime authLoginAt;

    @CreationTimestamp
    @Column(name = "\"authCreatedAt\"", nullable = false, updatable = false)
    private OffsetDateTime authCreatedAt;

    @UpdateTimestamp
    @Column(name = "\"authUpdatedAt\"", nullable = false)
    private OffsetDateTime authUpdatedAt;

    public void updateLoginAt() {
        this.authLoginAt = OffsetDateTime.now();
    }
}