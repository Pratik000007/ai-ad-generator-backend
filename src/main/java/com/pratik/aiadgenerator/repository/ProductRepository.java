package com.pratik.aiadgenerator.repository;

import com.pratik.aiadgenerator.entity.Product;
import com.pratik.aiadgenerator.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long> {
    List<Product> findByUser(User user);

    Optional<Product> findByIdAndUser(Long id, User user);

    long countByUser(User user);

}


