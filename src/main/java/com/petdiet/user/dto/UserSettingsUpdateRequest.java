package com.petdiet.user.dto;

import lombok.Getter;

@Getter
public class UserSettingsUpdateRequest {
    private Boolean isNotificationEnabled;
    private Boolean isDarkMode;
    private Boolean isDarkmode;
    private Boolean isSearchHistoryEnabled;
    private Boolean isPersonalInfoAgreed;

    public Boolean getIsDarkMode() {
        return isDarkMode != null ? isDarkMode : isDarkmode;
    }
}