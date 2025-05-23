package com.ssafy.italian_brainrot.repository;

import com.ssafy.italian_brainrot.entity.ResourceCard;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ResourceCardRepository extends JpaRepository<ResourceCard, Integer> {

}
