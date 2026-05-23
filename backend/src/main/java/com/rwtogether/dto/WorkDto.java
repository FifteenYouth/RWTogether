package com.rwtogether.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WorkDto {

    private Long id;
    private String title;
    private String coverUrl;
    private String type;
    private Integer totalEpisodes;
    private String apiSource;

    // 当前用户对该作品的状态（WANT / WATCHING / DONE / DROPPED）
    private String status;
}
