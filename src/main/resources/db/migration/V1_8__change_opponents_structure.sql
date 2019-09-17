ALTER TABLE opponents
    ADD COLUMN created_by BIGINT REFERENCES users ON DELETE CASCADE,
    ADD COLUMN pointing_to_user BIGINT REFERENCES users DEFAULT NULL;

INSERT INTO opponents(created_by, pointing_to_user)
SELECT user_id, (CASE WHEN is_real_user THEN user_id ELSE null END)
FROM opponents;

ALTER TABLE opponents
    ALTER COLUMN created_by SET NOT NULL,
    DROP COLUMN is_real_user,
    DROP COLUMN user_id,
    ADD CONSTRAINT UNIQUE_OPPONENT_NAME UNIQUE (name, created_by);

