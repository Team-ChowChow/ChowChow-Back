package com.petdiet.user.service;

import com.petdiet.auth.entity.User;
import com.petdiet.auth.repository.UserRepository;
import com.petdiet.user.dto.*;
import com.petdiet.user.entity.UserSettings;
import com.petdiet.user.repository.UserSettingsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserSettingsRepository userSettingsRepository;

    @Transactional(readOnly = true)
    public UserProfileResponse getProfile(UUID authUuid) {
        return UserProfileResponse.from(findUser(authUuid));
    }

    @Transactional
    public UserProfileResponse updateProfile(UUID authUuid, UserProfileUpdateRequest req) {
        User user = findUser(authUuid);
        if (req.getUserNickname() != null && !req.getUserNickname().equals(user.getUserNickname())) {
            if (userRepository.existsByUserNickname(req.getUserNickname())) {
                throw new IllegalArgumentException("이미 사용 중인 닉네임입니다.");
            }
        }
        user.updateProfile(
            req.getUserName() != null ? req.getUserName() : user.getUserName(),
            req.getUserProfileImg() != null ? req.getUserProfileImg() : user.getUserProfileImg()
        );
        if ("PENDING".equals(user.getUserStatus()) && req.getUserNickname() != null) {
            user.activate();
        }
        return UserProfileResponse.from(userRepository.save(user));
    }

    @Transactional(readOnly = true)
    public UserSettingsResponse getSettings(UUID authUuid) {
        User user = findUser(authUuid);
        UserSettings settings = userSettingsRepository.findById(user.getUserId())
                .orElseGet(() -> createDefaultSettings(user));
        return UserSettingsResponse.from(settings);
    }

    @Transactional
    public UserSettingsResponse updateSettings(UUID authUuid, UserSettingsUpdateRequest req) {
        User user = findUser(authUuid);
        UserSettings settings = userSettingsRepository.findById(user.getUserId())
                .orElseGet(() -> createDefaultSettings(user));
        settings.update(req.getIsNotificationEnabled(), req.getIsDarkMode(), req.getIsSearchHistoryEnabled());
        return UserSettingsResponse.from(userSettingsRepository.save(settings));
    }

    @Transactional
    public void withdraw(UUID authUuid) {
        User user = findUser(authUuid);
        user.updateProfile(user.getUserName(), user.getUserProfileImg());
    }

    private User findUser(UUID authUuid) {
        return userRepository.findByAuthUuid(authUuid)
                .orElseThrow(() -> new IllegalStateException("유저를 찾을 수 없습니다."));
    }

    private UserSettings createDefaultSettings(User user) {
        return userSettingsRepository.save(UserSettings.builder()
                .user(user)
                .build());
    }
}