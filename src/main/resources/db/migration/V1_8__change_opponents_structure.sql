ALTER TABLE opponents
    ADD COLUMN created_by BIGINT REFERENCES users ON DELETE CASCADE,
    ADD COLUMN pointing_to_user BIGINT REFERENCES users DEFAULT NULL;
