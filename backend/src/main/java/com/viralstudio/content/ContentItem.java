package com.viralstudio.content;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "content_items")
public class ContentItem {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, length = 180)
    private String topic;

    @Column(nullable = false, length = 500)
    private String hook;

    @Column(nullable = false, columnDefinition = "text")
    private String script;

    @Column(length = 500)
    private String caption;

    @Column(length = 500)
    private String hashtags;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private ContentPlatform platform = ContentPlatform.BOTH;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private ContentStatus status = ContentStatus.DRAFT;

    @Column(length = 1000)
    private String sourceUrl;

    @Column(length = 1000)
    private String editorNotes;

    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    @Column(nullable = false)
    private Instant updatedAt;

    @PrePersist
    void onCreate() { createdAt = updatedAt = Instant.now(); }

    @PreUpdate
    void onUpdate() { updatedAt = Instant.now(); }

    protected ContentItem() {}

    public ContentItem(String topic, String hook, String script, String caption, String hashtags, ContentPlatform platform) {
        this.topic = topic;
        this.hook = hook;
        this.script = script;
        this.caption = caption;
        this.hashtags = hashtags;
        this.platform = platform == null ? ContentPlatform.BOTH : platform;
        this.status = ContentStatus.DRAFT;
    }

    public UUID getId() { return id; }
    public String getTopic() { return topic; }
    public String getHook() { return hook; }
    public String getScript() { return script; }
    public String getCaption() { return caption; }
    public String getHashtags() { return hashtags; }
    public ContentPlatform getPlatform() { return platform; }
    public ContentStatus getStatus() { return status; }
    public String getSourceUrl() { return sourceUrl; }
    public String getEditorNotes() { return editorNotes; }
    public Instant getCreatedAt() { return createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }
    public void setStatus(ContentStatus status) { this.status = status; }
    public void setEditorNotes(String editorNotes) { this.editorNotes = editorNotes; }
}
