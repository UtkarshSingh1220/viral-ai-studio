package com.viralstudio.content;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record GenerateContentRequest(
    @NotBlank @Size(max = 180) String topic,
    ContentPlatform platform
) {}
