package com.rwtogether.service;

import com.rwtogether.dto.CommentDto;
import com.rwtogether.dto.ProgressDto;
import com.rwtogether.dto.WorkDetailDto;
import com.rwtogether.dto.WorkDto;
import com.rwtogether.entity.Episode;
import com.rwtogether.entity.Work;
import com.rwtogether.exception.BusinessException;
import com.rwtogether.exception.ResourceNotFoundException;
import com.rwtogether.repository.CommentRepository;
import com.rwtogether.repository.EpisodeRepository;
import com.rwtogether.repository.UserWorkRepository;
import com.rwtogether.repository.WorkRepository;
import com.rwtogether.service.external.BookApiService;
import com.rwtogether.service.external.TmdbService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class WorkService {

    private final WorkRepository workRepository;
    private final UserWorkRepository userWorkRepository;
    private final CommentRepository commentRepository;
    private final EpisodeRepository episodeRepository;
    private final ProgressService progressService;
    private final CommentService commentService;
    private final TmdbService tmdbService;
    private final BookApiService bookApiService;

    public List<Map<String, Object>> searchWorks(String keyword, String type) {
        List<Callable<List<Map<String, Object>>>> tasks = new ArrayList<>();

        if (type == null || type.equals("all")) {
            // 番剧与电视剧统一来自 TMDB(tv)，按动画类型自动归类为 ANIME/DRAMA
            tasks.add(() -> tmdbService.searchTv(keyword, null));
            tasks.add(() -> tmdbService.searchMovie(keyword));
            tasks.add(() -> bookApiService.searchBooks(keyword));
        } else {
            switch (type.toUpperCase()) {
                case "ANIME" -> tasks.add(() -> tmdbService.searchTv(keyword, true));
                case "DRAMA" -> tasks.add(() -> tmdbService.searchTv(keyword, false));
                case "MOVIE" -> tasks.add(() -> tmdbService.searchMovie(keyword));
                case "BOOK" -> tasks.add(() -> bookApiService.searchBooks(keyword));
            }
        }

        ExecutorService executor = Executors.newFixedThreadPool(tasks.size());
        List<Map<String, Object>> results = new ArrayList<>();
        try {
            List<Future<List<Map<String, Object>>>> futures = executor.invokeAll(tasks, 12, TimeUnit.SECONDS);
            for (Future<List<Map<String, Object>>> future : futures) {
                try {
                    results.addAll(future.get());
                } catch (Exception e) {
                    log.warn("搜索任务失败: {}", e.getMessage());
                }
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } finally {
            executor.shutdownNow();
        }

        return results;
    }

    @Transactional
    public Work findOrCreateWork(Map<String, Object> workData) {
        String apiSource = (String) workData.get("apiSource");
        String apiId = (String) workData.get("apiId");

        if (apiSource != null && apiId != null) {
            Optional<Work> existing = workRepository.findByApiSourceAndApiId(apiSource, apiId);
            if (existing.isPresent()) {
                return existing.get();
            }
        }

        Work work = new Work();
        work.setTitle((String) workData.getOrDefault("title", ""));
        work.setCoverUrl((String) workData.getOrDefault("coverUrl", ""));
        work.setDescription((String) workData.getOrDefault("description", ""));
        work.setType(Work.WorkType.valueOf((String) workData.getOrDefault("type", "ANIME")));
        work.setTotalEpisodes(workData.get("totalEpisodes") != null
                ? ((Number) workData.get("totalEpisodes")).intValue() : 0);
        work.setTotalSeasons(workData.get("totalSeasons") != null
                ? ((Number) workData.get("totalSeasons")).intValue() : 0);
        work.setApiSource(apiSource);
        work.setApiId(apiId);
        if (workData.get("metadata") != null) {
            @SuppressWarnings("unchecked")
            Map<String, Object> metadata = (Map<String, Object>) workData.get("metadata");
            work.setMetadata(metadata);
        }

        work = workRepository.save(work);

        // 保存分集信息（如果有）
        if (workData.containsKey("episodes")) {
            @SuppressWarnings("unchecked")
            List<Map<String, Object>> episodesData = (List<Map<String, Object>>) workData.get("episodes");
            saveEpisodes(work, episodesData);
        }

        return work;
    }

    /**
     * 保存分集信息
     */
    private void saveEpisodes(Work work, List<Map<String, Object>> episodesData) {
        if (episodesData == null || episodesData.isEmpty()) {
            return;
        }

        log.info("保存 {} 的分集信息，共 {} 集", work.getTitle(), episodesData.size());

        DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE;

        for (Map<String, Object> epData : episodesData) {
            try {
                Episode episode = new Episode();
                episode.setWork(work);

                // 集数
                Object episodeNumObj = epData.get("episodeNum");
                if (episodeNumObj != null) {
                    episode.setEpisodeNum(episodeNumObj instanceof Number
                            ? ((Number) episodeNumObj).intValue()
                            : Integer.parseInt(episodeNumObj.toString()));
                }

                // 季数（默认为1，TMDB 分集会带 seasonNum）
                Object seasonNumObj = epData.get("seasonNum");
                if (seasonNumObj instanceof Number num) {
                    episode.setSeasonNum(num.intValue());
                } else {
                    episode.setSeasonNum(1);
                }

                // 标题（优先使用中文标题）
                String title = (String) epData.getOrDefault("titleCn", epData.get("title"));
                episode.setTitle(title != null ? title : "");

                // 播出日期
                String airDateStr = (String) epData.get("airDate");
                if (airDateStr != null && !airDateStr.isEmpty()) {
                    try {
                        episode.setAirDate(LocalDate.parse(airDateStr, formatter));
                    } catch (Exception e) {
                        log.warn("解析播出日期失败: {}", airDateStr);
                    }
                }

                // 缩略图（如果有）
                String thumbnailUrl = (String) epData.get("thumbnailUrl");
                episode.setThumbnailUrl(thumbnailUrl != null ? thumbnailUrl : "");

                episodeRepository.save(episode);

            } catch (Exception e) {
                log.error("保存分集失败: {}", e.getMessage());
            }
        }

        log.info("分集信息保存完成");
    }

    public WorkDetailDto getWorkDetail(Long workId, Long userId) {
        Work work = workRepository.findById(workId)
                .orElseThrow(() -> new ResourceNotFoundException("作品不存在"));

        WorkDetailDto dto = new WorkDetailDto();
        dto.setId(work.getId());
        dto.setTitle(work.getTitle());
        dto.setCoverUrl(work.getCoverUrl());
        dto.setType(work.getType().name());
        dto.setTotalEpisodes(work.getTotalEpisodes());
        dto.setTotalSeasons(work.getTotalSeasons());
        dto.setDescription(work.getDescription());
        dto.setApiSource(work.getApiSource());
        dto.setMetadata(work.getMetadata());

        // 我的进度
        dto.setMyProgress(progressService.getUserProgress(userId, workId));

        // 其他人的进度
        dto.setFriendProgresses(progressService.getWorkProgresses(workId, userId));

        // 评论
        dto.setComments(commentService.getWorkComments(workId, userId));

        return dto;
    }

    public List<WorkDto> getUserWorks(Long userId, String type, String status) {
        List<Work> works;
        if (type != null) {
            works = workRepository.findByType(Work.WorkType.valueOf(type));
        } else {
            works = workRepository.findAll();
        }

        // 按用户进度筛选（仅在指定了 status 时），并填充用户状态
        return works.stream()
                .filter(w -> {
                    if (status != null) {
                        return userWorkRepository.findByUserIdAndWorkId(userId, w.getId())
                                .map(uw -> uw.getStatus().name().equals(status))
                                .orElse(false);
                    }
                    // 未指定 status 时返回所有作品
                    return true;
                })
                .map(w -> toDto(w, userId))
                .collect(Collectors.toList());
    }

    @Transactional
    public WorkDto createWorkManual(Map<String, Object> workData, Long userId) {
        Work work = findOrCreateWork(workData);
        return toDto(work, userId);
    }

    @Transactional
    public WorkDto importFromApi(String apiSource, String apiId, Map<String, Object> fallbackData, Long userId) {
        if (apiSource == null || apiId == null) {
            return createWorkManual(fallbackData, userId);
        }

        // 先检查是否已存在
        Optional<Work> existing = workRepository.findByApiSourceAndApiId(apiSource, apiId);
        if (existing.isPresent()) {
            return toDto(existing.get(), userId);
        }

        // 尝试从 API 获取详细信息
        Map<String, Object> detailData = fetchApiDetail(apiSource, apiId);
        if (detailData.isEmpty()) {
            // API 获取失败，使用搜索结果中的数据
            detailData = fallbackData;
        }

        Work work = findOrCreateWork(detailData);
        return toDto(work, userId);
    }

    private Map<String, Object> fetchApiDetail(String apiSource, String apiId) {
        try {
            return switch (apiSource) {
                case "tmdb" -> tmdbService.getDetail(apiId);
                case "google_books" -> bookApiService.getBookDetail(apiId);
                default -> Collections.emptyMap();
            };
        } catch (Exception e) {
            log.error("获取 API 详情失败 ({}): {}", apiSource, e.getMessage());
            return Collections.emptyMap();
        }
    }

    private WorkDto toDto(Work work) {
        return toDto(work, null);
    }

    private WorkDto toDto(Work work, Long userId) {
        String userStatus = null;
        if (userId != null) {
            userStatus = userWorkRepository.findByUserIdAndWorkId(userId, work.getId())
                    .map(uw -> uw.getStatus().name())
                    .orElse(null);
        }
        return new WorkDto(
                work.getId(),
                work.getTitle(),
                work.getCoverUrl(),
                work.getType().name(),
                work.getTotalEpisodes(),
                work.getApiSource(),
                userStatus
        );
    }

    @Transactional
    public void deleteWork(Long workId) {
        Work work = workRepository.findById(workId)
                .orElseThrow(() -> new ResourceNotFoundException("作品不存在"));
        // 级联删除关联数据
        commentRepository.deleteByWorkId(workId);
        userWorkRepository.deleteByWorkId(workId);
        workRepository.delete(work);
    }
}
