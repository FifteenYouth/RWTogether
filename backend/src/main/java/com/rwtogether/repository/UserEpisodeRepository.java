package com.rwtogether.repository;

import com.rwtogether.entity.UserEpisode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserEpisodeRepository extends JpaRepository<UserEpisode, Long> {

    @Query("SELECT ue FROM UserEpisode ue WHERE ue.user.id = :userId AND ue.episode.id = :episodeId")
    Optional<UserEpisode> findByUserIdAndEpisodeId(Long userId, Long episodeId);

    @Query("SELECT ue FROM UserEpisode ue WHERE ue.user.id = :userId AND ue.episode.work.id = :workId")
    List<UserEpisode> findByUserIdAndWorkId(Long userId, Long workId);

    @Query("SELECT COUNT(ue) FROM UserEpisode ue WHERE ue.user.id = :userId AND ue.episode.work.id = :workId")
    Long countByUserIdAndWorkId(Long userId, Long workId);
}
