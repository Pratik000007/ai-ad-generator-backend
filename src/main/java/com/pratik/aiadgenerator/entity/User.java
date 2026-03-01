package com.pratik.aiadgenerator.entity;

import com.pratik.aiadgenerator.enums.Role;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

@Entity
@Getter
@Setter
public class User  implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @Column(unique = true)
    private String email;

    private String password;

    private String resetToken;
    private LocalDateTime tokenExpiry;

   // public String getRole() {
   //     return "USER";


    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;



// 2. THIS IS THE KEY: Map your Role to Spring Authorities
@Override
public Collection<? extends GrantedAuthority> getAuthorities() {
    // This sends the role string (e.g., "ADMIN") to the filter
    return List.of(new SimpleGrantedAuthority(role.name()));
}

@Override
public String getUsername() {
    return email;
}

@Override
public boolean isAccountNonExpired() { return true; }

@Override
public boolean isAccountNonLocked() { return true; }

@Override
public boolean isCredentialsNonExpired() { return true; }

@Override
public boolean isEnabled() { return true; }
}