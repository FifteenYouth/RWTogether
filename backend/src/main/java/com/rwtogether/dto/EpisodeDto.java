package com.rwtogether.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EpisodeDto {
    private Long id;
    private Long workId;
    private Integer episodeNum;
    private Integer seasonNum;
    private String title;
    private LocalDate airDate;
    private String thumbnailUrl;

    // 用户观看信息
    private Boolean watched;
    private LocalDateTime watchedAt;
    private Integer rating;
}
