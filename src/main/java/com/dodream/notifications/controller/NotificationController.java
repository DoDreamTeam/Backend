package com.dodream.notifications.controller;

import com.dodream.notifications.domain.NotificationResponse;
import com.dodream.notifications.domain.UpdateNotificationRequest;
import com.dodream.notifications.domain.UpdateNotificationResponse;
import com.dodream.notifications.service.NotificationService;
import com.dodream.user.entity.User;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RequiredArgsConstructor
@RequestMapping("/api/notification")
@RestController
public class NotificationController {

    private final NotificationService notificationService;

    @GetMapping(value = "", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public ResponseEntity<SseEmitter> subscribe(@AuthenticationPrincipal User user,
        @RequestHeader(value = "Last-Event-ID", required = false, defaultValue = "") String lastEventId) {
        return ResponseEntity.ok(notificationService.subscribe(user, lastEventId));
    }

    // 사용자별 알림 조회
    @GetMapping("/{userId}")
    public ResponseEntity<List<NotificationResponse>> getNotifications(@PathVariable("userId") Long userId) {
        List<NotificationResponse> notifications = notificationService.getNotificationsByUserId(userId);
        return ResponseEntity.ok(notifications);
    }

    // 삭제 - 사용자별 알림 삭제
    @DeleteMapping("/{notificationId}")
    public ResponseEntity<NotificationResponse> deleteNotifications(
        @PathVariable("notificationId") Long notificationId, @AuthenticationPrincipal User user) {
        notificationService.deleteNotificationById(notificationId, user);
        return ResponseEntity.noContent().build();
    }

    // 수정 - 알림 읽음, 안 읽음
    @PatchMapping("/{notificationId}")
    public ResponseEntity<UpdateNotificationResponse> updateNotifications(
        @PathVariable("notificationId") Long notificationId, @AuthenticationPrincipal User user,
        @RequestBody UpdateNotificationRequest updateNotificationRequest) {
        UpdateNotificationResponse updateNotificationResponse =
            notificationService.updateNotificationReadStatus(notificationId, user, updateNotificationRequest);
        return ResponseEntity.ok(updateNotificationResponse);
    }
}
