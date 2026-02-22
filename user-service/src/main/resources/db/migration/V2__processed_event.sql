CREATE TABLE IF NOT EXISTS processed_events (
    id UUID PRIMARY KEY,
    event_id UUID NOT NULL UNIQUE,
    event_topic VARCHAR(255) NOT NULL,
    processed_at TIMESTAMP NOT NULL
);