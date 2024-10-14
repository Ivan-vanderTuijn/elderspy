package com.polytech.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AnalyserConfiguration {
    private String interval;
    private String timespan;
    private Double threshold;
}
