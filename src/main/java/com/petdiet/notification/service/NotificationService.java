package com.petdiet.notification.service;

import com.petdiet.auth.entity.User;
import com.petdiet.auth.repository.UserRepository;
import com.petdiet.notification.dto.NotificationResponse;
import com.petdiet.notification.entity.Notification;
import com.petdiet.notification.repository.NotificationRepository;
import com.petdiet.user.entity.UserSettings;
import com.petdiet.user.repository.UserSettingsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;
    private final UserSettingsRepository userSettingsRepository;

    @Transactional(readOnly = true)
    public List<NotificationResponse> getNotifications(UUID authUuid) {
        User user = findUser(authUuid);
        return notificationRepository.findAllByUserOrderByCreatedAtDesc(user).stream()
                .map(NotificationResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public long getUnreadCount(UUID authUuid) {
        User user = findUser(authUuid);
        return notificationRepository.countByUserAndIsRead(user, false);
    }

    @Transactional
    public NotificationResponse readNotification(UUID authUuid, Integer notificationId) {
        User user = findUser(authUuid);
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new IllegalArgumentException("알림을 찾을 수 없습니다."));
        if (!notification.getUser().getUserId().equals(user.getUserId())) {
            throw new IllegalArgumentException("알림을 찾을 수 없습니다.");
        }
        notification.markAsRead();
        return NotificationResponse.from(notification);
    }

    @Transactional
    public void readAllNotifications(UUID authUuid) {
        User user = findUser(authUuid);
        notificationRepository.markAllAsReadByUser(user);
    }

    @Transactional
    public void deleteNotification(UUID authUuid, Integer notificationId) {
        User user = findUser(authUuid);
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new IllegalArgumentException("알림을 찾을 수 없습니다."));
        if (!notification.getUser().getUserId().equals(user.getUserId())) {
            throw new IllegalArgumentException("알림을 찾을 수 없습니다.");
        }
        notificationRepository.delete(notification);
    }

    @Transactional(readOnly = true)
    public boolean getNotificationEnabled(UUID authUuid) {
        User user = findUser(authUuid);
        UserSettings settings = userSettingsRepository.findById(user.getUserId())
                .orElseGet(() -> userSettingsRepository.save(UserSettings.builder().user(user).build()));
        return settings.getIsNotificationEnabled();
    }

    @Transactional
    public boolean updateNotificationEnabled(UUID authUuid, boolean enabled) {
        User user = findUser(authUuid);
        UserSettings settings = userSettingsRepository.findById(user.getUserId())
                .orElseGet(() -> UserSettings.builder().user(user).build());
        settings.update(enabled, null, null);
        return userSettingsRepository.save(settings).getIsNotificationEnabled();
    }

    private User findUser(UUID authUuid) {
        return userRepository.findByAuthUuid(authUuid)
                .orElseThrow(() -> new IllegalStateException("유저를 찾을 수 없습니다."));
    }
}
