package com.ssafy.italian_brainrot.repository;

import com.ssafy.italian_brainrot.entity.Battle;
import com.ssafy.italian_brainrot.enumerate.BattleState;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface BattleRepository extends JpaRepository<Battle, Integer> {

    Optional<Battle> findByUserid1AndState(String userId, BattleState state);

    @Query("SELECT b FROM Battle b WHERE b.userid1 = :userId OR b.userid2 = :userId ORDER BY b.createdAt DESC")
    List<Battle> findByUserOrderByCreatedAtDesc(@Param("userId") String userId);

    @Query("SELECT b FROM Battle b WHERE b.state = :state AND b.createdAt < :time")
    List<Battle> findExpiredWaitingBattles(@Param("state") BattleState state, @Param("time") LocalDateTime time);

    List<Battle> findByState(BattleState state);
}