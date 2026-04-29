package com.petdiet.auth.service;

import com.petdiet.auth.dto.AuthResponse;
import com.petdiet.auth.entity.AuthAccount;
import com.petdiet.auth.entity.User;
import com.petdiet.auth.repository.AuthAccountRepository;
import com.petdiet.auth.repository.UserRepository;
import com.petdiet.config.SupabasePrincipal;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

    @Transactional
    public AuthResponse syncGoogleUser(SupabasePrincipal principal) {
        Optional<User> existing = userRepository.findByAuthUuid(principal.authUuid());
        if (existing.isPresent()) {
            User user = existing.get();
            updateLastLogin(user);
            AuthAccount account = authAccountRepository.findByUserAndAuthProvider(user, "GOOGLE").orElse(null);
            return AuthResponse.of(user, account, false);
        }

        User newUser = createUser(principal);
        AuthAccount account = createAuthAccount(newUser, principal);
        log.info("신규 Google 유저 등록: {}", principal.email());
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

    @Transactional(readOnly = true)
    public AuthResponse getMyAccount(UUID authUuid) {
        return getMe(authUuid);
    }

    private User createUser(SupabasePrincipal principal) {
        String nickname = generateNickname(principal.email());
        return userRepository.save(User.builder()
                .authUuid(principal.authUuid())
                .userName(principal.name().isBlank() ? principal.email() : principal.name())
                .userNickname(nickname)
                .userProfileImg(principal.avatarUrl())
                .userStatus("PENDING")
                .build());
    }

    private AuthAccount createAuthAccount(User user, SupabasePrincipal principal) {
        return authAccountRepository.save(AuthAccount.builder()
                .user(user)
                .authProvider("GOOGLE")
                .authEmail(principal.email())
                .providerUserId(principal.authUuid().toString())
                .authStatus("ACTIVE")
                .build());
    }

    private void updateLastLogin(User user) {
        authAccountRepository.findByUserAndAuthProvider(user, "GOOGLE")
                .ifPresent(AuthAccount::updateLoginAt);
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