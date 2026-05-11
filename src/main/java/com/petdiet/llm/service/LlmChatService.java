package com.petdiet.llm.service;

import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;
import com.petdiet.auth.repository.UserRepository;
import com.petdiet.llm.dto.LlmChatResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Service
public class LlmChatService {

    private final UserRepository userRepository;
    private final ObjectMapper objectMapper;
    private final WebClient webClient;
    private final String model;
    private final int maxTokens;

    public LlmChatService(
            UserRepository userRepository,
            ObjectMapper objectMapper,
            @Value("${openai.api-key}") String apiKey,
            @Value("${openai.base-url:https://api.openai.com}") String baseUrl,
            @Value("${openai.model:gpt-4o}") String model,
            @Value("${openai.max-tokens:2048}") int maxTokens) {
        this.userRepository = userRepository;
        this.objectMapper = objectMapper;
        this.model = model;
        this.maxTokens = maxTokens;
        this.webClient = WebClient.builder()
                .baseUrl(baseUrl)
                .defaultHeader("Authorization", "Bearer " + apiKey)
                .defaultHeader("Content-Type", "application/json")
                .build();
    }

    public LlmChatResponse chat(UUID authUuid, String prompt, String systemPrompt) {
        userRepository.findByAuthUuid(authUuid)
                .orElseThrow(() -> new IllegalStateException("유저를 찾을 수 없습니다."));
        return callOpenAi(prompt, systemPrompt);
    }

    private LlmChatResponse callOpenAi(String prompt, String systemPrompt) {
        List<Map<String, String>> messages = new ArrayList<>();
        if (systemPrompt != null && !systemPrompt.isBlank()) {
            messages.add(Map.of("role", "system", "content", systemPrompt));
        }
        messages.add(Map.of("role", "user", "content", prompt));

        Map<String, Object> body = Map.of(
                "model", model,
                "max_tokens", maxTokens,
                "messages", messages
        );

        try {
            String responseBody = webClient.post()
                    .uri("/v1/chat/completions")
                    .bodyValue(body)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            JsonNode root = objectMapper.readTree(responseBody);
            String content = root.path("choices").get(0).path("message").path("content").stringValue();
            return LlmChatResponse.builder().answer(content).build();
        } catch (Exception e) {
            log.error("OpenAI LLM 채팅 API 호출 실패", e);
            throw new RuntimeException("LLM 응답 생성 중 오류가 발생했습니다.", e);
        }
    }
}
