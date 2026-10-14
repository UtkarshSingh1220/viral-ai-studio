package com.viralstudio.trends;

import java.util.List;

public record TrendIdea(
    String title,
    String angle,
    String whyNow,
    String audienceFit,
    List<String> sourceUrls
) {}
