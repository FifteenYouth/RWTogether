package com.rwtogether.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CommentDto {

    private Long id;
    private Long userId;
    private String username;
    private String avatar;
    private String content;
    private String type;
    private Integer episodeNum;
    private Integer seasonNum;
    private Long parentId;
    private String createdAt;
    private long likeCount;
    private boolean likedByMe;
    private List<CommentDto> replies;
}
