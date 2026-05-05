package com.petdiet.llm.controller;

import com.petdiet.config.SupabasePrincipal;
import com.petdiet.llm.dto.LlmChatRequest;
import com.petdiet.llm.dto.LlmChatResponse;
import com.petdiet.llm.service.LlmChatService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/llm")
@RequiredArgsConstructor
public class LlmChatController {

    private final LlmChatService llmChatService;

    @PostMapping("/chat")
    public ResponseEntity<LlmChatResponse> chat(
            @AuthenticationPrincipal SupabasePrincipal principal,
            @RequestBody @Valid LlmChatRequest request) {
        return ResponseEntity.ok(llmChatService.chat(
                principal.authUuid(), request.getPrompt(), request.getSystemPrompt()));
    }
}
