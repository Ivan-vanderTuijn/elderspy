package com.polytech.dti;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Configuration {
    private String interval;
    private String timespan;
    private Double threshold;
}
