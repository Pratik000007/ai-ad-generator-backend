package com.pratik.aiadgenerator.controller;

import com.pratik.aiadgenerator.dto.ProductRequest;
import com.pratik.aiadgenerator.entity.Product;
import com.pratik.aiadgenerator.entity.User;
import com.pratik.aiadgenerator.repository.ProductRepository;
import com.pratik.aiadgenerator.repository.UserRepository;
import com.pratik.aiadgenerator.service.AdService;
import lombok.RequiredArgsConstructor;
//import org.apache.tomcat.util.net.openssl.ciphers.Authentication;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;
import com.pratik.aiadgenerator.dto.ProductResponse;


import java.util.List;


@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final AdService adService;


    @PostMapping
    public ResponseEntity<ProductResponse> createProduct(
            @RequestBody ProductRequest request,
            Authentication authentication) {

        User user = userRepository
                .findByEmail(authentication.getName())
                .orElseThrow();

        Product product = new Product();
        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setTargetAudience(request.getTargetAudience());
        product.setUser(user);

        productRepository.save(product);

       // return ResponseEntity.ok(product);

        ProductResponse response = new ProductResponse(
                product.getId(),
                product.getName(),
                product.getDescription(),
                product.getTargetAudience()
        );

        return ResponseEntity.ok(response);

    }

    // ✅ GET ALL PRODUCTS FOR LOGGED-IN USER
    @GetMapping
    public ResponseEntity<List<ProductResponse>> getMyProducts(Authentication authentication) {

        User user = userRepository
                .findByEmail(authentication.getName())
                .orElseThrow();

        //List<Product> products = productRepository.findByUser(user);

        List<ProductResponse> response = productRepository
                .findByUser(user)
                .stream()
                .map(p -> new ProductResponse(
                        p.getId(),
                        p.getName(),
                        p.getDescription(),
                        p.getTargetAudience()
                ))
                .toList();


        //return ResponseEntity.ok(products);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{productId}/ads")
    public ResponseEntity<?> getAdsByProduct(
            @PathVariable Long productId,
            Authentication authentication) {

        return ResponseEntity.ok(
                adService.getAdsByProduct(productId, authentication.getName())
        );
    }


}

