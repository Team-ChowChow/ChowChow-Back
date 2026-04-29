package com.petdiet.user.dto;

import jakarta.validation.constraints.Size;
import lombok.Getter;

@Getter
public class UserProfileUpdateRequest {
    @Size(max = 100)
    private String userName;

    @Size(min = 2, max = 50)
    private String userNickname;

    private String userProfileImg;
    private String userProfileimg;

    public String getUserProfileImg() {
        return userProfileImg != null ? userProfileImg : userProfileimg;
    }
}