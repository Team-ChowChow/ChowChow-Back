package com.petdiet.llm.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class LlmChatResponse {
    private String answer;
}
