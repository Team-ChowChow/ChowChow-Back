package com.petdiet.pet.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Getter
public class PetRequest {
    @NotBlank
    private String petName;

    @NotNull
    private String petType;

    private Integer breedId;
    private String petGender;
    private LocalDate petBirthdate;
    private BigDecimal petWeight;
    private Integer petBodyConditionScore;
    private LocalDate petBodyScoreDate;
    private Integer petActivityLevel;
    private Boolean isNeutered;
    private Boolean petNeutered;
    private String petProfileImg;
    private String petProfileImageUrl;
    private List<Integer> allergyIds;
    private List<Integer> diseaseIds;

    public Boolean getIsNeutered() {
        return isNeutered != null ? isNeutered : petNeutered;
    }

    public String getPetProfileImg() {
        return petProfileImg != null ? petProfileImg : petProfileImageUrl;
    }
}
