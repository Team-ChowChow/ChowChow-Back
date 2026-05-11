package com.petdiet.ai.diet.service;

import tools.jackson.databind.ObjectMapper;
import com.petdiet.auth.entity.User;
import com.petdiet.auth.repository.UserRepository;
import com.petdiet.master.entity.Allergy;
import com.petdiet.master.entity.Breed;
import com.petdiet.master.entity.Disease;
import com.petdiet.master.repository.AllergyRepository;
import com.petdiet.master.repository.BreedRepository;
import com.petdiet.master.repository.DiseaseRepository;
import com.petdiet.pet.entity.UserPet;
import com.petdiet.pet.repository.UserPetRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class DietRecommendServiceTest {

    @Mock UserRepository userRepository;
    @Mock UserPetRepository userPetRepository;
    @Mock AllergyRepository allergyRepository;
    @Mock DiseaseRepository diseaseRepository;
    @Mock BreedRepository breedRepository;

    DietRecommendService service;

    @BeforeEach
    void setUp() {
        service = new DietRecommendService(
                userRepository, userPetRepository,
                allergyRepository, diseaseRepository, breedRepository,
                new ObjectMapper(),
                "test-api-key", "https://api.openai.com", "gpt-4o", 2048);
    }

    @Test
    @DisplayName("존재하지 않는 유저로 요청 시 예외 발생")
    void recommend_throwsWhenUserNotFound() {
        given(userRepository.findByAuthUuid(any())).willReturn(Optional.empty());

        assertThatThrownBy(() -> service.recommend(UUID.randomUUID(), 1, null))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("유저를 찾을 수 없습니다");
    }

    @Test
    @DisplayName("존재하지 않는 반려동물로 요청 시 예외 발생")
    void recommend_throwsWhenPetNotFound() {
        User user = User.builder()
                .authUuid(UUID.randomUUID())
                .userName("테스터")
                .userNickname("tester1")
                .build();
        given(userRepository.findByAuthUuid(any())).willReturn(Optional.of(user));
        given(userPetRepository.findByPetIdAndUser(any(), any())).willReturn(Optional.empty());

        assertThatThrownBy(() -> service.recommend(UUID.randomUUID(), 99, null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("반려동물을 찾을 수 없습니다");
    }

    @Test
    @DisplayName("프롬프트에 알레르기 이름이 포함된다 (ID 아님)")
    void buildPrompt_includesAllergyNames() {
        UserPet pet = buildPet("강아지", "DOG");
        Breed breed = null;

        Allergy chicken = Allergy.builder().allergyId(1).allergyName("닭고기 알러지").build();
        Allergy beef = Allergy.builder().allergyId(2).allergyName("소고기 알러지").build();
        List<Allergy> allergies = List.of(chicken, beef);

        String prompt = service.buildPrompt(pet, breed, allergies, List.of(), null);

        assertThat(prompt)
                .contains("닭고기 알러지")
                .contains("소고기 알러지")
                .doesNotContain("allergyId")
                .doesNotContain("\"1\"")
                .doesNotContain("\"2\"");
    }

    @Test
    @DisplayName("프롬프트에 질환 이름과 설명이 포함된다")
    void buildPrompt_includesDiseaseNameAndDescription() {
        UserPet pet = buildPet("나비", "CAT");
        Disease kidney = Disease.builder()
                .diseaseId(1)
                .diseaseName("신장 질환")
                .diseaseDescription("단백질·인·나트륨 섭취 제한 필요")
                .build();

        String prompt = service.buildPrompt(pet, null, List.of(), List.of(kidney), null);

        assertThat(prompt)
                .contains("신장 질환")
                .contains("단백질·인·나트륨 섭취 제한 필요");
    }

    @Test
    @DisplayName("품종이 있으면 프롬프트에 포함된다")
    void buildPrompt_includesBreedWhenPresent() {
        UserPet pet = buildPet("초코", "DOG");
        Breed breed = Breed.builder().breedId(1).breedName("말티즈").petType("DOG").build();

        String prompt = service.buildPrompt(pet, breed, List.of(), List.of(), null);

        assertThat(prompt).contains("말티즈");
    }

    @Test
    @DisplayName("체중과 중성화 정보가 있으면 프롬프트에 포함된다")
    void buildPrompt_includesWeightAndNeuteredInfo() {
        UserPet pet = UserPet.builder()
                .petName("코코")
                .petType("DOG")
                .petWeight(new BigDecimal("5.20"))
                .isNeutered(true)
                .petBirthdate(LocalDate.now().minusYears(2))
                .build();

        String prompt = service.buildPrompt(pet, null, List.of(), List.of(), null);

        assertThat(prompt)
                .contains("5.20")
                .contains("예");
    }

    @Test
    @DisplayName("사용자 요청 메모가 있으면 프롬프트에 포함된다")
    void buildPrompt_includesUserNotes() {
        UserPet pet = buildPet("뽀삐", "DOG");
        String notes = "소화가 약해서 부드러운 재료로 부탁드립니다";

        String prompt = service.buildPrompt(pet, null, List.of(), List.of(), notes);

        assertThat(prompt).contains(notes);
    }

    @Test
    @DisplayName("응답 형식이 JSON 지시를 포함한다")
    void buildPrompt_containsJsonFormatInstruction() {
        UserPet pet = buildPet("망고", "CAT");

        String prompt = service.buildPrompt(pet, null, List.of(), List.of(), null);

        assertThat(prompt)
                .contains("JSON")
                .contains("ingredients")
                .contains("warnings");
    }

    private UserPet buildPet(String name, String type) {
        return UserPet.builder()
                .petName(name)
                .petType(type)
                .build();
    }
}
