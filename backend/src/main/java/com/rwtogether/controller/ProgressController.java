package com.rwtogether.controller;

import com.rwtogether.dto.ProgressDto;
import com.rwtogether.service.ProgressService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/works/{workId}/progress")
@RequiredArgsConstructor
public class ProgressController {

    private final ProgressService progressService;

    @PostMapping
    public ResponseEntity<ProgressDto> setProgress(@PathVariable Long workId,
                                                    @RequestBody Map<String, Object> body,
                                                    Authentication auth) {
        Long userId = (Long) auth.getPrincipal();
        String status = (String) body.get("status");
        String progressDetail = (String) body.get("progressDetail");
        Integer rating = body.get("rating") != null ? ((Number) body.get("rating")).intValue() : null;
        return ResponseEntity.ok(progressService.setProgress(userId, workId, status, progressDetail, rating));
    }

    @PutMapping
    public ResponseEntity<ProgressDto> updateProgress(@PathVariable Long workId,
                                                      @RequestBody Map<String, Object> body,
                                                      Authentication auth) {
        Long userId = (Long) auth.getPrincipal();
        String status = (String) body.get("status");
        String progressDetail = (String) body.get("progressDetail");
        Integer rating = body.get("rating") != null ? ((Number) body.get("rating")).intValue() : null;
        return ResponseEntity.ok(progressService.setProgress(userId, workId, status, progressDetail, rating));
    }

    @DeleteMapping
    public ResponseEntity<Void> removeProgress(@PathVariable Long workId, Authentication auth) {
        Long userId = (Long) auth.getPrincipal();
        progressService.removeProgress(userId, workId);
        return ResponseEntity.noContent().build();
    }
}
