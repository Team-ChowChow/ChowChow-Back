package com.petdiet.auth.service;

import com.petdiet.auth.client.EmailConfirmTokenUtil;
import com.petdiet.auth.client.ResendEmailClient;
import com.petdiet.auth.client.SupabaseAuthClient;
import com.petdiet.auth.client.SupabaseAuthClient.SupabaseSignupResult;
import com.petdiet.auth.client.SupabaseAuthClient.SupabaseTokenResult;
import com.petdiet.auth.dto.AuthResponse;
import com.petdiet.auth.dto.LoginRequest;
import com.petdiet.auth.dto.SignupRequest;
import com.petdiet.auth.entity.AuthAccount;
import com.petdiet.auth.entity.User;
import com.petdiet.auth.repository.AuthAccountRepository;
import com.petdiet.auth.repository.UserRepository;
import com.petdiet.config.SupabasePrincipal;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final AuthAccountRepository authAccountRepository;
    private final SupabaseAuthClient supabaseAuthClient;
    private final ResendEmailClient resendEmailClient;
    private final EmailConfirmTokenUtil emailConfirmTokenUtil;

    @Value("${app.base-url}")
    private String appBaseUrl;

    @Transactional
    public AuthResponse signup(SignupRequest req) {
        if (!isEmailAvailable(req.getEmail())) {
            throw new IllegalArgumentException("이미 사용 중인 이메일입니다.");
        }

        SupabaseSignupResult result = supabaseAuthClient.signup(req.getEmail(), req.getPassword());

        String token = emailConfirmTokenUtil.generate(result.authUuid(), req.getEmail(), req.getUserName());
        String confirmUrl = appBaseUrl + "/api/auth/confirm?token=" + token;
        resendEmailClient.sendConfirmationEmail(req.getEmail(), confirmUrl);

        log.info("인증 이메일 발송: {}", req.getEmail());

        return AuthResponse.builder()
                .message("가입 확인 이메일을 발송했습니다. 받은 편지함을 확인해주세요.")
                .build();
    }

    @Transactional
    public AuthResponse login(LoginRequest req) {
        SupabaseTokenResult result = supabaseAuthClient.login(req.getEmail(), req.getPassword());

        User user = userRepository.findByAuthUuid(result.authUuid())
                .orElseThrow(() -> new IllegalStateException("이메일 인증이 완료되지 않았습니다. 받은 편지함을 확인해주세요."));

        AuthAccount account = authAccountRepository.findByUserAndAuthProvider(user, "EMAIL").orElse(null);
        if (account != null) account.updateLoginAt();

        return AuthResponse.of(user, account, false).toBuilder()
                .accessToken(result.accessToken())
                .refreshToken(result.refreshToken())
                .build();
    }

    @Transactional
    public AuthResponse confirmEmail(String token) {
        Claims claims = emailConfirmTokenUtil.verify(token);
        UUID authUuid = UUID.fromString(claims.getSubject());
        String email = claims.get("email", String.class);
        String userName = claims.get("userName", String.class);

        // 이미 인증 완료된 경우 (중복 클릭) — 기존 유저 반환
        Optional<User> existing = userRepository.findByAuthUuid(authUuid);
        if (existing.isPresent()) {
            AuthAccount account = authAccountRepository.findByUserAndAuthProvider(existing.get(), "EMAIL").orElse(null);
            return AuthResponse.of(existing.get(), account, false);
        }

        String nickname = generateNickname(email);
        User user = userRepository.save(User.builder()
                .authUuid(authUuid)
                .userName(userName != null ? userName : email)
                .userNickname(nickname)
                .userStatus("ACTIVE")
                .build());

        AuthAccount account = authAccountRepository.save(AuthAccount.builder()
                .user(user)
                .authProvider("EMAIL")
                .authEmail(email)
                .providerUserId(authUuid.toString())
                .authStatus("ACTIVE")
                .build());

        log.info("이메일 인증 완료 — DB 등록: {}", email);
        return AuthResponse.of(user, account, true);
    }

    @Transactional
    public AuthResponse syncSocialUser(SupabasePrincipal principal) {
        String provider = principal.provider().toUpperCase();

        Optional<User> existing = userRepository.findByAuthUuid(principal.authUuid());
        if (existing.isPresent()) {
            User user = existing.get();
            AuthAccount account = authAccountRepository.findByUserAndAuthProvider(user, provider).orElse(null);
            if (account != null) account.updateLoginAt();
            return AuthResponse.of(user, account, false);
        }

        User newUser = createUser(principal);
        AuthAccount account = createAuthAccount(newUser, principal, provider);
        log.info("신규 소셜 유저 등록 [provider={}]: {}", provider, principal.email());
        return AuthResponse.of(newUser, account, true);
    }

    @Transactional(readOnly = true)
    public AuthResponse getMe(UUID authUuid) {
        User user = userRepository.findByAuthUuid(authUuid)
                .orElseThrow(() -> new IllegalStateException("등록되지 않은 유저입니다."));
        AuthAccount account = authAccountRepository.findFirstByUserOrderByAuthCreatedAtAsc(user).orElse(null);
        return AuthResponse.of(user, account, false);
    }

    @Transactional(readOnly = true)
    public boolean isEmailAvailable(String authEmail) {
        return !authAccountRepository.existsByAuthEmail(authEmail);
    }

    public void logout(String accessToken) {
        supabaseAuthClient.logout(accessToken);
    }

    private User createUser(SupabasePrincipal principal) {
        String nickname = generateNickname(principal.email());
        return userRepository.save(User.builder()
                .authUuid(principal.authUuid())
                .userName(principal.name().isBlank() ? principal.email() : principal.name())
                .userNickname(nickname)
                .userProfileImg(principal.avatarUrl())
                .userStatus("ACTIVE")
                .build());
    }

    private AuthAccount createAuthAccount(User user, SupabasePrincipal principal, String provider) {
        return authAccountRepository.save(AuthAccount.builder()
                .user(user)
                .authProvider(provider)
                .authEmail(principal.email())
                .providerUserId(principal.authUuid().toString())
                .authStatus("ACTIVE")
                .build());
    }

    private String generateNickname(String email) {
        String base = email.contains("@") ? email.split("@")[0] : email;
        String candidate = base;
        int suffix = 1;
        while (userRepository.existsByUserNickname(candidate)) {
            candidate = base + suffix++;
        }
        return candidate;
    }
}
