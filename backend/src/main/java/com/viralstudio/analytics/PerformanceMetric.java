package com.viralstudio.analytics;

import com.viralstudio.content.ContentItem;
import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "performance_metrics", indexes = {
    @Index(name = "idx_performance_content_platform_captured", columnList = "content_id,platform,captured_at")
})
public class PerformanceMetric {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "content_id", nullable = false)
    private ContentItem content;

    @Column(nullable = false, length = 30)
    private String platform;

    @Column(nullable = false)
    private long views;
    @Column(nullable = false)
    private long likes;
    @Column(nullable = false)
    private long comments;
    @Column(nullable = false)
    private long shares;
    @Column(nullable = false)
    private long saves;
    @Column(nullable = false)
    private long followersGained;
    @Column
    private Double averageWatchSeconds;
    @Column(nullable = false)
    private Instant capturedAt;

    protected PerformanceMetric() {}

    public PerformanceMetric(ContentItem content, String platform, long views, long likes,
            long comments, long shares, long saves, long followersGained,
            Double averageWatchSeconds, Instant capturedAt) {
        this.content = content;
        this.platform = platform;
        this.views = views;
        this.likes = likes;
        this.comments = comments;
        this.shares = shares;
        this.saves = saves;
        this.followersGained = followersGained;
        this.averageWatchSeconds = averageWatchSeconds;
        this.capturedAt = capturedAt == null ? Instant.now() : capturedAt;
    }

    public UUID getId() { return id; }
    public ContentItem getContent() { return content; }
    public String getPlatform() { return platform; }
    public long getViews() { return views; }
    public long getLikes() { return likes; }
    public long getComments() { return comments; }
    public long getShares() { return shares; }
    public long getSaves() { return saves; }
    public long getFollowersGained() { return followersGained; }
    public Double getAverageWatchSeconds() { return averageWatchSeconds; }
    public Instant getCapturedAt() { return capturedAt; }
    public double getEngagementRate() {
        return views == 0 ? 0.0 : 100.0 * (likes + comments + shares + saves) / views;
    }
}
