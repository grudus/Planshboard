CREATE TABLE IF NOT EXISTS users (
  id            BIGINT       NOT NULL                 AUTO_INCREMENT PRIMARY KEY,
  name          VARCHAR(255) NOT NULL UNIQUE,
  password      VARCHAR(255)                          DEFAULT NULL,
  role          VARCHAR(255) NOT NULL                 DEFAULT 'USER',
  token         VARCHAR(255),
  register_date DATETIME     NOT NULL                 DEFAULT NOW()
);