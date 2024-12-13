package com.dodream.common.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;

/*
Spring 동작 흐름
1. Spring 애플리케이션이 시작되면 SchedulerConfig 클래스가 실행되어 사용자 정의
ThreadPoolTaskScheduler가 생성 및 초기화
2. ScheduledTaskRegistrar는 해당 스레드 풀을 사용하여 스케쥴링 작업 실행
3. 모든 스케쥴링 작업(@Scheduled)은 최대 4개의 스레드로 동시에 처리됨
 */

@Configuration
public class SchedulerConfig implements SchedulingConfigurer {

    // 기본 스케쥴링 설정 정의
    // configureTasks 메서드를 통해 멀티 스레드 환경 설정 가능
    // (스레드 풀 크기 변경, 다양한 스케쥴링 전략 추가, 스레드 안전성 보장)
    @Override
    public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
        ThreadPoolTaskScheduler threadPoolTaskScheduler = new ThreadPoolTaskScheduler();

        threadPoolTaskScheduler.setPoolSize(4);
        threadPoolTaskScheduler.setThreadGroupName("dodream-scheduler thread-pool");
        threadPoolTaskScheduler.setThreadNamePrefix("dodream-scheduler");
        threadPoolTaskScheduler.initialize();

        taskRegistrar.setTaskScheduler(threadPoolTaskScheduler);
    }
}
