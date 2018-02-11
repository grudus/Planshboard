CREATE TABLE IF NOT EXISTS users (
  id            BIGSERIAL PRIMARY KEY,
  name          VARCHAR(255) NOT NULL UNIQUE,
  password      VARCHAR(255)                          DEFAULT NULL,
  role          VARCHAR(255) NOT NULL                 DEFAULT 'USER',
  token         VARCHAR(255),
  register_date timestamp     NOT NULL                 DEFAULT NOW()
);

CREATE TABLE IF NOT EXISTS boardgames (
  id      BIGSERIAL PRIMARY KEY,
  name    VARCHAR(255) NOT NULL,
  user_id BIGINT       NOT NULL REFERENCES users ON DELETE CASCADE,
  CONSTRAINT UNIQUE_BOARD_GAME_NAME UNIQUE (name, user_id)
);