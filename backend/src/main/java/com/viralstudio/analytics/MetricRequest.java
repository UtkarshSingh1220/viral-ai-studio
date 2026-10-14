package com.viralstudio.analytics;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.DecimalMin;
import java.time.Instant;

public record MetricRequest(
    @NotBlank String platform,
    @PositiveOrZero long views,
    @PositiveOrZero long likes,
    @PositiveOrZero long comments,
    @PositiveOrZero long shares,
    @PositiveOrZero long saves,
    @PositiveOrZero long followersGained,
    @DecimalMin("0.0") Double averageWatchSeconds,
    Instant capturedAt
) {}
