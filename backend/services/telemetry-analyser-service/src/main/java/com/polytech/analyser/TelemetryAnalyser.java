package com.polytech.analyser;

import io.quarkus.scheduler.ScheduledExecution;

public interface TelemetryAnalyser {
    void analyse(ScheduledExecution execution);
}
