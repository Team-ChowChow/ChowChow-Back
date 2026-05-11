package com.petdiet.user.dto;

import lombok.Getter;

@Getter
public class UserSettingsUpdateRequest {
    private Boolean isNotificationEnabled;
    private Boolean isDarkMode;
    private Boolean isSearchHistoryEnabled;
    private Boolean isPersonalInfoAgreed;
}
