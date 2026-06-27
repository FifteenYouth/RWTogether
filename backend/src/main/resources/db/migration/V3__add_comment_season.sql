-- 评论增加季编号，使多季作品的分集感想能区分「第X季第Y集」，避免不同季同集号串台
ALTER TABLE comment ADD COLUMN season_num INT DEFAULT NULL AFTER episode_num;

-- 已有分集感想默认归入第 1 季
UPDATE comment SET season_num = 1 WHERE type = 'THOUGHT' AND episode_num IS NOT NULL AND season_num IS NULL;

-- 按季+集筛选分集感想的复合索引
CREATE INDEX idx_work_season_episode ON comment (work_id, season_num, episode_num);
