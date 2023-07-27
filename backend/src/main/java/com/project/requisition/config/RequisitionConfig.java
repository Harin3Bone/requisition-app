package com.project.requisition.config;

import com.project.requisition.constants.RequisitionProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Clock;
import java.time.ZoneId;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class RequisitionConfig {

    private final RequisitionProperties requisitionProperties;

    @Bean
    public Clock systemClock() {
        log.info("System timezone: {}", requisitionProperties.getTimezone());
        return Clock.system(ZoneId.of(requisitionProperties.getTimezone()));
    }
}
