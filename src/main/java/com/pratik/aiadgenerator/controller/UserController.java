package com.pratik.aiadgenerator.controller;


import com.pratik.aiadgenerator.dto.UserDto;
import com.pratik.aiadgenerator.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import com.pratik.aiadgenerator.service.UserService;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/me")
    public ResponseEntity<UserDto> getCurrentUser(Authentication authentication) {

        String email = authentication.getName();
        User user = userService.findByEmail(email);

        return ResponseEntity.ok(new UserDto(user));
    }
}
