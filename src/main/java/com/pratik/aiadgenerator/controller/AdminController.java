package com.pratik.aiadgenerator.controller;


import com.pratik.aiadgenerator.dto.AdminDashboardResponse;
import com.pratik.aiadgenerator.service.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;

    @GetMapping("/dashboard")
    public AdminDashboardResponse getDashboard() {
        return adminService.getDashboardStats();
    }
}
