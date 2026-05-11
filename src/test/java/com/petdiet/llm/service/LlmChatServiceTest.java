package com.petdiet.llm.service;

import tools.jackson.databind.ObjectMapper;
import com.petdiet.auth.entity.User;
import com.petdiet.auth.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class LlmChatServiceTest {

    @Mock
    UserRepository userRepository;

    LlmChatService service;

    @BeforeEach
    void setUp() {
        service = new LlmChatService(
                userRepository, new ObjectMapper(),
                "test-api-key", "https://api.openai.com", "gpt-4o", 2048);
    }

    @Test
    @DisplayName("존재하지 않는 유저로 채팅 요청 시 예외 발생")
    void chat_throwsWhenUserNotFound() {
        given(userRepository.findByAuthUuid(any())).willReturn(Optional.empty());

        assertThatThrownBy(() -> service.chat(UUID.randomUUID(), "테스트 프롬프트", null))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("유저를 찾을 수 없습니다");
    }

    @Test
    @DisplayName("존재하는 유저는 유저 검증을 통과한다")
    void chat_passesUserValidationWhenUserExists() {
        User user = User.builder()
                .authUuid(UUID.randomUUID())
                .userName("테스터")
                .userNickname("tester1")
                .build();
        given(userRepository.findByAuthUuid(any())).willReturn(Optional.of(user));

        // OpenAI 호출 시 RuntimeException 발생 — 유저 검증은 통과했음을 확인
        assertThatThrownBy(() -> service.chat(UUID.randomUUID(), "테스트", null))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("LLM 응답 생성 중 오류");
    }
}
