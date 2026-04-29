package com.petdiet.ai.diet.service;

import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;
import com.petdiet.ai.diet.dto.DietRecommendResponse;
import com.petdiet.auth.entity.User;
import com.petdiet.auth.repository.UserRepository;
import com.petdiet.pet.entity.UserPet;
import com.petdiet.pet.repository.UserPetRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class DietRecommendService {

    private final UserRepository userRepository;
    private final UserPetRepository userPetRepository;
    private final ObjectMapper objectMapper;

    @Value("${openai.api-key}")
    private String openAiApiKey;

    @Value("${openai.model:gpt-4o}")
    private String model;

    @Value("${openai.max-tokens:2048}")
    private int maxTokens;

    @Transactional(readOnly = true)
    public DietRecommendResponse recommend(UUID authUuid, Integer petId, String userNotes) {
        User user = userRepository.findByAuthUuid(authUuid)
                .orElseThrow(() -> new IllegalStateException("유저를 찾을 수 없습니다."));
        UserPet pet = userPetRepository.findByPetIdAndUser(petId, user)
                .orElseThrow(() -> new IllegalArgumentException("반려동물을 찾을 수 없습니다."));

        String prompt = buildPrompt(pet, userNotes);
        return callOpenAi(prompt);
    }

    private String buildPrompt(UserPet pet, String userNotes) {
        int ageMonths = 0;
        if (pet.getPetBirthdate() != null) {
            LocalDate now = LocalDate.now();
            ageMonths = (now.getYear() - pet.getPetBirthdate().getYear()) * 12
                    + (now.getMonthValue() - pet.getPetBirthdate().getMonthValue());
        }

        List<Integer> allergyIds = pet.getAllergies().stream()
                .map(a -> a.getAllergyId())
                .toList();
        List<Integer> diseaseIds = pet.getDiseases().stream()
                .map(d -> d.getDiseaseId())
                .toList();

        StringBuilder sb = new StringBuilder();
        sb.append("당신은 반려동물 영양 전문가입니다. 다음 반려동물 정보를 바탕으로 안전한 홈메이드 식단을 추천해주세요.\n\n");
        sb.append("## 반려동물 정보\n");
        sb.append("- 종류: ").append(pet.getPetType()).append("\n");
        sb.append("- 이름: ").append(pet.getPetName()).append("\n");
        if (ageMonths > 0) {
            sb.append("- 나이: ").append(ageMonths / 12).append("년 ").append(ageMonths % 12).append("개월\n");
        }
        if (pet.getPetWeight() != null) {
            sb.append("- 체중: ").append(pet.getPetWeight()).append("kg\n");
        }
        if (pet.getIsNeutered() != null) {
            sb.append("- 중성화: ").append(pet.getIsNeutered() ? "예" : "아니오").append("\n");
        }
        if (!allergyIds.isEmpty()) {
            sb.append("- 알레르기 ID 목록: ").append(allergyIds).append(" (해당 알레르기 식재료 제외)\n");
        }
        if (!diseaseIds.isEmpty()) {
            sb.append("- 질병 ID 목록: ").append(diseaseIds).append(" (해당 질병에 맞는 식단 구성)\n");
        }
        if (userNotes != null && !userNotes.isBlank()) {
            sb.append("- 사용자 요청: ").append(userNotes).append("\n");
        }
        sb.append("\n## 응답 형식 (JSON만 반환, 마크다운 코드블록 없이)\n");
        sb.append("""
                {
                  "title": "식단 제목",
                  "description": "식단 설명 (1~2줄)",
                  "ingredients": [
                    {"name": "재료명", "amount": "용량"}
                  ],
                  "steps": ["조리 단계 1", "조리 단계 2"],
                  "feedingAmount": "하루 급여량 안내",
                  "warnings": ["주의사항 1", "주의사항 2"]
                }
                """);
        return sb.toString();
    }

    private DietRecommendResponse callOpenAi(String prompt) {
        WebClient client = WebClient.builder()
                .baseUrl("https://api.openai.com")
                .defaultHeader("Authorization", "Bearer " + openAiApiKey)
                .defaultHeader("Content-Type", "application/json")
                .build();

        Map<String, Object> body = Map.of(
                "model", model,
                "max_tokens", maxTokens,
                "messages", List.of(
                        Map.of("role", "user", "content", prompt)
                ),
                "response_format", Map.of("type", "json_object")
        );

        try {
            String responseBody = client.post()
                    .uri("/v1/chat/completions")
                    .bodyValue(body)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            JsonNode root = objectMapper.readTree(responseBody);
            String content = root.path("choices").get(0).path("message").path("content").stringValue();
            return objectMapper.readValue(content, DietRecommendResponse.class);
        } catch (Exception e) {
            log.error("OpenAI 식단 추천 API 호출 실패", e);
            throw new RuntimeException("AI 식단 추천 중 오류가 발생했습니다.", e);
        }
    }
}
