package com.rwtogether.service.external;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.Duration;
import java.util.*;

@Service
@Slf4j
public class BookApiService {

    private final WebClient webClient;

    public BookApiService(@Value("${api.google-books.base-url}") String baseUrl) {
        this.webClient = WebClient.builder()
                .baseUrl(baseUrl)
                .build();
    }

    public List<Map<String, Object>> searchBooks(String keyword) {
        try {
            Map result = webClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/volumes")
                            .queryParam("q", keyword)
                            .queryParam("langRestrict", "zh")
                            .queryParam("maxResults", 10)
                            .build())
                    .retrieve()
                    .bodyToMono(Map.class)
                    .timeout(Duration.ofSeconds(3))
                    .block();

            if (result == null || result.get("items") == null) {
                return Collections.emptyList();
            }

            List<Map<String, Object>> items = (List<Map<String, Object>>) result.get("items");
            List<Map<String, Object>> results = new ArrayList<>();

            for (Map<String, Object> item : items) {
                Map<String, Object> volumeInfo = (Map<String, Object>) item.get("volumeInfo");
                if (volumeInfo == null) continue;

                Map<String, Object> work = new HashMap<>();
                work.put("title", volumeInfo.get("title"));

                // 封面 URL 替换 http 为 https
                String coverUrl = "";
                if (volumeInfo.get("imageLinks") != null) {
                    Map<String, Object> imageLinks = (Map<String, Object>) volumeInfo.get("imageLinks");
                    coverUrl = String.valueOf(imageLinks.getOrDefault("thumbnail", ""));
                    coverUrl = coverUrl.replace("http:", "https:");
                }
                work.put("coverUrl", coverUrl);
                work.put("description", volumeInfo.get("description"));
                work.put("apiSource", "google_books");
                work.put("apiId", String.valueOf(item.get("id")));
                work.put("type", "BOOK");
                work.put("totalEpisodes", 0);

                // 提取页数作为 metadata
                Map<String, Object> metadata = new HashMap<>();
                metadata.put("authors", volumeInfo.get("authors"));
                metadata.put("pageCount", volumeInfo.get("pageCount"));
                metadata.put("publisher", volumeInfo.get("publisher"));
                metadata.put("publishedDate", volumeInfo.get("publishedDate"));
                work.put("metadata", metadata);

                results.add(work);
            }

            return results;
        } catch (Exception e) {
            log.error("Google Books 搜索失败: {}", e.getMessage());
            return Collections.emptyList();
        }
    }

    public Map<String, Object> getBookDetail(String volumeId) {
        try {
            Map result = webClient.get()
                    .uri("/volumes/{id}", volumeId)
                    .retrieve()
                    .bodyToMono(Map.class)
                    .timeout(Duration.ofSeconds(3))
                    .block();

            if (result == null) return Collections.emptyMap();

            Map<String, Object> volumeInfo = (Map<String, Object>) result.get("volumeInfo");
            if (volumeInfo == null) return Collections.emptyMap();

            Map<String, Object> detail = new HashMap<>();
            detail.put("title", volumeInfo.get("title"));

            String coverUrl = "";
            if (volumeInfo.get("imageLinks") != null) {
                Map<String, Object> imageLinks = (Map<String, Object>) volumeInfo.get("imageLinks");
                coverUrl = String.valueOf(imageLinks.getOrDefault("large", imageLinks.getOrDefault("thumbnail", "")));
                coverUrl = coverUrl.replace("http:", "https:");
            }
            detail.put("coverUrl", coverUrl);
            detail.put("description", volumeInfo.get("description"));
            detail.put("apiSource", "google_books");
            detail.put("apiId", String.valueOf(result.get("id")));
            detail.put("type", "BOOK");
            detail.put("totalEpisodes", 0);

            Map<String, Object> metadata = new HashMap<>();
            metadata.put("authors", volumeInfo.get("authors"));
            metadata.put("pageCount", volumeInfo.get("pageCount"));
            metadata.put("publisher", volumeInfo.get("publisher"));
            metadata.put("publishedDate", volumeInfo.get("publishedDate"));
            metadata.put("categories", volumeInfo.get("categories"));
            metadata.put("averageRating", volumeInfo.get("averageRating"));
            detail.put("metadata", metadata);

            return detail;
        } catch (Exception e) {
            log.error("Google Books 详情获取失败: {}", e.getMessage());
            return Collections.emptyMap();
        }
    }
}
