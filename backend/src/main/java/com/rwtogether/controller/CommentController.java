package com.rwtogether.controller;

import com.rwtogether.dto.CommentDto;
import com.rwtogether.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    @GetMapping("/api/works/{workId}/comments")
    public ResponseEntity<List<CommentDto>> list(@PathVariable Long workId, Authentication auth) {
        Long userId = (Long) auth.getPrincipal();
        return ResponseEntity.ok(commentService.getWorkComments(workId, userId));
    }

    @PostMapping("/api/works/{workId}/comments")
    public ResponseEntity<CommentDto> create(@PathVariable Long workId,
                                              @RequestBody Map<String, Object> body,
                                              Authentication auth) {
        Long userId = (Long) auth.getPrincipal();
        String content = (String) body.get("content");
        String type = (String) body.getOrDefault("type", "REVIEW");
        Integer episodeNum = body.get("episodeNum") != null
                ? ((Number) body.get("episodeNum")).intValue() : null;
        Integer seasonNum = body.get("seasonNum") != null
                ? ((Number) body.get("seasonNum")).intValue() : null;
        return ResponseEntity.ok(commentService.addComment(userId, workId, content, type, episodeNum, seasonNum));
    }

    @PostMapping("/api/comments/{id}/reply")
    public ResponseEntity<CommentDto> reply(@PathVariable Long id,
                                             @RequestBody Map<String, String> body,
                                             Authentication auth) {
        Long userId = (Long) auth.getPrincipal();
        return ResponseEntity.ok(commentService.replyComment(userId, id, body.get("content")));
    }

    @PostMapping("/api/comments/{id}/like")
    public ResponseEntity<Map<String, Object>> toggleLike(@PathVariable Long id, Authentication auth) {
        Long userId = (Long) auth.getPrincipal();
        boolean liked = commentService.toggleLike(userId, id);
        return ResponseEntity.ok(Map.of("liked", liked));
    }
}
