package com.pratik.aiadgenerator.service;


import com.pratik.aiadgenerator.dto.AnalyticsResponse;
import com.pratik.aiadgenerator.entity.User;
import com.pratik.aiadgenerator.repository.AdRepository;
import com.pratik.aiadgenerator.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AnalyticsService {

    private final AdRepository adRepository;
    private final UserRepository userRepository;

    public AnalyticsResponse getUserAnalytics(String email) {

        User user = userRepository.findByEmail(email)
                .orElseThrow();

        long totalAds = adRepository.countByUser(user);

        long adsThisMonth = adRepository.countByUserAndCreatedAtAfter(
                user,
                LocalDateTime.now().minusMonths(1)
        );

        long adsLast7Days = adRepository.countByUserAndCreatedAtAfter(
                user,
                LocalDateTime.now().minusDays(7)
        );

        List<Object[]> results = adRepository.countAdsByPlatform(user);

        Map<String, Long> platformMap = new HashMap<>();

        for (Object[] row : results) {
            platformMap.put(
                    row[0].toString(),
                    (Long) row[1]
            );
        }

        return new AnalyticsResponse(
                totalAds,
                adsThisMonth,
                adsLast7Days,
                platformMap
        );
    }
}
