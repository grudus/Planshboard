CREATE TABLE IF NOT EXISTS notifications
(
    id              BIGSERIAL PRIMARY KEY,
    available_for   BIGINT    NOT NULL REFERENCES users ON DELETE CASCADE,
    created_at      TIMESTAMP NOT NULL DEFAULT NOW(),
    visited         BOOLEAN   NOT NULL DEFAULT FALSE,
    type            VARCHAR   NOT NULL,
    created_by      BIGINT             DEFAULT NULL REFERENCES users ON DELETE SET DEFAULT,
    additional_data JSON               DEFAULT NULL,
    CHECK ( type IN ('MARKED_AS_OPPONENT') )
)
