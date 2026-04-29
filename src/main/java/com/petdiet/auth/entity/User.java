package com.petdiet.auth.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "\"Users\"")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "\"userId\"")
    private Integer userId;

    @Column(name = "\"authUuid\"", nullable = false, unique = true, columnDefinition = "uuid")
    private UUID authUuid;

    @Column(name = "\"userName\"", nullable = false)
    private String userName;

    @Column(name = "\"userNickname\"", nullable = false, unique = true)
    private String userNickname;

    @Column(name = "\"userProfileImg\"")
    private String userProfileImg;

    @Builder.Default
    @Column(name = "\"userStatus\"", nullable = false)
    private String userStatus = "PENDING";

    @CreationTimestamp
    @Column(name = "\"userCreatedAt\"", nullable = false, updatable = false)
    private OffsetDateTime userCreatedAt;

    @UpdateTimestamp
    @Column(name = "\"userUpdatedAt\"", nullable = false)
    private OffsetDateTime userUpdatedAt;

    public void updateProfile(String userName, String userProfileImg) {
        this.userName = userName;
        this.userProfileImg = userProfileImg;
    }

    public void activate() {
        this.userStatus = "ACTIVE";
    }
}