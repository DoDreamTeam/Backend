package com.dodream;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing              // 자동 값 등록
@EnableCaching                  // 레디스 캐싱
public class DodreamSampleApplication {

    public static void main(String[] args) {
        SpringApplication.run(DodreamSampleApplication.class, args);
    }

}
