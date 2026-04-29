package com.petdiet.user.dto;

import com.petdiet.auth.entity.User;
import lombok.Builder;
import lombok.Getter;

import java.time.OffsetDateTime;

@Getter
@Builder
public class UserProfileResponse {
    private Integer userId;
    private String userName;
    private String userNickname;
    private String userProfileImg;
    private String userStatus;
    private OffsetDateTime userCreatedAt;
    private OffsetDateTime userUpdatedAt;

    public static UserProfileResponse from(User user) {
        return UserProfileResponse.builder()
                .userId(user.getUserId())
                .userName(user.getUserName())
                .userNickname(user.getUserNickname())
                .userProfileImg(user.getUserProfileImg())
                .userStatus(user.getUserStatus())
                .userCreatedAt(user.getUserCreatedAt())
                .userUpdatedAt(user.getUserUpdatedAt())
                .build();
    }
}