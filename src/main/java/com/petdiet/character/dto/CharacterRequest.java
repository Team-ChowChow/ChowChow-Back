package com.petdiet.character.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class CharacterRequest {
    @NotNull
    private Integer petId;

    @NotBlank
    private String characterName;

    private String characterType;
    private String characterState;
    private Boolean isPrimary;
}
