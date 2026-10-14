package com.viralstudio.analytics;

import com.viralstudio.content.ContentItem;
import com.viralstudio.content.ContentItemRepository;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import jakarta.validation.Valid;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/analytics")
public class AnalyticsController {
    private final PerformanceMetricRepository metrics;
    private final ContentItemRepository content;

    public AnalyticsController(PerformanceMetricRepository metrics, ContentItemRepository content) {
        this.metrics = metrics;
        this.content = content;
    }

    @PostMapping("/{contentId}")
    @ResponseStatus(HttpStatus.CREATED)
    public MetricResponse record(@PathVariable UUID contentId, @Valid @RequestBody MetricRequest request) {
        ContentItem item = content.findById(contentId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Content item not found"));
        String platform = request.platform().trim().toUpperCase(Locale.ROOT);
        if (!Set.of("INSTAGRAM_REELS", "YOUTUBE_SHORTS").contains(platform)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "platform must be INSTAGRAM_REELS or YOUTUBE_SHORTS");
        }
        PerformanceMetric saved = metrics.save(new PerformanceMetric(item, platform, request.views(),
            request.likes(), request.comments(), request.shares(), request.saves(), request.followersGained(),
            request.averageWatchSeconds(), request.capturedAt()));
        return MetricResponse.from(saved);
    }

    @GetMapping("/dashboard")
    public AnalyticsDashboard dashboard() {
        List<PerformanceMetric> all = metrics.findAllByOrderByCapturedAtDesc();
        Map<String, PerformanceMetric> latestByContentAndPlatform = new LinkedHashMap<>();
        for (PerformanceMetric metric : all) {
            String key = metric.getContent().getId() + ":" + metric.getPlatform();
            latestByContentAndPlatform.putIfAbsent(key, metric);
        }
        List<PerformanceMetric> latest = new ArrayList<>(latestByContentAndPlatform.values());
        long views = latest.stream().mapToLong(PerformanceMetric::getViews).sum();
        long likes = latest.stream().mapToLong(PerformanceMetric::getLikes).sum();
        long comments = latest.stream().mapToLong(PerformanceMetric::getComments).sum();
        long shares = latest.stream().mapToLong(PerformanceMetric::getShares).sum();
        long saves = latest.stream().mapToLong(PerformanceMetric::getSaves).sum();
        long followers = latest.stream().mapToLong(PerformanceMetric::getFollowersGained).sum();
        double engagement = views == 0 ? 0 : 100.0 * (likes + comments + shares + saves) / views;

        List<MetricResponse> top = latest.stream()
            .sorted(Comparator.comparingDouble(PerformanceMetric::getEngagementRate).reversed())
            .limit(5).map(MetricResponse::from).toList();

        List<String> recommendations = buildRecommendations(latest, views, engagement);
        return new AnalyticsDashboard(latest.size(), views, likes, comments, shares, saves, followers,
            Math.round(engagement * 100.0) / 100.0, top, recommendations);
    }

    private List<String> buildRecommendations(List<PerformanceMetric> latest, long totalViews, double engagement) {
        if (latest.isEmpty()) {
            return List.of("Record a platform metrics snapshot after each post to unlock performance-based recommendations.",
                "Track views, saves, shares, comments, and average watch time consistently.");
        }
        List<String> suggestions = new ArrayList<>();
        double saveShareRate = totalViews == 0 ? 0 : 100.0 * latest.stream()
            .mapToLong(m -> m.getSaves() + m.getShares()).sum() / totalViews;
        double commentRate = totalViews == 0 ? 0 : 100.0 * latest.stream()
            .mapToLong(PerformanceMetric::getComments).sum() / totalViews;
        double followerRate = totalViews == 0 ? 0 : 100.0 * latest.stream()
            .mapToLong(PerformanceMetric::getFollowersGained).sum() / totalViews;
        if (saveShareRate < 1.0) suggestions.add("Test a more saveable format: a checklist, code example, or concise step-by-step tutorial.");
        else suggestions.add("Your saves/shares signal utility. Make a follow-up that expands the same practical topic.");
        if (commentRate < 0.3) suggestions.add("End with one specific question to invite useful comments instead of a generic 'thoughts?'.");
        if (followerRate < 0.2) suggestions.add("Add a clear series promise, such as 'Part 2: implement this in Spring Boot', to convert viewers into followers.");
        OptionalDouble watch = latest.stream().filter(m -> m.getAverageWatchSeconds() != null)
            .mapToDouble(PerformanceMetric::getAverageWatchSeconds).average();
        if (watch.isPresent() && watch.getAsDouble() < 15.0) suggestions.add("Average watch time is under 15 seconds; test a faster first 2 seconds and remove setup before the payoff.");
        if (engagement >= 5.0) suggestions.add("Engagement is strong in the recorded sample. Reuse the hook structure, but vary the example and verify all claims.");
        if (suggestions.isEmpty()) suggestions.add("Compare performance by platform and topic after collecting more posts; avoid drawing conclusions from a tiny sample.");
        return suggestions;
    }

    public record MetricResponse(UUID id, UUID contentId, String topic, String platform, long views,
        long likes, long comments, long shares, long saves, long followersGained,
        Double averageWatchSeconds, double engagementRate, java.time.Instant capturedAt) {
        static MetricResponse from(PerformanceMetric m) {
            return new MetricResponse(m.getId(), m.getContent().getId(), m.getContent().getTopic(),
                m.getPlatform(), m.getViews(), m.getLikes(), m.getComments(), m.getShares(), m.getSaves(),
                m.getFollowersGained(), m.getAverageWatchSeconds(),
                Math.round(m.getEngagementRate() * 100.0) / 100.0, m.getCapturedAt());
        }
    }

    public record AnalyticsDashboard(int trackedPostPlatforms, long views, long likes, long comments,
        long shares, long saves, long followersGained, double engagementRate,
        List<MetricResponse> topPerformers, List<String> recommendations) {}
}
