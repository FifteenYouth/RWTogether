package com.rwtogether.repository;

import com.rwtogether.entity.Episode;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EpisodeRepository extends JpaRepository<Episode, Long> {

    List<Episode> findByWorkIdOrderBySeasonNumAscEpisodeNumAsc(Long workId);
}
