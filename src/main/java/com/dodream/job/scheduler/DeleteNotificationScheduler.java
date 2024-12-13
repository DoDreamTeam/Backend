package com.dodream.job.scheduler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Slf4j
@Component
public class DeleteNotificationScheduler {

    private final JobLauncher jobLauncher;
    private final Job job;

    /*
    1. 스케쥴링 및 비동기 실행 사용하여 배치 작업 실행
    2. @Async(value = "asyncNotificationExecutor") - 작업 비동기로 실행
    3. 매일 자정에 실행
     */
    @Async(value = "asyncNotificationExecutor")
    @Scheduled(cron = "0 0 0 */1 * *", zone = "Asia/Seoul")
    public void runDeleteNotificationJob() throws JobInstanceAlreadyCompleteException,
        JobExecutionAlreadyRunningException, JobParametersInvalidException, JobRestartException {
        System.out.println("runDeleteNotificationJob");
        jobLauncher.run(job, new JobParameters());
    }

}
