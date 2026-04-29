package com.petdiet.notification.controller;

import com.petdiet.config.SupabasePrincipal;
import com.petdiet.notification.dto.NotificationResponse;
import com.petdiet.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    @GetMapping
    public ResponseEntity<List<NotificationResponse>> getNotifications(
            @AuthenticationPrincipal SupabasePrincipal principal) {
        return ResponseEntity.ok(notificationService.getNotifications(principal.authUuid()));
    }

    @GetMapping("/unread-count")
    public ResponseEntity<Map<String, Long>> getUnreadCount(
            @AuthenticationPrincipal SupabasePrincipal principal) {
        long count = notificationService.getUnreadCount(principal.authUuid());
        return ResponseEntity.ok(Map.of("count", count));
    }

    @PatchMapping("/{notificationId}/read")
    public ResponseEntity<NotificationResponse> readNotification(
            @AuthenticationPrincipal SupabasePrincipal principal,
            @PathVariable Integer notificationId) {
        return ResponseEntity.ok(notificationService.readNotification(principal.authUuid(), notificationId));
    }

    @PatchMapping("/read-all")
    public ResponseEntity<Void> readAllNotifications(
            @AuthenticationPrincipal SupabasePrincipal principal) {
        notificationService.readAllNotifications(principal.authUuid());
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{notificationId}")
    public ResponseEntity<NotificationResponse> getNotification(
            @AuthenticationPrincipal SupabasePrincipal principal,
            @PathVariable Integer notificationId) {
        return ResponseEntity.ok(notificationService.readNotification(principal.authUuid(), notificationId));
    }

    @GetMapping("/settings")
    public ResponseEntity<?> getNotificationSettings(
            @AuthenticationPrincipal SupabasePrincipal principal) {
        boolean enabled = notificationService.getNotificationEnabled(principal.authUuid());
        return ResponseEntity.ok(Map.of("isNotificationEnabled", enabled));
    }

    @PatchMapping("/settings")
    public ResponseEntity<?> updateNotificationSettings(
            @AuthenticationPrincipal SupabasePrincipal principal,
            @RequestBody Map<String, Object> body) {
        boolean requested = Boolean.TRUE.equals(body.get("isNotificationEnabled"));
        boolean enabled = notificationService.updateNotificationEnabled(principal.authUuid(), requested);
        return ResponseEntity.ok(Map.of(
                "isNotificationEnabled", enabled,
                "message", "알림 설정이 수정되었습니다."
        ));
    }

    @DeleteMapping("/{notificationId}")
    public ResponseEntity<Void> deleteNotification(
            @AuthenticationPrincipal SupabasePrincipal principal,
            @PathVariable Integer notificationId) {
        notificationService.deleteNotification(principal.authUuid(), notificationId);
        return ResponseEntity.noContent().build();
    }
}
