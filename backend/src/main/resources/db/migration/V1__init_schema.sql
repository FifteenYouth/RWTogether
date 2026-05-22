CREATE TABLE IF NOT EXISTS `user` (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(100) NOT NULL,
    avatar VARCHAR(500) DEFAULT '',
    bio VARCHAR(200) DEFAULT '',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_username (username)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS work (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(200) NOT NULL,
    cover_url VARCHAR(500) DEFAULT '',
    description TEXT,
    type ENUM('ANIME', 'DRAMA', 'MOVIE', 'BOOK') NOT NULL,
    total_episodes INT DEFAULT 0,
    total_seasons INT DEFAULT 0,
    api_source VARCHAR(50) DEFAULT NULL,
    api_id VARCHAR(100) DEFAULT NULL,
    metadata JSON DEFAULT NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE INDEX idx_api_source_id (api_source, api_id),
    INDEX idx_type (type)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS user_work (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    work_id BIGINT NOT NULL,
    status ENUM('WANT', 'WATCHING', 'PAUSED', 'DROPPED', 'DONE') NOT NULL DEFAULT 'WANT',
    progress_detail VARCHAR(200) DEFAULT '',
    rating INT DEFAULT NULL CHECK (rating IS NULL OR (rating >= 1 AND rating <= 10)),
    started_at DATE DEFAULT NULL,
    finished_at DATE DEFAULT NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE INDEX idx_user_work (user_id, work_id),
    INDEX idx_user_status (user_id, status),
    INDEX idx_work_id (work_id),
    CONSTRAINT fk_user_work_user FOREIGN KEY (user_id) REFERENCES `user`(id) ON DELETE CASCADE,
    CONSTRAINT fk_user_work_work FOREIGN KEY (work_id) REFERENCES work(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS episode (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    work_id BIGINT NOT NULL,
    episode_num INT NOT NULL,
    season_num INT DEFAULT 1,
    title VARCHAR(200) DEFAULT '',
    air_date DATE DEFAULT NULL,
    thumbnail_url VARCHAR(500) DEFAULT '',
    INDEX idx_work_episode (work_id, season_num, episode_num),
    CONSTRAINT fk_episode_work FOREIGN KEY (work_id) REFERENCES work(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS comment (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    work_id BIGINT NOT NULL,
    content TEXT NOT NULL,
    type ENUM('REVIEW', 'THOUGHT') NOT NULL DEFAULT 'REVIEW',
    episode_num INT DEFAULT NULL,
    parent_id BIGINT DEFAULT NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_work_type (work_id, type),
    INDEX idx_work_episode (work_id, episode_num),
    INDEX idx_parent (parent_id),
    CONSTRAINT fk_comment_user FOREIGN KEY (user_id) REFERENCES `user`(id) ON DELETE CASCADE,
    CONSTRAINT fk_comment_work FOREIGN KEY (work_id) REFERENCES work(id) ON DELETE CASCADE,
    CONSTRAINT fk_comment_parent FOREIGN KEY (parent_id) REFERENCES comment(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS like_record (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    comment_id BIGINT NOT NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UNIQUE INDEX idx_user_comment (user_id, comment_id),
    CONSTRAINT fk_like_user FOREIGN KEY (user_id) REFERENCES `user`(id) ON DELETE CASCADE,
    CONSTRAINT fk_like_comment FOREIGN KEY (comment_id) REFERENCES comment(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
