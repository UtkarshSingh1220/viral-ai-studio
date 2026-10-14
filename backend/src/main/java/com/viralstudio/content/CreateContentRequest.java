package com.viralstudio.content;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateContentRequest(
    @NotBlank @Size(max = 180) String topic,
    @NotBlank @Size(max = 500) String hook,
    @NotBlank @Size(max = 12000) String script,
    @Size(max = 500) String caption,
    @Size(max = 500) String hashtags,
    ContentPlatform platform
) {}
