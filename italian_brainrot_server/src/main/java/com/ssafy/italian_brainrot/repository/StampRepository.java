package com.ssafy.italian_brainrot.repository;

import com.ssafy.italian_brainrot.entity.Stamp;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StampRepository extends JpaRepository<Stamp, Integer> {

}
