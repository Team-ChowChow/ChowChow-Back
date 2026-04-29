package com.petdiet.auth.dto;

import com.petdiet.auth.entity.User;
import com.petdiet.auth.entity.AuthAccount;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class AuthResponse {
    private Integer userId;
    private Integer authId;
    private String userName;
    private String userNickname;
    private String userProfileImg;
    private String authEmail;
    private String authProvider;
    private String userStatus;
    private String authStatus;
    private boolean isNewUser;
    private String message;

    public static AuthResponse of(User user, boolean isNewUser) {
        return of(user, null, isNewUser);
    }

    public static AuthResponse of(User user, AuthAccount account, boolean isNewUser) {
        return AuthResponse.builder()
                .userId(user.getUserId())
                .authId(account != null ? account.getAuthId() : null)
                .userName(user.getUserName())
                .userNickname(user.getUserNickname())
                .userProfileImg(user.getUserProfileImg())
                .authEmail(account != null ? account.getAuthEmail() : null)
                .authProvider(account != null ? account.getAuthProvider() : null)
                .userStatus(user.getUserStatus())
                .authStatus(account != null ? account.getAuthStatus() : null)
                .isNewUser(isNewUser)
                .message(isNewUser ? "회원가입이 완료되었습니다." : "로그인에 성공했습니다.")
                .build();
    }
}