package com.dodream.job.notification;

import com.dodream.notifications.entity.Notifications;
import com.dodream.notifications.repository.NotificationRepository;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@StepScope
@RequiredArgsConstructor
public class DeleteNotificationTasklet implements Tasklet {

    private final NotificationRepository notificationRepository;
    @Override
    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) {
        log.info("start deleting notifications : {}", LocalDateTime.now());
        List<Notifications> notificationsList = notificationRepository.findAllByCreatedAtBefore(LocalDateTime.now().minusDays(1));
        notificationRepository.deleteAll(notificationsList);
        return RepeatStatus.FINISHED;
    }

}
