package com.pratik.aiadgenerator.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
public class Ad {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String headline;

    @Column(length = 2000)
    private String description;

    private String cta;

    private String platform;

    private LocalDateTime createdAt;

    @ManyToOne
    private Product product;


    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;


    @PrePersist
    public void prePersist() {
    this.createdAt = LocalDateTime.now();
    }
}