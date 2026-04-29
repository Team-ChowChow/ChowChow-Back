package com.petdiet.notification.entity;

import com.petdiet.auth.entity.User;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.OffsetDateTime;

@Entity
@Table(name = "\"Notifications\"")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "\"notificationId\"")
    private Integer notificationId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "\"userId\"", nullable = false)
    private User user;

    @Column(name = "\"notificationType\"", nullable = false)
    private String notificationType;

    @Column(name = "\"notificationTitle\"", nullable = false)
    private String notificationTitle;

    @Column(name = "\"notificationContent\"")
    private String notificationContent;

    @Column(name = "\"relatedId\"")
    private Integer relatedId;

    @Builder.Default
    @Column(name = "\"isRead\"", nullable = false)
    private Boolean isRead = false;

    @CreationTimestamp
    @Column(name = "\"createdAt\"", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    public void markAsRead() {
        this.isRead = true;
    }
}
