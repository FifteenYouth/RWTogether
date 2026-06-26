package com.rwtogether.controller;

import com.rwtogether.dto.EpisodeDto;
import com.rwtogether.service.EpisodeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/works/{workId}/episodes")
@RequiredArgsConstructor
public class EpisodeController {

    private final EpisodeService episodeService;

    @GetMapping
    public ResponseEntity<List<EpisodeDto>> list(@PathVariable Long workId, Authentication auth) {
        Long userId = (Long) auth.getPrincipal();
        return ResponseEntity.ok(episodeService.getWorkEpisodes(workId, userId));
    }

    @PostMapping("/{episodeId}/toggle")
    public ResponseEntity<EpisodeDto> toggleWatched(@PathVariable Long workId,
                                                     @PathVariable Long episodeId,
                                                     Authentication auth) {
        Long userId = (Long) auth.getPrincipal();
        return ResponseEntity.ok(episodeService.toggleWatched(episodeId, userId));
    }

    @PostMapping("/{episodeId}/rate")
    public ResponseEntity<EpisodeDto> rate(@PathVariable Long workId,
                                           @PathVariable Long episodeId,
                                           @RequestBody Map<String, Integer> body,
                                           Authentication auth) {
        Long userId = (Long) auth.getPrincipal();
        Integer rating = body.get("rating");
        return ResponseEntity.ok(episodeService.rateEpisode(episodeId, userId, rating));
    }
}
