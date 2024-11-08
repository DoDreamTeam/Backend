package com.dodream.notifications.service;

import com.dodream.common.exception.BaseException;
import com.dodream.common.exception.ErrorCode;
import com.dodream.notifications.domain.NotificationResponse;
import com.dodream.notifications.domain.UpdateNotificationRequest;
import com.dodream.notifications.domain.UpdateNotificationResponse;
import com.dodream.notifications.entity.Notifications;
import com.dodream.notifications.enumtype.NotifyType;
import com.dodream.notifications.repository.EmitterRepository;
import com.dodream.notifications.repository.NotificationRepository;
import com.dodream.user.entity.User;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Service
@Slf4j
@RequiredArgsConstructor
public class NotificationService {

    // 기본 타임아웃 설정 - 연결 지속시간 1시간
    private static final Long DEFAULT_TIMEOUT = 60L * 1000 * 60;

    private final EmitterRepository emitterRepository;
    private final NotificationRepository notificationRepository;

    // 클라이언트가 구독을 위해 호출하는 메서드
    // (userId - 구독하는 클라이언트 사용자 아이디, SseEmitter - 서버에서 보낸 이벤트 Emitter)
    public SseEmitter subscribe(User user, String lastEventId)  {
        String emitterId = user.getUsername() + "_" + System.currentTimeMillis();
        SseEmitter emitter = emitterRepository.save(emitterId, new SseEmitter(DEFAULT_TIMEOUT));

        emitter.onCompletion(() -> emitterRepository.deleteById(emitterId));
        emitter.onTimeout(() -> {
            emitterRepository.deleteById(emitterId);
        });

        sendToClient(emitter, emitterId, "EventStream Created. [userId = " + user.getId() + "]");

        if (!lastEventId.isEmpty()) {
            Map<String, Object> events = emitterRepository.findAllEventCacheStartWithByUserId(String.valueOf(user.getId()));
            events.entrySet().stream()
                .filter(entry -> lastEventId.compareTo(entry.getKey()) < 0)
                .forEach(entry -> sendToClient(emitter, entry.getKey(), entry.getValue()));
        }
        return emitter;
    }

    public void notifyDoDreamClient(User user, NotifyType notifyType, String content, String url, String toName) {
        Notifications notifications = notificationRepository.save(
            Objects.requireNonNull(createNotification(user, notifyType, content, url, toName)));

        Map<String, SseEmitter> sseEmitters = emitterRepository.findAllEmitterStartWithByUsername(user.getUsername());

        NotificationResponse notificationResponse = NotificationResponse.fromEntity(notifications);

        sseEmitters.forEach(
            (key, emitter) -> {
                emitterRepository.saveEventCache(key, notifications);
                sendToDoDreamClient(emitter, key, notificationResponse);
            }
        );
    }

