package com.rwtogether.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "episode")
public class Episode {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "work_id", nullable = false)
    private Work work;

    @Column(name = "episode_num", nullable = false)
    private Integer episodeNum;

    @Column(name = "season_num")
    private Integer seasonNum = 1;

    @Column(length = 200)
    private String title = "";

    @Column(name = "air_date")
    private LocalDate airDate;

    @Column(name = "thumbnail_url", length = 500)
    private String thumbnailUrl = "";
}
