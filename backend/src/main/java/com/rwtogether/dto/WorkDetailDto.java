package com.rwtogether.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WorkDetailDto {

    private Long id;
    private String title;
    private String coverUrl;
    private String type;
    private Integer totalEpisodes;
    private Integer totalSeasons;
    private String description;
    private String apiSource;
    private Object metadata;
    private ProgressDto myProgress;
    private List<UserProgressDto> friendProgresses;
    private List<CommentDto> comments;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UserProgressDto {
        private Long userId;
        private String username;
        private String avatar;
        private String status;
        private String progressDetail;
        private Integer rating;
    }
}
