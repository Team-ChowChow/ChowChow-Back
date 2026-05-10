package com.petdiet.auth.controller;

import com.petdiet.auth.dto.AuthResponse;
import com.petdiet.auth.dto.LoginRequest;
import com.petdiet.auth.dto.SignupRequest;
import com.petdiet.auth.service.AuthService;
import com.petdiet.config.SupabasePrincipal;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/signup")
    public ResponseEntity<AuthResponse> signup(@RequestBody @Valid SignupRequest request) {
        return ResponseEntity.ok(authService.signup(request));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody @Valid LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpServletRequest request) {
        String header = request.getHeader("Authorization");
        if (header != null && header.startsWith("Bearer ")) {
            authService.logout(header.substring(7));
        }
        return ResponseEntity.noContent().build();
    }

    // 소셜 로그인 후 최초 1회 호출 — DB에 유저 동기화
    @PostMapping("/sync")
    public ResponseEntity<AuthResponse> sync(@AuthenticationPrincipal SupabasePrincipal principal) {
        return ResponseEntity.ok(authService.syncSocialUser(principal));
    }

    @GetMapping("/me")
    public ResponseEntity<AuthResponse> me(@AuthenticationPrincipal SupabasePrincipal principal) {
        return ResponseEntity.ok(authService.getMe(principal.authUuid()));
    }

    // 이메일 인증 링크 클릭 콜백
    @GetMapping(value = "/confirm", produces = MediaType.TEXT_HTML_VALUE)
    public ResponseEntity<String> confirm(@RequestParam(required = false) String token) {
        if (token == null || token.isBlank()) {
            return ResponseEntity.badRequest().body(htmlPage("❌ 잘못된 요청", "인증 링크가 올바르지 않습니다.", false));
        }
        try {
            authService.confirmEmail(token);
            return ResponseEntity.ok(htmlPage("✅ 이메일 인증 완료", "앱으로 돌아가서 로그인해주세요.", true));
        } catch (Exception e) {
            return ResponseEntity.ok(htmlPage("❌ 인증 실패", "링크가 만료되었거나 유효하지 않습니다. 다시 회원가입을 시도해주세요.", false));
        }
    }

    private String htmlPage(String title, String message, boolean success) {
        String color = success ? "#4caf50" : "#e53935";
        return """
                <!DOCTYPE html>
                <html lang="ko">
                <head>
                  <meta charset="UTF-8">
                  <meta name="viewport" content="width=device-width, initial-scale=1.0">
                  <title>%s</title>
                  <style>
                    body { font-family: sans-serif; display: flex; justify-content: center;
                           align-items: center; height: 100vh; margin: 0; background: #f5f5f5; }
                    .card { background: white; padding: 40px; border-radius: 12px;
                            text-align: center; box-shadow: 0 2px 12px rgba(0,0,0,0.1); }
                    h2 { color: %s; margin-bottom: 12px; }
                    p  { color: #666; }
                  </style>
                </head>
                <body>
                  <div class="card">
                    <h2>%s</h2>
                    <p>%s</p>
                  </div>
                </body>
                </html>
                """.formatted(title, color, title, message);
    }

    @GetMapping("/check-email")
    public ResponseEntity<?> checkEmail(@RequestParam String authEmail) {
        boolean available = authService.isEmailAvailable(authEmail);
        return ResponseEntity.ok(java.util.Map.of(
                "authEmail", authEmail,
                "available", available,
                "message", available ? "사용 가능한 이메일입니다." : "이미 사용 중인 이메일입니다."
        ));
    }
}