    private Notifications createNotification(User user, NotifyType notifyType,
        String content, String url, String toName) {
        if (notifyType.equals(NotifyType.STUDY_APPLY)) {                        // 스터디 가입 신청
            return Notifications.builder()
                .user(user)
                .isRead(false)
                .notifyType(NotifyType.STUDY_APPLY)
                .content(content)
                .url(url)
                .createdAt(LocalDateTime.now())
                .toName(toName)
                .build();
        } else if (notifyType.equals(NotifyType.STUDY_APPROVAL)) {              // 스터디 가입 승인
            return Notifications.builder()
                .user(user)
                .isRead(false)
                .notifyType(NotifyType.STUDY_APPROVAL)
                .content(content)
                .url(url)
                .createdAt(LocalDateTime.now())
                .toName(toName)
                .build();
        } else if (notifyType.equals(NotifyType.LEADER_CHANGE)) {               // 스터디 방장 변경
            return Notifications.builder()
                .user(user)
                .isRead(false)
                .notifyType(NotifyType.STUDY_APPROVAL)
                .content(content)
                .url(url)
                .createdAt(LocalDateTime.now())
                .toName(toName)
                .build();
        } else if (notifyType.equals(NotifyType.STUDY_REFUSAL)) {             // 스터디 가입 거절
            return Notifications.builder()
                .user(user)
                .isRead(false)
                .notifyType(NotifyType.STUDY_REFUSAL)
                .content(content)
                .url(url)
                .createdAt(LocalDateTime.now())
                .toName(toName)
                .build();
        } else if (notifyType.equals(NotifyType.STUDY_MEMBER_WITHDRAW)) {       // 스터디 멤버 탈퇴
            return Notifications.builder()
                .user(user)
                .isRead(false)
                .notifyType(NotifyType.STUDY_MEMBER_WITHDRAW)
                .content(content)
                .url(url)
                .createdAt(LocalDateTime.now())
                .toName(toName)
                .build();
        } else if (notifyType.equals(NotifyType.QUESTION_COMMENT)) {            // 스터디 문제 댓글
            return Notifications.builder()
                .user(user)
                .isRead(false)
                .notifyType(NotifyType.QUESTION_COMMENT)
                .content(content)
                .url(url)
                .createdAt(LocalDateTime.now())
                .toName(toName)
                .build();
        } else if (notifyType.equals(NotifyType.BOOK_COMMENT)) {                // 내 문제집 댓글
            return Notifications.builder()
                .user(user)
                .isRead(false)
                .notifyType(NotifyType.BOOK_COMMENT)
                .content(content)
                .url(url)
                .createdAt(LocalDateTime.now())
                .toName(toName)
                .build();
        } else {
            return null;
        }
    }

    private void sendToDoDreamClient(SseEmitter emitter, String emitterId,
        NotificationResponse notificationResponse) {
        try {
            emitter.send(SseEmitter.event()
                .id(emitterId)
                .name("SSE")
                .data(notificationResponse, MediaType.APPLICATION_JSON));
        } catch (IOException exception) {
            emitterRepository.deleteById(emitterId);
            emitter.completeWithError(exception);
            log.error("Failed to send data to client for emitter ID: {}. Error: {}", emitterId, exception.getMessage());
        }
    }

    private void sendToClient(SseEmitter emitter, String emitterId, Object data) {
        try {
            emitter.send(SseEmitter.event()
                .id(emitterId)
                .name("SSE")
                .data(data, MediaType.APPLICATION_JSON));
        } catch (IOException exception) {
            emitterRepository.deleteById(emitterId);
            emitter.completeWithError(exception);
            log.error("Failed to send data to client for emitter ID: {}. Error: {}", emitterId, exception.getMessage());
        }
    }

    @Transactional(readOnly = true)
    public List<NotificationResponse> getNotificationsByUserId(Long userId) {
        List<Notifications> notifications = notificationRepository.findByUserIdOrderByCreatedAtDesc(userId);
        return notifications.stream().map(NotificationResponse::fromEntity).toList();
    }

    @Transactional
    public void deleteNotificationById(Long notificationId, User user) {
        Notifications notifications = notificationRepository.findById(notificationId)
            .orElseThrow(() -> new BaseException(ErrorCode.NOTIFICATION_NOT_FOUND));

        if (!notifications.getUser().getId().equals(user.getId())) {
            throw new BaseException(ErrorCode.ACCESS_DENIED);
        }

        notificationRepository.deleteById(notifications.getId());
    }

    @Transactional
    public UpdateNotificationResponse updateNotificationReadStatus(
        Long notificationId, User user, UpdateNotificationRequest updateNotificationRequest) {
        Notifications notifications = notificationRepository.findById(notificationId)
            .orElseThrow(() -> new BaseException(ErrorCode.NOTIFICATION_NOT_FOUND));

        if (!notifications.getUser().getId().equals(user.getId())) {
            throw new BaseException(ErrorCode.ACCESS_DENIED);
        }

        notifications.updateNotificationsReadStatus(updateNotificationRequest.isRead());
        return UpdateNotificationResponse.fromEntity(notifications);
    }
}
