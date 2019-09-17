INSERT INTO opponents(created_by, pointing_to_user)
SELECT user_id, (CASE WHEN is_real_user THEN user_id ELSE null END)
FROM opponents;
