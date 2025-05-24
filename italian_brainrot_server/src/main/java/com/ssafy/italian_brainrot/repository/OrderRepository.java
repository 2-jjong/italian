package com.ssafy.italian_brainrot.repository;

import com.ssafy.italian_brainrot.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Integer> {

    List<Order> findByUserIdOrderByTimeStampDesc(String userId);

    @Query("SELECT o FROM Order o WHERE o.user.id = :userId AND o.timeStamp >= :startDate ORDER BY o.timeStamp DESC")
    List<Order> findByUserIdAndRecentMonths(@Param("userId") String userId, @Param("startDate") LocalDateTime startDate);
}
