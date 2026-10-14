CREATE TABLE content_items (
    id UUID PRIMARY KEY,
    topic VARCHAR(180) NOT NULL,
    hook VARCHAR(500) NOT NULL,
    script TEXT NOT NULL,
    caption VARCHAR(500),
    hashtags VARCHAR(500),
    platform VARCHAR(30) NOT NULL DEFAULT 'BOTH',
    status VARCHAR(30) NOT NULL DEFAULT 'DRAFT',
    source_url VARCHAR(1000),
    editor_notes VARCHAR(1000),
    created_at TIMESTAMPTZ NOT NULL,
    updated_at TIMESTAMPTZ NOT NULL
);
CREATE INDEX idx_content_items_status ON content_items(status);
CREATE INDEX idx_content_items_updated_at ON content_items(updated_at DESC);
