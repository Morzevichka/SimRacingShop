CREATE TYPE status AS ENUM('NEW', 'PROCESSED', 'FAILED');

CREATE TABLE IF NOT EXISTS outbox_events (
    id UUID PRIMARY KEY,
    event_id VARCHAR(255) NOT NULL,
    topic VARCHAR(255) NOT NULL,
    payload JSONB NOT NULL,
    status STATUS NOT NULL,
    created_at TIMESTAMP NOT NULL
);

CREATE INDEX idx_outbox_event_status_created ON outbox_events(status, created_at);