package com.ssafy.italian_brainrot.repository;

import com.ssafy.italian_brainrot.entity.CharacterCard;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CharacterCardRepository extends JpaRepository<CharacterCard, Integer> {

}
