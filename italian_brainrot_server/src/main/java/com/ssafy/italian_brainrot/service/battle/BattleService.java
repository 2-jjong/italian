package com.ssafy.italian_brainrot.service.battle;

import com.ssafy.italian_brainrot.dto.battle.BattleDTO;
import com.ssafy.italian_brainrot.dto.battle.BattleRequestDTO;

import java.util.List;

public interface BattleService {

    /**
     * 배틀 생성
     * - 중복 배틀 체크 (이미 생성한 배틀이 있으면 실패)
     * - 새 배틀 생성 (WAITING 상태)
     * - 1분 후 자동 취소 스케줄링
     *
     * @param userId 배틀 생성자 ID (쿠키에서 추출)
     * @param request 배틀 생성 요청 정보
     * @return 생성된 배틀 ID (-1이면 실패)
     */
    int createBattle(String userId, BattleRequestDTO request);

    /**
     * 배틀 수락
     * - 배틀 존재 및 상태 확인 (WAITING 상태여야 함)
     * - 배틀 상태를 RUNNING으로 변경
     * - GPT API 호출하여 배틀 진행
     * - user1, user2에게 배틀 시작 FCM 전송
     * - 1분 후 결과 FCM 전송 스케줄링
     *
     * @param userId 배틀 수락자 ID (쿠키에서 추출)
     * @param request 배틀 수락 요청 정보
     * @return 배틀 ID (-1이면 실패)
     */
    int acceptBattle(String userId, BattleRequestDTO request);

    /**
     * 배틀 결과 조회
     *
     * @param battleId 배틀 ID
     * @return 배틀 정보
     */
    BattleDTO getBattle(int battleId);

    /**
     * 특정 사용자의 배틀 내역 조회
     *
     * @param userId 사용자 ID
     * @return 배틀 내역 목록 (생성시간 내림차순)
     */
    List<BattleDTO> getUserBattles(String userId);

    /**
     * 만료된 WAITING 배틀 자동 취소 (스케줄링)
     */
    void cancelExpiredBattles();

    /**
     * RUNNING 배틀 결과 처리 및 FCM 전송 (스케줄링)
     */
    void processRunningBattles();
}