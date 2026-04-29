package com.petdiet.user.dto;

import com.petdiet.user.entity.UserSettings;
import lombok.Builder;
import lombok.Getter;

import java.time.OffsetDateTime;

@Getter
@Builder
public class UserSettingsResponse {
    private Integer userId;
    private Boolean isNotificationEnabled;
    private Boolean isDarkMode;
    private Boolean isSearchHistoryEnabled;
    private Boolean isPersonalInfoAgreed;
    private OffsetDateTime personalInfoAgreedAt;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;

    public static UserSettingsResponse from(UserSettings settings) {
        return UserSettingsResponse.builder()
                .userId(settings.getUser().getUserId())
                .isNotificationEnabled(settings.getIsNotificationEnabled())
                .isDarkMode(settings.getIsDarkMode())
                .isSearchHistoryEnabled(settings.getIsSearchHistoryEnabled())
                .isPersonalInfoAgreed(settings.getIsPersonalInfoAgreed())
                .personalInfoAgreedAt(settings.getPersonalInfoAgreedAt())
                .createdAt(settings.getCreatedAt())
                .updatedAt(settings.getUpdatedAt())
                .build();
    }
}