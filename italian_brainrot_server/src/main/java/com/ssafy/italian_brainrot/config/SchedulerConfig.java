package com.ssafy.italian_brainrot.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@Configuration
@EnableScheduling  // 스케줄링 기능 활성화
@EnableAsync       // 비동기 처리 활성화
public class SchedulerConfig {
}