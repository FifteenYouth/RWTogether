package com.rwtogether.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProgressDto {

    private Long id;
    private String status;
    private String progressDetail;
    private Integer rating;
    private LocalDate startedAt;
    private LocalDate finishedAt;
}
