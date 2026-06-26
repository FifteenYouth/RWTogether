package com.rwtogether.service;

import com.rwtogether.dto.EpisodeDto;
import com.rwtogether.entity.Episode;
import com.rwtogether.entity.User;
import com.rwtogether.entity.UserEpisode;
import com.rwtogether.repository.EpisodeRepository;
import com.rwtogether.repository.UserEpisodeRepository;
import com.rwtogether.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EpisodeService {

    private final EpisodeRepository episodeRepository;
    private final UserEpisodeRepository userEpisodeRepository;
    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public List<EpisodeDto> getWorkEpisodes(Long workId, Long userId) {
        List<Episode> episodes = episodeRepository.findByWorkIdOrderBySeasonNumAscEpisodeNumAsc(workId);
        List<UserEpisode> watchedEpisodes = userEpisodeRepository.findByUserIdAndWorkId(userId, workId);

        Map<Long, UserEpisode> watchedMap = watchedEpisodes.stream()
            .collect(Collectors.toMap(ue -> ue.getEpisode().getId(), ue -> ue));

        return episodes.stream().map(ep -> {
            EpisodeDto dto = new EpisodeDto();
            dto.setId(ep.getId());
            dto.setWorkId(ep.getWork().getId());
            dto.setEpisodeNum(ep.getEpisodeNum());
            dto.setSeasonNum(ep.getSeasonNum());
            dto.setTitle(ep.getTitle());
            dto.setAirDate(ep.getAirDate());
            dto.setThumbnailUrl(ep.getThumbnailUrl());

            UserEpisode userEp = watchedMap.get(ep.getId());
            if (userEp != null) {
                dto.setWatched(true);
                dto.setWatchedAt(userEp.getWatchedAt());
                dto.setRating(userEp.getRating());
            } else {
                dto.setWatched(false);
            }

            return dto;
        }).collect(Collectors.toList());
    }

    @Transactional
    public EpisodeDto toggleWatched(Long episodeId, Long userId) {
        Episode episode = episodeRepository.findById(episodeId)
            .orElseThrow(() -> new RuntimeException("Episode not found"));
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found"));

        var existingOpt = userEpisodeRepository.findByUserIdAndEpisodeId(userId, episodeId);

        if (existingOpt.isPresent()) {
            // 取消标记
            userEpisodeRepository.delete(existingOpt.get());
            return toDto(episode, null);
        } else {
            // 标记为已看
            UserEpisode userEp = new UserEpisode();
            userEp.setUser(user);
            userEp.setEpisode(episode);
            userEp.setWatchedAt(LocalDateTime.now());
            userEpisodeRepository.save(userEp);
            return toDto(episode, userEp);
        }
    }

    @Transactional
    public EpisodeDto rateEpisode(Long episodeId, Long userId, Integer rating) {
        Episode episode = episodeRepository.findById(episodeId)
            .orElseThrow(() -> new RuntimeException("Episode not found"));
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found"));

        var userEpOpt = userEpisodeRepository.findByUserIdAndEpisodeId(userId, episodeId);
        UserEpisode userEp;

        if (userEpOpt.isPresent()) {
            userEp = userEpOpt.get();
            userEp.setRating(rating);
        } else {
            userEp = new UserEpisode();
            userEp.setUser(user);
            userEp.setEpisode(episode);
            userEp.setRating(rating);
            userEp.setWatchedAt(LocalDateTime.now());
        }

        userEpisodeRepository.save(userEp);
        return toDto(episode, userEp);
    }

    private EpisodeDto toDto(Episode ep, UserEpisode userEp) {
        EpisodeDto dto = new EpisodeDto();
        dto.setId(ep.getId());
        dto.setWorkId(ep.getWork().getId());
        dto.setEpisodeNum(ep.getEpisodeNum());
        dto.setSeasonNum(ep.getSeasonNum());
        dto.setTitle(ep.getTitle());
        dto.setAirDate(ep.getAirDate());
        dto.setThumbnailUrl(ep.getThumbnailUrl());

        if (userEp != null) {
            dto.setWatched(true);
            dto.setWatchedAt(userEp.getWatchedAt());
            dto.setRating(userEp.getRating());
        } else {
            dto.setWatched(false);
        }

        return dto;
    }
}
