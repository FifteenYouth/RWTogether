package com.rwtogether.service.external;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.*;

@Service
@Slf4j
public class TmdbService {

    private final WebClient webClient;
    private final String apiKey;
    private final String imageBaseUrl;

    public TmdbService(@Value("${api.tmdb.base-url}") String baseUrl,
                       @Value("${api.tmdb.api-key}") String apiKey,
                       @Value("${api.tmdb.image-base-url}") String imageBaseUrl) {
        this.apiKey = apiKey;
        this.imageBaseUrl = imageBaseUrl;
        this.webClient = WebClient.builder()
                .baseUrl(baseUrl)
                .build();
    }

    public List<Map<String, Object>> searchTv(String keyword) {
        return searchTmdb(keyword, "tv", "DRAMA");
    }

    public List<Map<String, Object>> searchMovie(String keyword) {
        return searchTmdb(keyword, "movie", "MOVIE");
    }

    private List<Map<String, Object>> searchTmdb(String keyword, String endpoint, String type) {
        try {
            Map result = webClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/search/" + endpoint)
                            .queryParam("api_key", apiKey)
                            .queryParam("query", keyword)
                            .queryParam("language", "zh-CN")
                            .queryParam("page", 1)
                            .build())
                    .retrieve()
                    .bodyToMono(Map.class)
                    .block();

            if (result == null || result.get("results") == null) {
                return Collections.emptyList();
            }

            List<Map<String, Object>> list = (List<Map<String, Object>>) result.get("results");
            return parseTmdbResults(list, type);
        } catch (Exception e) {
            log.error("TMDB 搜索失败 ({}): {}", endpoint, e.getMessage());
            return Collections.emptyList();
        }
    }

    private List<Map<String, Object>> parseTmdbResults(List<Map<String, Object>> list, String type) {
        List<Map<String, Object>> results = new ArrayList<>();
        for (Map<String, Object> item : list) {
            Map<String, Object> work = new HashMap<>();
            String titleField = "MOVIE".equals(type) ? "title" : "name";
            work.put("title", item.get(titleField));
            work.put("coverUrl", item.get("poster_path") != null
                    ? imageBaseUrl + item.get("poster_path")
                    : "");
            work.put("description", item.get("overview"));
            work.put("apiSource", "tmdb");
            work.put("apiId", String.valueOf(item.get("id")));
            work.put("type", type);
            work.put("totalEpisodes", item.getOrDefault("number_of_episodes", 0));
            work.put("totalSeasons", item.getOrDefault("number_of_seasons", 0));
            results.add(work);
        }
        return results;
    }

    public Map<String, Object> getDetail(String tmdbId) {
        // 尝试先获取 TV 详情，失败则获取 Movie 详情
        Map<String, Object> detail = getTvDetail(tmdbId);
        if (detail.isEmpty()) {
            detail = getMovieDetail(tmdbId);
        }
        return detail;
    }

    public Map<String, Object> getTvDetail(String tmdbId) {
        return getTmdbDetail("tv", tmdbId, "DRAMA");
    }

    public Map<String, Object> getMovieDetail(String tmdbId) {
        return getTmdbDetail("movie", tmdbId, "MOVIE");
    }

    private Map<String, Object> getTmdbDetail(String endpoint, String tmdbId, String type) {
        try {
            Map result = webClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/" + endpoint + "/{id}")
                            .queryParam("api_key", apiKey)
                            .queryParam("language", "zh-CN")
                            .build(tmdbId))
                    .retrieve()
                    .bodyToMono(Map.class)
                    .block();

            if (result == null) return Collections.emptyMap();

            Map<String, Object> detail = new HashMap<>();
            String titleField = "MOVIE".equals(type) ? "title" : "name";
            detail.put("title", result.get(titleField));
            detail.put("coverUrl", result.get("poster_path") != null
                    ? imageBaseUrl + result.get("poster_path") : "");
            detail.put("description", result.get("overview"));
            detail.put("apiSource", "tmdb");
            detail.put("apiId", String.valueOf(result.get("id")));
            detail.put("type", type);
            detail.put("totalEpisodes", result.getOrDefault("number_of_episodes", 0));
            detail.put("totalSeasons", result.getOrDefault("number_of_seasons", 0));
            detail.put("metadata", result);
            return detail;
        } catch (Exception e) {
            log.error("TMDB 详情获取失败 ({}): {}", endpoint, e.getMessage());
            return Collections.emptyMap();
        }
    }
}
