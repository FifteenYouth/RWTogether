package com.rwtogether.service;

import com.rwtogether.dto.CommentDto;
import com.rwtogether.dto.ProgressDto;
import com.rwtogether.dto.WorkDetailDto;
import com.rwtogether.dto.WorkDto;
import com.rwtogether.entity.Work;
import com.rwtogether.exception.BusinessException;
import com.rwtogether.exception.ResourceNotFoundException;
import com.rwtogether.repository.CommentRepository;
import com.rwtogether.repository.UserWorkRepository;
import com.rwtogether.repository.WorkRepository;
import com.rwtogether.service.external.BangumiService;
import com.rwtogether.service.external.BookApiService;
import com.rwtogether.service.external.TmdbService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class WorkService {

    private final WorkRepository workRepository;
    private final UserWorkRepository userWorkRepository;
    private final CommentRepository commentRepository;
    private final ProgressService progressService;
    private final CommentService commentService;
    private final BangumiService bangumiService;
    private final TmdbService tmdbService;
    private final BookApiService bookApiService;

    public List<Map<String, Object>> searchWorks(String keyword, String type) {
        List<Map<String, Object>> results = new ArrayList<>();

        if (type == null || type.equals("all")) {
            results.addAll(bangumiService.searchAnime(keyword));
            results.addAll(tmdbService.searchTv(keyword));
            results.addAll(tmdbService.searchMovie(keyword));
            results.addAll(bookApiService.searchBooks(keyword));
        } else {
            switch (type.toUpperCase()) {
                case "ANIME" -> results.addAll(bangumiService.searchAnime(keyword));
                case "DRAMA" -> results.addAll(tmdbService.searchTv(keyword));
                case "MOVIE" -> results.addAll(tmdbService.searchMovie(keyword));
                case "BOOK" -> results.addAll(bookApiService.searchBooks(keyword));
            }
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

        return workRepository.save(work);
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

        // 按用户进度筛选
        return works.stream()
                .filter(w -> {
                    if (status != null) {
                        return userWorkRepository.findByUserIdAndWorkId(userId, w.getId())
                                .map(uw -> uw.getStatus().name().equals(status))
                                .orElse(false);
                    }
                    return userWorkRepository.findByUserIdAndWorkId(userId, w.getId()).isPresent();
                })
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public WorkDto createWorkManual(Map<String, Object> workData) {
        Work work = findOrCreateWork(workData);
        return toDto(work);
    }

    @Transactional
    public WorkDto importFromApi(String apiSource, String apiId, Map<String, Object> fallbackData) {
        if (apiSource == null || apiId == null) {
            return createWorkManual(fallbackData);
        }

        // 先检查是否已存在
        Optional<Work> existing = workRepository.findByApiSourceAndApiId(apiSource, apiId);
        if (existing.isPresent()) {
            return toDto(existing.get());
        }

        // 尝试从 API 获取详细信息
        Map<String, Object> detailData = fetchApiDetail(apiSource, apiId);
        if (detailData.isEmpty()) {
            // API 获取失败，使用搜索结果中的数据
            detailData = fallbackData;
        }

        Work work = findOrCreateWork(detailData);
        return toDto(work);
    }

    private Map<String, Object> fetchApiDetail(String apiSource, String apiId) {
        try {
            return switch (apiSource) {
                case "bangumi" -> bangumiService.getAnimeDetail(apiId);
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
        return new WorkDto(
                work.getId(),
                work.getTitle(),
                work.getCoverUrl(),
                work.getType().name(),
                work.getTotalEpisodes(),
                work.getApiSource()
        );
    }
}
