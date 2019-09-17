ALTER TABLE opponents
    ALTER COLUMN created_by SET NOT NULL,
    DROP COLUMN is_real_user,
    DROP COLUMN user_id,
    ADD CONSTRAINT UNIQUE_OPPONENT_NAME UNIQUE (name, created_by);
