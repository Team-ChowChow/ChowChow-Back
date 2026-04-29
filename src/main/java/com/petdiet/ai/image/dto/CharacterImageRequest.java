package com.petdiet.ai.image.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class CharacterImageRequest {

    @NotNull
    private Integer petId;

    private String style;
}
