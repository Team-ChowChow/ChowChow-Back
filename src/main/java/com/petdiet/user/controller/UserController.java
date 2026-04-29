package com.petdiet.user.controller;

import com.petdiet.config.SupabasePrincipal;
import com.petdiet.user.dto.*;
import com.petdiet.user.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/me")
    public ResponseEntity<UserProfileResponse> getProfile(
            @AuthenticationPrincipal SupabasePrincipal principal) {
        return ResponseEntity.ok(userService.getProfile(principal.authUuid()));
    }

    @PatchMapping("/me")
    public ResponseEntity<UserProfileResponse> updateProfile(
            @AuthenticationPrincipal SupabasePrincipal principal,
            @RequestBody @Valid UserProfileUpdateRequest request) {
        return ResponseEntity.ok(userService.updateProfile(principal.authUuid(), request));
    }

    @GetMapping("/me/settings")
    public ResponseEntity<UserSettingsResponse> getSettings(
            @AuthenticationPrincipal SupabasePrincipal principal) {
        return ResponseEntity.ok(userService.getSettings(principal.authUuid()));
    }

    @PatchMapping("/me/settings")
    public ResponseEntity<UserSettingsResponse> updateSettings(
            @AuthenticationPrincipal SupabasePrincipal principal,
            @RequestBody UserSettingsUpdateRequest request) {
        return ResponseEntity.ok(userService.updateSettings(principal.authUuid(), request));
    }

    @PatchMapping("/me/profile-image")
    public ResponseEntity<UserProfileResponse> updateProfileImage(
            @AuthenticationPrincipal SupabasePrincipal principal,
            @RequestBody @Valid UserProfileUpdateRequest request) {
        return ResponseEntity.ok(userService.updateProfile(principal.authUuid(), request));
    }

    @GetMapping("/me/status")
    public ResponseEntity<?> getUserStatus(
            @AuthenticationPrincipal SupabasePrincipal principal) {
        UserProfileResponse profile = userService.getProfile(principal.authUuid());
        return ResponseEntity.ok(Map.of(
                "userId", profile.getUserId(),
                "userStatus", profile.getUserStatus(),
                "message", "사용자 상태 조회에 성공했습니다."
        ));
    }

    @DeleteMapping("/me")
    public ResponseEntity<Void> withdraw(
            @AuthenticationPrincipal SupabasePrincipal principal) {
        userService.withdraw(principal.authUuid());
        return ResponseEntity.noContent().build();
    }
}