UPDATE opponents
SET created_by = user_id,
pointing_to_user = (CASE WHEN is_real_user THEN user_id ELSE null END)
FROM opponents;
