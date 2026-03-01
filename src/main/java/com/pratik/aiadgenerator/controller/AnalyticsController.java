package com.pratik.aiadgenerator.controller;

import com.pratik.aiadgenerator.entity.User;
//import com.pratik.aiadgenerator.model.User;
import com.pratik.aiadgenerator.repository.AdRepository;
import com.pratik.aiadgenerator.repository.ProductRepository;
import com.pratik.aiadgenerator.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/analytics")
@RequiredArgsConstructor
public class AnalyticsController {

    private final AdRepository adRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    @GetMapping
    public ResponseEntity<?> getAnalytics(Authentication authentication) {

        String email = authentication.getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // ===== Basic Stats =====
        long totalProducts = productRepository.countByUser(user);
       // long totalAds = adRepository.countByProduct_User(user);
        long totalAds = adRepository.countByUser(user);

        long adsThisMonth = adRepository.countByUserAndCreatedAtAfter(
                user,
                LocalDateTime.now().minusMonths(1)
        );

        long adsLast7Days = adRepository.countByUserAndCreatedAtAfter(
                user,
                LocalDateTime.now().minusDays(7)
        );

        // ===== Platform Breakdown =====
        long instagramAds = adRepository.countByUserAndPlatform(user, "INSTAGRAM");
        long facebookAds = adRepository.countByUserAndPlatform(user, "FACEBOOK");
        long linkedinAds = adRepository.countByUserAndPlatform(user, "LINKEDIN");
        long googleAds = adRepository.countByUserAndPlatform(user, "GOOGLE");

        Map<String, Object> platformStats = new HashMap<>();
        platformStats.put("instagram", instagramAds);
        platformStats.put("facebook", facebookAds);
        platformStats.put("linkedin", linkedinAds);
        platformStats.put("google", googleAds);

        // ===== Final Response =====
        Map<String, Object> data = new HashMap<>();
        data.put("totalProducts", totalProducts);
        data.put("totalAds", totalAds);
        data.put("adsThisMonth", adsThisMonth);
        data.put("adsLast7Days", adsLast7Days);
        data.put("platformBreakdown", platformStats);

        return ResponseEntity.ok(data);
    }

    @GetMapping("/monthly-trend")
    public ResponseEntity<?> getMonthlyTrend(Authentication authentication) {

        String email = authentication.getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        var results = adRepository.getMonthlyTrendByUser(user);

        var trendList = results.stream().map(obj -> {
            Map<String, Object> map = new HashMap<>();
            map.put("month", obj[0]);
            map.put("count", obj[1]);
            return map;
        }).toList();

        return ResponseEntity.ok(trendList);
    }

}