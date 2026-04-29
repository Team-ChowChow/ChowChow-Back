package com.petdiet.notification.dto;

import com.petdiet.notification.entity.Notification;
import lombok.Builder;
import lombok.Getter;

import java.time.OffsetDateTime;

@Getter
@Builder
public class NotificationResponse {
    private Integer notificationId;
    private String notificationType;
    private String notificationTitle;
    private String title;
    private String notificationContent;
    private String message;
    private Integer relatedId;
    private Integer targetId;
    private String targetType;
    private String deepLink;
    private Boolean isRead;
    private OffsetDateTime createdAt;

    public static NotificationResponse from(Notification n) {
        return NotificationResponse.builder()
                .notificationId(n.getNotificationId())
                .notificationType(n.getNotificationType())
                .notificationTitle(n.getNotificationTitle())
                .title(n.getNotificationTitle())
                .notificationContent(n.getNotificationContent())
                .message(n.getNotificationContent())
                .relatedId(n.getRelatedId())
                .targetId(n.getRelatedId())
                .targetType(null)
                .deepLink(null)
                .isRead(n.getIsRead())
                .createdAt(n.getCreatedAt())
                .build();
    }
}
