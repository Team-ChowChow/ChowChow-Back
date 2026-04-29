package com.petdiet.ai.image.service;

import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;
import com.petdiet.ai.image.dto.ImageGenerateResponse;
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

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class ImageGenerateService {

    private final UserRepository userRepository;
    private final UserPetRepository userPetRepository;
    private final ObjectMapper objectMapper;

    @Value("${openai.api-key}")
    private String openAiApiKey;

    @Transactional(readOnly = true)
    public ImageGenerateResponse generateCharacterImage(UUID authUuid, Integer petId, String style) {
        User user = userRepository.findByAuthUuid(authUuid)
                .orElseThrow(() -> new IllegalStateException("유저를 찾을 수 없습니다."));
        UserPet pet = userPetRepository.findByPetIdAndUser(petId, user)
                .orElseThrow(() -> new IllegalArgumentException("반려동물을 찾을 수 없습니다."));

        String prompt = buildCharacterPrompt(pet, style);
        return ImageGenerateResponse.builder()
                .imageUrl(callDallE(prompt))
                .build();
    }

    public ImageGenerateResponse generateRecipeImage(String recipeName, List<String> ingredients, String description) {
        String prompt = buildRecipePrompt(recipeName, ingredients, description);
        return ImageGenerateResponse.builder()
                .imageUrl(callDallE(prompt))
                .build();
    }

    private String buildCharacterPrompt(UserPet pet, String style) {
        String styleDesc = (style != null && !style.isBlank()) ? style : "cute cartoon chibi";
        return String.format(
                "A %s illustration of a %s named %s as a %s character. " +
                "The image should be vibrant, friendly, and suitable for a pet care app. " +
                "White background, high quality, digital art style.",
                styleDesc, pet.getPetType(), pet.getPetName(), styleDesc
        );
    }

    private String buildRecipePrompt(String recipeName, List<String> ingredients, String description) {
        StringBuilder sb = new StringBuilder();
        sb.append("A beautiful food photography style image of a homemade pet food dish called '")
                .append(recipeName).append("'");
        if (ingredients != null && !ingredients.isEmpty()) {
            sb.append(", made with ").append(String.join(", ", ingredients));
        }
        if (description != null && !description.isBlank()) {
            sb.append(". ").append(description);
        }
        sb.append(". Clean white plate, natural lighting, top-down view, high quality food photography.");
        return sb.toString();
    }

    private String callDallE(String prompt) {
        WebClient client = WebClient.builder()
                .baseUrl("https://api.openai.com")
                .defaultHeader("Authorization", "Bearer " + openAiApiKey)
                .defaultHeader("Content-Type", "application/json")
                .build();

        Map<String, Object> body = Map.of(
                "model", "dall-e-3",
                "prompt", prompt,
                "n", 1,
                "size", "1024x1024",
                "quality", "standard"
        );

        try {
            String responseBody = client.post()
                    .uri("/v1/images/generations")
                    .bodyValue(body)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            JsonNode root = objectMapper.readTree(responseBody);
            return root.path("data").get(0).path("url").stringValue();
        } catch (Exception e) {
            log.error("DALL-E 이미지 생성 API 호출 실패", e);
            throw new RuntimeException("AI 이미지 생성 중 오류가 발생했습니다.", e);
        }
    }
}
