CREATE TABLE IF NOT EXISTS users (
  id            BIGINT       NOT NULL         AUTO_INCREMENT PRIMARY KEY,
  name          VARCHAR(255) NOT NULL UNIQUE,
  password      VARCHAR(255)                  DEFAULT NULL,
  register_date DATETIME     NOT NULL         DEFAULT NOW()
);