package com.petdiet.ai.image.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ImageGenerateResponse {
    private String imageUrl;
}
