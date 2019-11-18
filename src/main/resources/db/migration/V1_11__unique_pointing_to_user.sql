ALTER TABLE opponents
    ADD CONSTRAINT UNIQUE_POINTING_TO_USER UNIQUE (created_by, pointing_to_user);
