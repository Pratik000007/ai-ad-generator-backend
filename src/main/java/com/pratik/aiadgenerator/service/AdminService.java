package com.pratik.aiadgenerator.service;

import com.pratik.aiadgenerator.dto.AdminDashboardResponse;
import com.pratik.aiadgenerator.repository.UserRepository;
import com.pratik.aiadgenerator.repository.AdRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final UserRepository userRepository;
    private final AdRepository adRepository;

    public AdminDashboardResponse getDashboardStats() {

        long totalUsers = userRepository.count();
        long totalAds = adRepository.count();

        return new AdminDashboardResponse(totalUsers, totalAds);
    }
}
