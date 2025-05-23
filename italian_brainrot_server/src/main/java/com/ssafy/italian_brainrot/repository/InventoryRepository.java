package com.ssafy.italian_brainrot.repository;

import com.ssafy.italian_brainrot.entity.Inventory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InventoryRepository extends JpaRepository<Inventory, Integer> {

    List<Inventory> findByUserId(String userId);
}
