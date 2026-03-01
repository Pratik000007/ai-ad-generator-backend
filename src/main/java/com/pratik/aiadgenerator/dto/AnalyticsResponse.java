package com.pratik.aiadgenerator.dto;



import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Map;

@Data
@AllArgsConstructor
public class AnalyticsResponse {

    private long totalAds;
    private long adsThisMonth;
    private long adsLast7Days;
    private Map<String, Long> platformBreakdown;
}
