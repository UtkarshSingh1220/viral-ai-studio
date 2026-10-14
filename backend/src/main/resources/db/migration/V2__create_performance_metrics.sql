CREATE TABLE performance_metrics (
    id UUID PRIMARY KEY,
    content_id UUID NOT NULL REFERENCES content_items(id) ON DELETE CASCADE,
    platform VARCHAR(30) NOT NULL,
    views BIGINT NOT NULL DEFAULT 0 CHECK (views >= 0),
    likes BIGINT NOT NULL DEFAULT 0 CHECK (likes >= 0),
    comments BIGINT NOT NULL DEFAULT 0 CHECK (comments >= 0),
    shares BIGINT NOT NULL DEFAULT 0 CHECK (shares >= 0),
    saves BIGINT NOT NULL DEFAULT 0 CHECK (saves >= 0),
    followers_gained BIGINT NOT NULL DEFAULT 0 CHECK (followers_gained >= 0),
    average_watch_seconds DOUBLE PRECISION CHECK (average_watch_seconds IS NULL OR average_watch_seconds >= 0),
    captured_at TIMESTAMPTZ NOT NULL
);
CREATE INDEX idx_performance_content_platform_captured
    ON performance_metrics(content_id, platform, captured_at DESC);
CREATE INDEX idx_performance_captured_at ON performance_metrics(captured_at DESC);
