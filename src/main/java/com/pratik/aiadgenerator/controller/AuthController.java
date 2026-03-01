package com.pratik.aiadgenerator.controller;

import com.pratik.aiadgenerator.dto.AuthRequest;
import com.pratik.aiadgenerator.dto.AuthResponse;
import com.pratik.aiadgenerator.entity.User;
import com.pratik.aiadgenerator.enums.Role;
import com.pratik.aiadgenerator.repository.UserRepository;
import com.pratik.aiadgenerator.security.JwtService;
import com.pratik.aiadgenerator.service.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final EmailService emailService;

    private static final String ADMIN_EMAIL = "mendhepratik24@gmail.com";

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody AuthRequest request) {

        User user = new User();
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));


        if (request.getEmail().equalsIgnoreCase(ADMIN_EMAIL)) {
            user.setRole(Role.ADMIN);
        } else {
            user.setRole(Role.USER);
        }


        userRepository.save(user);

        return ResponseEntity.ok("User Registered Successfully");
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(
            @RequestBody AuthRequest request) {

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!passwordEncoder.matches(
                request.getPassword(),
                user.getPassword())) {
            throw new RuntimeException("Invalid Credentials");
        }

        //String token = jwtService.generateToken(user.getEmail());
        String token = jwtService.generateToken(user);

        return ResponseEntity.ok(new AuthResponse(token, user.getRole().name()));
    }


    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        String token = UUID.randomUUID().toString();
        user.setResetToken(token);
        user.setTokenExpiry(LocalDateTime.now().plusMinutes(15));
        userRepository.save(user);

        // ✅ 2. Replace the TODO with the actual service call
        emailService.sendResetEmail(user.getEmail(), token);

        return ResponseEntity.ok("Reset link sent to email");
    }

    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody Map<String, String> request) {
        String token = request.get("token");
        String newPassword = request.get("newPassword");

        User user = userRepository.findByResetToken(token)
                .filter(u -> u.getTokenExpiry().isAfter(LocalDateTime.now()))
                .orElseThrow(() -> new RuntimeException("Invalid or expired token"));

        user.setPassword(passwordEncoder.encode(newPassword));
        user.setResetToken(null); // Clear token so it can't be used again
        user.setTokenExpiry(null);
        userRepository.save(user);

        return ResponseEntity.ok("Password updated successfully");
    }
}

