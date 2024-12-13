package com.dodream.job.notification;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.boot.autoconfigure.batch.BatchProperties;
import org.springframework.boot.autoconfigure.batch.JobLauncherApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.util.StringUtils;

@Slf4j
@Configuration
@RequiredArgsConstructor
@EnableConfigurationProperties(BatchProperties.class)           // Spring Batch 관련 속성 활성화
public class NotificationBatchConfig {

    private final DeleteNotificationTasklet deleteNotificationTasklet;

    /*
    jobLauncherApplicationRunner 메서드
    역할: Spring Batch에서 작업 실행 시 초기화 및 실행을 담당하는 객체를 생성한다.
    조건
    @ConditionalOnMissingBean: 동일한 타입의 Bean이 없을 때만 생성된다.
    @ConditionalOnProperty: spring.batch.job.enabled 속성이 true이거나 없을 경우 활성화된다.
     */

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnProperty(prefix = "spring.batch.job", name = "enabled", havingValue = "true", matchIfMissing = true)
    public JobLauncherApplicationRunner jobLauncherApplicationRunner(JobLauncher jobLauncher,
        JobExplorer jobExplorer, JobRepository jobRepository, BatchProperties properties) {
        JobLauncherApplicationRunner runner = new JobLauncherApplicationRunner(jobLauncher, jobExplorer, jobRepository);
        String jobNames = properties.getJob().getName();
        if (StringUtils.hasText(jobNames)) {
            runner.setJobName(jobNames);
        }
        log.info("jobLauncherApplicationRunner 실행");
        return runner;
    }

    /*
    메서드 이름: "deleteNotificationStep"
    실행 로직: deleteNotificationTasklet
    트랜잭션 관리: PlatformTransactionManager로 처리
    옵션: allowStartIfComplete(true)를 통해 작업이 완료된 상태에서도 다시 실행 가능하도록 설정.
     */

    @Bean
    public Job deleteNotificationJob(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        return new JobBuilder("deleteNotificationJob", jobRepository)
            .start(deleteNotificationStep(jobRepository, transactionManager))
            .build();
    }

    @Bean
    @JobScope
    public Step deleteNotificationStep(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        return new StepBuilder("deleteNotificationStep", jobRepository)
            .tasklet(deleteNotificationTasklet, transactionManager)
            .allowStartIfComplete(true)
            .build();
    }
}
