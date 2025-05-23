package com.ssafy.italian_brainrot.repository;

import com.ssafy.italian_brainrot.entity.OrderDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderDetailRepository extends JpaRepository<OrderDetail, Integer> {

    List<OrderDetail> findAllByProduct_Id(int productId);
}
