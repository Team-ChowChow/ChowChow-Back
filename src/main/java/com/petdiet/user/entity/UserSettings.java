package com.petdiet.user.entity;

import com.petdiet.auth.entity.User;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.OffsetDateTime;

@Entity
@Table(name = "\"UserSettings\"")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserSettings {

    @Id
    @Column(name = "\"userId\"")
    private Integer userId;

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "\"userId\"")
    private User user;

    @Builder.Default
    @Column(name = "\"isNotificationEnabled\"", nullable = false)
    private Boolean isNotificationEnabled = true;

    @Builder.Default
    @Column(name = "\"isDarkMode\"", nullable = false)
    private Boolean isDarkMode = false;

    @Builder.Default
    @Column(name = "\"isSearchHistoryEnabled\"", nullable = false)
    private Boolean isSearchHistoryEnabled = true;

    @Builder.Default
    @Column(name = "\"isPersonalInfoAgreed\"", nullable = false)
    private Boolean isPersonalInfoAgreed = false;

    @Column(name = "\"personalInfoAgreedAt\"")
    private OffsetDateTime personalInfoAgreedAt;

    @CreationTimestamp
    @Column(name = "\"createdAt\"", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "\"updatedAt\"", nullable = false)
    private OffsetDateTime updatedAt;

    public void update(Boolean isNotificationEnabled, Boolean isDarkMode, Boolean isSearchHistoryEnabled) {
        if (isNotificationEnabled != null) this.isNotificationEnabled = isNotificationEnabled;
        if (isDarkMode != null) this.isDarkMode = isDarkMode;
        if (isSearchHistoryEnabled != null) this.isSearchHistoryEnabled = isSearchHistoryEnabled;
    }
}