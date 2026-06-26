CREATE TABLE IF NOT EXISTS user_episode (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    episode_id BIGINT NOT NULL,
    watched_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    rating INT DEFAULT NULL CHECK (rating IS NULL OR (rating >= 1 AND rating <= 10)),
    UNIQUE INDEX idx_user_episode (user_id, episode_id),
    INDEX idx_user_id (user_id),
    INDEX idx_episode_id (episode_id),
    CONSTRAINT fk_user_episode_user FOREIGN KEY (user_id) REFERENCES `user`(id) ON DELETE CASCADE,
    CONSTRAINT fk_user_episode_episode FOREIGN KEY (episode_id) REFERENCES episode(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
