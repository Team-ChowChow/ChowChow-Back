package com.petdiet.llm.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class LlmChatRequest {

    @NotBlank
    private String prompt;

    private String systemPrompt;
}
