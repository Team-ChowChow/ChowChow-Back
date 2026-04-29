package com.petdiet.search.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/search")
public class SearchController {

    @GetMapping("/recent")
    public ResponseEntity<?> getRecentSearches() {
        return ResponseEntity.ok(Map.of("recent", List.of(), "totalCount", 0));
    }

    @GetMapping("/popular")
    public ResponseEntity<?> getPopularSearches() {
        return ResponseEntity.ok(Map.of("popular", List.of(), "totalCount", 0));
    }

    @PostMapping("/log")
    public ResponseEntity<?> saveSearchLog(@RequestBody Map<String, Object> body) {
        return ResponseEntity.ok(Map.of(
                "searchKeyword", body.getOrDefault("searchKeyword", ""),
                "message", "검색 기록이 저장되었습니다."
        ));
    }

    @DeleteMapping("/{searchLogId}")
    public ResponseEntity<?> deleteSearchLog(@PathVariable Long searchLogId) {
        return ResponseEntity.ok(Map.of("searchLogId", searchLogId, "message", "검색 기록이 삭제되었습니다."));
    }

    @DeleteMapping("/recent/all")
    public ResponseEntity<?> deleteAllSearchLogs() {
        return ResponseEntity.ok(Map.of("message", "전체 검색 기록이 삭제되었습니다."));
    }
}
