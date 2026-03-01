package com.pratik.aiadgenerator.controller;

import com.pratik.aiadgenerator.dto.AdRequest;
import com.pratik.aiadgenerator.entity.Ad;
import com.pratik.aiadgenerator.service.AdService;
import lombok.RequiredArgsConstructor;
//import org.apache.tomcat.util.net.openssl.ciphers.Authentication;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;

import java.util.List;

@RestController
@RequestMapping("/api/ads")
@RequiredArgsConstructor
public class AdController {

    private final AdService adService;


    @PostMapping("/generate/{productId}")
    public ResponseEntity<?> generate(@PathVariable Long productId, Authentication authentication) {

        System.out.println("INSIDE GENERATE ADS CONTROLLER");

        String email = authentication.getName();

        return ResponseEntity.ok(adService.generateAds(productId, email));


    }


    // 🔹 Generate directly from request
    @PostMapping("/generate")
    public ResponseEntity<?> generateFromRequest(
            @RequestBody AdRequest request, Authentication authentication) {

        String email = authentication.getName();

        return ResponseEntity.ok(
                adService.generateFromRequest(request, email)
        );
    }

    // ✅ GET ALL ADS FOR LOGGED-IN USER
    @GetMapping
    public ResponseEntity<List<Ad>> getMyAds(Authentication authentication) {

        String email = authentication.getName();

        return ResponseEntity.ok(
                adService.getAdsByUser(email)
        );
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteAd(
            @PathVariable Long id,
            Authentication authentication
    ) {
        String email = authentication.getName();
        adService.deleteAd(id, email);
        return ResponseEntity.ok("Ad deleted successfully");
    }



}

