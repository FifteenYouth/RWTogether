package com.rwtogether.service.external;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.Duration;
import java.util.*;

@Service
@Slf4j
public class TmdbService {

    /** TMDB 动画(Animation)类型 ID，用于把番剧从普通电视剧中识别出来 */
    private static final int GENRE_ANIMATION = 16;

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

    /**
     * 搜索电视剧/番剧。TMDB 不区分番剧与电视剧，这里通过 genre 16(动画) 把番剧标记为 ANIME。
     * @param onlyAnime true=只返回番剧(ANIME)，false=只返回非动画电视剧(DRAMA)，null=全部返回(按 genre 自动归类)
     */
    public List<Map<String, Object>> searchTv(String keyword, Boolean onlyAnime) {
        List<Map<String, Object>> all = searchTmdb(keyword, "tv", null);
        if (onlyAnime == null) {
            return all;
        }
        List<Map<String, Object>> filtered = new ArrayList<>();
        for (Map<String, Object> work : all) {
            boolean isAnime = "ANIME".equals(work.get("type"));
            if (onlyAnime == isAnime) {
                filtered.add(work);
            }
        }
        return filtered;
    }

    public List<Map<String, Object>> searchMovie(String keyword) {
        return searchTmdb(keyword, "movie", "MOVIE");
    }

    /**
     * @param forceType 强制类型；为 null 时对 tv 结果按 genre 自动归类(ANIME/DRAMA)
     */
    private List<Map<String, Object>> searchTmdb(String keyword, String endpoint, String forceType) {
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
                    .timeout(Duration.ofSeconds(10))
                    .block();

            if (result == null || result.get("results") == null) {
                return Collections.emptyList();
            }

            List<Map<String, Object>> list = (List<Map<String, Object>>) result.get("results");
            return parseTmdbResults(list, endpoint, forceType);
        } catch (Exception e) {
            log.error("TMDB 搜索失败 ({}): {}", endpoint, e.getMessage());
            return Collections.emptyList();
        }
    }

    private List<Map<String, Object>> parseTmdbResults(List<Map<String, Object>> list, String endpoint, String forceType) {
        List<Map<String, Object>> results = new ArrayList<>();
        boolean isTv = "tv".equals(endpoint);
        for (Map<String, Object> item : list) {
            Map<String, Object> work = new HashMap<>();
            String titleField = isTv ? "name" : "title";
            String type = forceType != null
                    ? forceType
                    : (isTv ? (hasAnimationGenre(item.get("genre_ids")) ? "ANIME" : "DRAMA") : "MOVIE");
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

    @SuppressWarnings("unchecked")
    private boolean hasAnimationGenre(Object genreIds) {
        if (genreIds instanceof List<?> ids) {
            for (Object id : ids) {
                if (id instanceof Number n && n.intValue() == GENRE_ANIMATION) {
                    return true;
                }
            }
        }
        return false;
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
        return getTmdbDetail("tv", tmdbId, null);
    }

    public Map<String, Object> getMovieDetail(String tmdbId) {
        return getTmdbDetail("movie", tmdbId, "MOVIE");
    }

    /**
     * @param forceType 强制类型；为 null 时对 tv 详情按 genres 自动归类(ANIME/DRAMA)
     */
    private Map<String, Object> getTmdbDetail(String endpoint, String tmdbId, String forceType) {
        try {
            Map result = webClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/" + endpoint + "/{id}")
                            .queryParam("api_key", apiKey)
                            .queryParam("language", "zh-CN")
                            .build(tmdbId))
                    .retrieve()
                    .bodyToMono(Map.class)
                    .timeout(Duration.ofSeconds(10))
                    .block();

            if (result == null) return Collections.emptyMap();

            boolean isTv = "tv".equals(endpoint);
            String titleField = isTv ? "name" : "title";
            String type = forceType != null
                    ? forceType
                    : (isTv ? (hasAnimationGenreObjects(result.get("genres")) ? "ANIME" : "DRAMA") : "MOVIE");

            Map<String, Object> detail = new HashMap<>();
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

            // 电视剧/番剧：按季拉取真实分集
            if (isTv) {
                int seasons = result.get("number_of_seasons") instanceof Number n ? n.intValue() : 0;
                List<Map<String, Object>> episodes = getEpisodes(tmdbId, seasons);
                detail.put("episodes", episodes);
            }

            return detail;
        } catch (Exception e) {
            log.error("TMDB 详情获取失败 ({}): {}", endpoint, e.getMessage());
            return Collections.emptyMap();
        }
    }

    @SuppressWarnings("unchecked")
    private boolean hasAnimationGenreObjects(Object genres) {
        if (genres instanceof List<?> list) {
            for (Object g : list) {
                if (g instanceof Map<?, ?> m && m.get("id") instanceof Number n
                        && n.intValue() == GENRE_ANIMATION) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 按季拉取电视剧/番剧的全部分集。
     * 调用 /tv/{id}/season/{n} 获取每季的 episodes，展平后返回。
     */
    public List<Map<String, Object>> getEpisodes(String tmdbId, int numberOfSeasons) {
        if (numberOfSeasons <= 0) {
            return Collections.emptyList();
        }
        List<Map<String, Object>> episodes = new ArrayList<>();
        for (int season = 1; season <= numberOfSeasons; season++) {
            final int seasonNum = season;
            try {
                Map result = webClient.get()
                        .uri(uriBuilder -> uriBuilder
                                .path("/tv/{id}/season/{season}")
                                .queryParam("api_key", apiKey)
                                .queryParam("language", "zh-CN")
                                .build(tmdbId, seasonNum))
                        .retrieve()
                        .bodyToMono(Map.class)
                        .timeout(Duration.ofSeconds(10))
                        .block();

                if (result == null || result.get("episodes") == null) {
                    continue;
                }

                List<Map<String, Object>> eps = (List<Map<String, Object>>) result.get("episodes");
                for (Map<String, Object> ep : eps) {
                    Map<String, Object> episode = new HashMap<>();
                    episode.put("episodeNum", ep.get("episode_number"));
                    episode.put("seasonNum", ep.getOrDefault("season_number", seasonNum));
                    episode.put("title", ep.get("name"));
                    episode.put("airDate", ep.get("air_date"));
                    episode.put("thumbnailUrl", ep.get("still_path") != null
                            ? imageBaseUrl + ep.get("still_path") : "");
                    episode.put("description", ep.get("overview"));
                    episodes.add(episode);
                }
            } catch (Exception e) {
                log.warn("TMDB 第 {} 季分集获取失败: {}", seasonNum, e.getMessage());
            }
        }
        log.info("TMDB 拉取分集完成，共 {} 集 (ID={})", episodes.size(), tmdbId);
        return episodes;
    }
}
