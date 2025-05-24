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

    /**
     * 특정 사용자의 활성 배틀 조회 (WAITING 상태)
     */
    Optional<Battle> findByUserid1AndState(String userId, BattleState state);

    /**
     * 특정 사용자의 모든 배틀 내역 조회 (생성시간 내림차순)
     */
    @Query("SELECT b FROM Battle b WHERE b.userid1 = :userId OR b.userid2 = :userId ORDER BY b.createdAt DESC")
    List<Battle> findByUserOrderByCreatedAtDesc(@Param("userId") String userId);

    /**
     * 1분 이상 지난 WAITING 상태 배틀 조회 (자동 취소용)
     */
    @Query("SELECT b FROM Battle b WHERE b.state = :state AND b.createdAt < :cutoffTime")
    List<Battle> findExpiredWaitingBattles(@Param("state") BattleState state, @Param("cutoffTime") LocalDateTime cutoffTime);

    /**
     * RUNNING 상태 배틀 조회 (결과 처리용)
     */
    List<Battle> findByState(BattleState state);
}