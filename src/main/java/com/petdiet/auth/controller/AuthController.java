package com.petdiet.auth.controller;

import com.petdiet.auth.dto.AuthResponse;
import com.petdiet.auth.service.AuthService;
import com.petdiet.config.SupabasePrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    // 로그인 후 최초 1회 호출 — DB에 유저 동기화
    @PostMapping("/sync")
    public ResponseEntity<AuthResponse> sync(@AuthenticationPrincipal SupabasePrincipal principal) {
        return ResponseEntity.ok(authService.syncGoogleUser(principal));
    }

    @PostMapping("/signup")
    public ResponseEntity<?> signup() {
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED)
                .body(Map.of("message", "회원가입 API는 아직 구현 중입니다."));
    }

    @PostMapping("/login")
    public ResponseEntity<?> login() {
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED)
                .body(Map.of("message", "이메일 로그인 API는 아직 구현 중입니다."));
    }

    @PostMapping("/oauth/login")
    public ResponseEntity<?> oauthLogin() {
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED)
                .body(Map.of("message", "소셜 로그인 API는 아직 구현 중입니다."));
    }

    @GetMapping("/check-email")
    public ResponseEntity<?> checkEmail(@RequestParam String authEmail) {
        boolean available = authService.isEmailAvailable(authEmail);
        return ResponseEntity.ok(Map.of(
                "authEmail", authEmail,
                "available", available,
                "message", available ? "사용 가능한 이메일입니다." : "이미 사용 중인 이메일입니다."
        ));
    }

    @PostMapping("/email-verifications/request")
    public ResponseEntity<?> requestEmailVerification() {
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED)
                .body(Map.of("message", "이메일 인증 요청 API는 아직 구현 중입니다."));
    }

    @PostMapping("/email-verifications/confirm")
    public ResponseEntity<?> confirmEmailVerification() {
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED)
                .body(Map.of("message", "이메일 인증 확인 API는 아직 구현 중입니다."));
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout() {
        return ResponseEntity.ok(Map.of("message", "로그아웃이 완료되었습니다."));
    }

    // 현재 로그인 유저 정보 조회
    @GetMapping("/me")
    public ResponseEntity<AuthResponse> me(@AuthenticationPrincipal SupabasePrincipal principal) {
        return ResponseEntity.ok(authService.getMe(principal.authUuid()));
    }

    @GetMapping("/me/account")
    public ResponseEntity<AuthResponse> myAccount(@AuthenticationPrincipal SupabasePrincipal principal) {
        return ResponseEntity.ok(authService.getMyAccount(principal.authUuid()));
    }
}