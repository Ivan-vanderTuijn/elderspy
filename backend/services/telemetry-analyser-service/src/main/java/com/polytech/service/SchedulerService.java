package com.polytech.service;

import io.quarkus.scheduler.Scheduler;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ApplicationScoped
public class SchedulerService {
    @Inject
    Scheduler scheduler;

    public SchedulerService() {}

    public void schedule(TelemetryAnalyser analyser, String jobName, String interval) {
        cancelSchedule(jobName);
        scheduler.newJob(jobName)
                .setInterval(interval)
                .setTask(executionContext -> {
                    log.info("Running analysis job: {}", jobName);
                    analyser.analyse();
                })
                .schedule();
        log.info("Scheduling job: {}", jobName);
    }

    public void cancelSchedule(String jobName) {
        log.info("Cancelling job: {}", jobName);
        scheduler.unscheduleJob(jobName);
    }
}
