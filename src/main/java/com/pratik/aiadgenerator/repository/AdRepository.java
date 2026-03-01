package com.pratik.aiadgenerator.repository;

import com.pratik.aiadgenerator.dto.MonthlyTrendDTO;
import com.pratik.aiadgenerator.entity.Ad;
import com.pratik.aiadgenerator.entity.Product;
import com.pratik.aiadgenerator.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDateTime;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface AdRepository extends JpaRepository<Ad, Long> {
    List<Ad> findByProduct(Product product);

    long countByProduct_User(User user);

    List<Ad> findByUserEmail(String email);


    long countByUser(User user);

    long countByUserAndCreatedAtAfter(User user, LocalDateTime date);


    //long countByProduct_UserAndPlatform(User user, String platform);

    long countByUserAndPlatform(User user, String platform);

    @Query("SELECT a.platform, COUNT(a) FROM Ad a WHERE a.user = :user GROUP BY a.platform")
   // List<Object[]> countAdsByPlatform(User user);


    List<Object[]> countAdsByPlatform(@Param("user") User user);


    @Query("""
    SELECT 
        FUNCTION('DATE_FORMAT', a.createdAt, '%Y-%m') as month,
        COUNT(a) as count
    FROM Ad a
    WHERE a.user = :user
    GROUP BY FUNCTION('DATE_FORMAT', a.createdAt, '%Y-%m')
    ORDER BY FUNCTION('DATE_FORMAT', a.createdAt, '%Y-%m')
    """)
   // List<Object[]> getMonthlyTrendByUser(@Param("user") User user);
      //  List<MonthlyTrendDTO> getMonthlyTrend();

         List<Object[]> getMonthlyTrendByUser(@Param("user") User user);


}
