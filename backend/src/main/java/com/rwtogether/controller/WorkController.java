package com.rwtogether.controller;

import com.rwtogether.dto.ProgressDto;
import com.rwtogether.dto.WorkDetailDto;
import com.rwtogether.dto.WorkDto;
import com.rwtogether.service.ProgressService;
import com.rwtogether.service.WorkService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/works")
@RequiredArgsConstructor
public class WorkController {

    private final WorkService workService;
    private final ProgressService progressService;

    @GetMapping
    public ResponseEntity<List<WorkDto>> list(@RequestParam(required = false) String type,
                                              @RequestParam(required = false) String status,
                                              Authentication auth) {
        Long userId = (Long) auth.getPrincipal();
        return ResponseEntity.ok(workService.getUserWorks(userId, type, status));
    }

    @GetMapping("/{id}")
    public ResponseEntity<WorkDetailDto> detail(@PathVariable Long id, Authentication auth) {
        Long userId = (Long) auth.getPrincipal();
        return ResponseEntity.ok(workService.getWorkDetail(id, userId));
    }

    @GetMapping("/search")
    public ResponseEntity<List<Map<String, Object>>> search(@RequestParam String q,
                                                             @RequestParam(required = false, defaultValue = "all") String type) {
        return ResponseEntity.ok(workService.searchWorks(q, type));
    }

    @PostMapping
    public ResponseEntity<WorkDto> create(@RequestBody Map<String, Object> workData, Authentication auth) {
        Long userId = (Long) auth.getPrincipal();
        WorkDto work = workService.createWorkManual(workData, userId);
        // 自动为当前用户标记为"想看"
        progressService.setProgress(userId, work.getId(), "WANT", null, null);
        // 重新查询以获取更新后的状态
        work.setStatus("WANT");
        return ResponseEntity.ok(work);
    }

    @PostMapping("/import")
    public ResponseEntity<WorkDto> importFromApi(@RequestBody Map<String, Object> importData, Authentication auth) {
        Long userId = (Long) auth.getPrincipal();
        String apiSource = (String) importData.get("apiSource");
        String apiId = (String) importData.get("apiId");
        WorkDto work = workService.importFromApi(apiSource, apiId, importData, userId);
        // 自动为当前用户标记为"想看"
        progressService.setProgress(userId, work.getId(), "WANT", null, null);
        // 重新查询以获取更新后的状态
        work.setStatus("WANT");
        return ResponseEntity.ok(work);
    }
}
