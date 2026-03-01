package com.pratik.aiadgenerator.security;

import com.pratik.aiadgenerator.entity.User;
import com.pratik.aiadgenerator.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email)
            throws UsernameNotFoundException {

        //User user = userRepository.findByEmail(email)
        //        .orElseThrow(() ->
        //                new UsernameNotFoundException("User not found"));

       // return org.springframework.security.core.userdetails.User
       //         .withUsername(user.getEmail())
        //        .password(user.getPassword())
                //.authorities("ROLE_" + user.getRole())
        //        .authorities(user.getRole().name())
         //       .build();

        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }

    }


