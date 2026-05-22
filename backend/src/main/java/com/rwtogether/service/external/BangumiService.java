package com.rwtogether.service.external;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

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
            Map result = webClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/search/subject/{keyword}")
                            .queryParam("type", 2)
                            .queryParam("responseGroup", "small")
                            .queryParam("max_results", 10)
                            .build(keyword))
                    .retrieve()
                    .bodyToMono(Map.class)
                    .block();

            if (result == null || result.get("list") == null) {
                return Collections.emptyList();
            }

            List<Map<String, Object>> list = (List<Map<String, Object>>) result.get("list");
            List<Map<String, Object>> results = new ArrayList<>();

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
            Map result = webClient.get()
                    .uri("/subject/{id}", bgmId)
                    .retrieve()
                    .bodyToMono(Map.class)
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
            return detail;
        } catch (Exception e) {
            log.error("Bangumi 详情获取失败: {}", e.getMessage());
            return Collections.emptyMap();
        }
    }
}
