package com.rwtogether.service.external;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.Duration;
import java.util.*;

@Service
@Slf4j
public class BangumiService {

    private final WebClient webClient;

    public BangumiService(@Value("${api.bangumi.base-url}") String baseUrl) {
        this.webClient = WebClient.builder()
                .baseUrl(baseUrl)
                .defaultHeader("User-Agent", "RWTogether/1.0")
                .build();
    }

    public List<Map<String, Object>> searchAnime(String keyword) {
        try {
            log.info("开始搜索 Bangumi: {}", keyword);

            Map result = webClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/search/subject/{keyword}")
                            .queryParam("type", 2)
                            .queryParam("responseGroup", "small")
                            .queryParam("max_results", 10)
                            .build(keyword))
                    .retrieve()
                    .bodyToMono(Map.class)
                    .timeout(Duration.ofSeconds(10))  // 增加到 10 秒
                    .block();

            if (result == null || result.get("list") == null) {
                log.warn("Bangumi 搜索返回空结果");
                return Collections.emptyList();
            }

            List<Map<String, Object>> list = (List<Map<String, Object>>) result.get("list");
            List<Map<String, Object>> results = new ArrayList<>();

            log.info("Bangumi 搜索成功，找到 {} 个结果", list.size());

            for (Map<String, Object> item : list) {
                Map<String, Object> work = new HashMap<>();
                work.put("title", item.get("name"));
                work.put("coverUrl", item.get("images") != null
                        ? ((Map<String, Object>) item.get("images")).get("medium")
                        : "");
                work.put("description", item.get("summary"));
                work.put("apiSource", "bangumi");
                work.put("apiId", String.valueOf(item.get("id")));
                work.put("type", "ANIME");
                work.put("totalEpisodes", item.get("eps"));
                results.add(work);
            }

            return results;
        } catch (Exception e) {
            log.error("Bangumi 搜索失败: {}", e.getMessage());
            return Collections.emptyList();
        }
    }

    public Map<String, Object> getAnimeDetail(String bgmId) {
        try {
            log.info("获取 Bangumi 详情: ID={}", bgmId);

            Map result = webClient.get()
                    .uri("/subject/{id}", bgmId)
                    .retrieve()
                    .bodyToMono(Map.class)
                    .timeout(Duration.ofSeconds(10))  // 增加到 10 秒
                    .block();

            if (result == null) {
                return Collections.emptyMap();
            }

            Map<String, Object> detail = new HashMap<>();
            detail.put("title", result.get("name"));
            detail.put("coverUrl", result.get("images") != null
                    ? ((Map<String, Object>) result.get("images")).get("large")
                    : "");
            detail.put("description", result.get("summary"));
            detail.put("apiSource", "bangumi");
            detail.put("apiId", String.valueOf(result.get("id")));
            detail.put("type", "ANIME");
            detail.put("totalEpisodes", result.get("eps"));
            detail.put("metadata", result);

            // 获取分集信息
            List<Map<String, Object>> episodes = getEpisodes(bgmId);
            detail.put("episodes", episodes);

            return detail;
        } catch (Exception e) {
            log.error("Bangumi 详情获取失败: {}", e.getMessage());
            return Collections.emptyMap();
        }
    }

    /**
     * 获取作品的分集列表
     */
    public List<Map<String, Object>> getEpisodes(String bgmId) {
        try {
            log.info("Fetching episodes for Bangumi ID: {}", bgmId);

            List result = webClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/episodes")
                            .queryParam("subject_id", bgmId)
                            .build())
                    .retrieve()
                    .bodyToMono(List.class)
                    .timeout(Duration.ofSeconds(10))  // 增加到 10 秒
                    .block();

            if (result == null || result.isEmpty()) {
                log.warn("No episodes found for Bangumi ID: {}", bgmId);
                return Collections.emptyList();
            }

            List<Map<String, Object>> episodes = new ArrayList<>();
            for (Object item : result) {
                if (item instanceof Map) {
                    Map<String, Object> ep = (Map<String, Object>) item;

                    // 只保留正式集（排除 SP、OP、ED 等）
                    Integer type = ep.get("type") != null ? ((Number) ep.get("type")).intValue() : 0;
                    if (type != 0) {
                        continue; // type=0 是正片，其他是特别篇等
                    }

                    Map<String, Object> episode = new HashMap<>();
                    episode.put("episodeNum", ep.get("sort")); // 集数
                    episode.put("title", ep.get("name")); // 标题
                    episode.put("titleCn", ep.get("name_cn")); // 中文标题
                    episode.put("airDate", ep.get("airdate")); // 播出日期
                    episode.put("duration", ep.get("duration")); // 时长
                    episode.put("description", ep.get("desc")); // 描述

                    episodes.add(episode);
                }
            }

            log.info("Fetched {} episodes for Bangumi ID: {}", episodes.size(), bgmId);
            return episodes;

        } catch (Exception e) {
            log.error("获取 Bangumi 分集失败: {}", e.getMessage());
            return Collections.emptyList();
        }
    }
}
