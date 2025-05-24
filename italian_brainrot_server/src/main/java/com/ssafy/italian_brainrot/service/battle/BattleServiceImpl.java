package com.ssafy.italian_brainrot.service.battle;

import com.ssafy.italian_brainrot.dto.battle.BattleDTO;
import com.ssafy.italian_brainrot.dto.battle.BattleRequestDTO;
import com.ssafy.italian_brainrot.entity.Battle;
import com.ssafy.italian_brainrot.entity.Card;
import com.ssafy.italian_brainrot.enumerate.BattleState;
import com.ssafy.italian_brainrot.mapper.BattleMapper;
import com.ssafy.italian_brainrot.repository.BattleRepository;
import com.ssafy.italian_brainrot.repository.CharacterCardRepository;
import com.ssafy.italian_brainrot.repository.ResourceCardRepository;
import com.ssafy.italian_brainrot.service.fcm.FcmService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
public class BattleServiceImpl implements BattleService {

    private static final Logger logger = LoggerFactory.getLogger(BattleServiceImpl.class);

    @Autowired
    private BattleRepository battleRepository;

    @Autowired
    private ResourceCardRepository resourceCardRepository;

    @Autowired
    private CharacterCardRepository characterCardRepository;

    @Autowired
    private BattleMapper battleMapper;

    @Autowired
    private GptBattleService gptBattleService;

    @Autowired
    private FcmService fcmService;

    @Override
    @Transactional
    public int createBattle(String userId, BattleRequestDTO request) {
        try {
            // 1. 중복 배틀 체크
            if (hasActiveBattle(userId)) {
                logger.warn("배틀 생성 실패: 이미 활성 배틀이 존재함 - userId={}", userId);
                return -1;
            }

            // 2. 카드 조회
            Card user1Card = getCardById(request.getUser1Card());
            if (user1Card == null) {
                logger.warn("배틀 생성 실패: 카드를 찾을 수 없음 - cardId={}", request.getUser1Card());
                return -1;
            }

            // 3. 배틀 생성
            Battle battle = Battle.builder()
                    .userid1(userId)
                    .user1Card(user1Card)
                    .state(BattleState.WAITING)
                    .createdAt(LocalDateTime.now())
                    .build();

            battle = battleRepository.save(battle);

            logger.debug("배틀 생성 성공: battleId={}, userId={}, cardId={}",
                    battle.getId(), userId, request.getUser1Card());

            return battle.getId();

        } catch (Exception e) {
            logger.error("배틀 생성 중 오류 발생: userId={}", userId, e);
            return -1;
        }
    }

    @Override
    @Transactional
    public int acceptBattle(String userId, BattleRequestDTO request) {
        try {
            // 1. 배틀 조회
            Battle battle = battleRepository.findById(request.getId()).orElse(null);
            if (battle == null) {
                logger.warn("배틀 수락 실패: 배틀을 찾을 수 없음 - battleId={}", request.getId());
                return -1;
            }

            // 2. 배틀 상태 확인
            if (battle.getState() != BattleState.WAITING) {
                logger.warn("배틀 수락 실패: 잘못된 배틀 상태 - battleId={}, state={}",
                        request.getId(), battle.getState());
                return -1;
            }

            // 3. 자기 자신과의 배틀 방지
            if (battle.getUserid1().equals(userId)) {
                logger.warn("배틀 수락 실패: 자기 자신과의 배틀 시도 - battleId={}, userId={}",
                        request.getId(), userId);
                return -1;
            }

            // 4. 카드 조회
            Card user2Card = getCardById(request.getUser2Card());
            if (user2Card == null) {
                logger.warn("배틀 수락 실패: 카드를 찾을 수 없음 - cardId={}", request.getUser2Card());
                return -1;
            }

            // 5. 배틀 정보 업데이트
            battle.setUserid2(userId);
            battle.setUser2Card(user2Card);
            battle.setState(BattleState.RUNNING);
            battleRepository.save(battle);

            // 6. 배틀 시작 FCM 알림 전송
            fcmService.sendBattleStartNotification(battle.getUserid1(), battle.getUserid2(), battle.getId());

            // 7. 비동기로 GPT 배틀 처리 및 1분 후 결과 전송 (백그라운드)
            processBattleAsync(battle);

            logger.debug("배틀 수락 성공: battleId={}, user1={}, user2={}",
                    battle.getId(), battle.getUserid1(), battle.getUserid2());

            return battle.getId();

        } catch (Exception e) {
            logger.error("배틀 수락 중 오류 발생: battleId={}, userId={}", request.getId(), userId, e);
            return -1;
        }
    }

    @Override
    public BattleDTO getBattle(int battleId) {
        try {
            Battle battle = battleRepository.findById(battleId).orElse(null);
            if (battle == null) {
                logger.warn("배틀 조회 실패: 배틀을 찾을 수 없음 - battleId={}", battleId);
                return null;
            }

            return battleMapper.convertBattleDTO(battle);

        } catch (Exception e) {
            logger.error("배틀 조회 중 오류 발생: battleId={}", battleId, e);
            return null;
        }
    }

    @Override
    public List<BattleDTO> getUserBattles(String userId) {
        try {
            List<Battle> battles = battleRepository.findByUserOrderByCreatedAtDesc(userId);
            List<BattleDTO> battleDTOs = battleMapper.convertBattleDTOList(battles);

            logger.debug("사용자 배틀 내역 조회 완료: userId={}, count={}", userId, battleDTOs.size());
            return battleDTOs;

        } catch (Exception e) {
            logger.error("사용자 배틀 내역 조회 중 오류 발생: userId={}", userId, e);
            return List.of();
        }
    }

    @Override
    @Transactional
    public void cancelExpiredBattles() {
        try {
            LocalDateTime cutoffTime = LocalDateTime.now().minusMinutes(1);
            List<Battle> expiredBattles = battleRepository.findExpiredWaitingBattles(BattleState.WAITING, cutoffTime);

            for (Battle battle : expiredBattles) {
                battle.setState(BattleState.CANCEL);
                battle.setContent("상대방의 응답이 없어 배틀이 취소되었습니다.");
                battleRepository.save(battle);

                logger.debug("만료된 배틀 취소: battleId={}, userId1={}", battle.getId(), battle.getUserid1());
            }

            if (!expiredBattles.isEmpty()) {
                logger.debug("만료된 배틀 {}개 취소 완료", expiredBattles.size());
            }

        } catch (Exception e) {
            logger.error("만료된 배틀 취소 중 오류 발생", e);
        }
    }

    @Override
    @Transactional
    public void processRunningBattles() {
        try {
            List<Battle> runningBattles = battleRepository.findByState(BattleState.RUNNING);
            LocalDateTime cutoffTime = LocalDateTime.now().minusMinutes(1);

            for (Battle battle : runningBattles) {
                // 1분 이상 지난 RUNNING 배틀만 처리
                if (battle.getCreatedAt().isBefore(cutoffTime) && battle.getContent() == null) {
                    // GPT 배틀 결과가 아직 없는 경우 처리
                    processDelayedBattle(battle);
                }
            }

        } catch (Exception e) {
            logger.error("RUNNING 배틀 처리 중 오류 발생", e);
        }
    }

    /**
     * 비동기로 GPT 배틀 처리 및 1분 후 결과 전송
     */
    @Async
    public void processBattleAsync(Battle battle) {
        try {
            // 1분 대기
            Thread.sleep(60000);

            // GPT API 호출하여 배틀 진행
            GptBattleService.BattleResult result = gptBattleService.processBattle(
                    battle.getUserid1(), battle.getUserid2(),
                    battle.getUser1Card(), battle.getUser2Card()
            );

            // 배틀 결과 저장
            battle.setState(result.getWinner());
            battle.setContent(result.getContent());
            battleRepository.save(battle);

            // 결과 FCM 알림 전송
            fcmService.sendBattleResultNotification(
                    battle.getUserid1(), battle.getUserid2(),
                    battle.getId(), result.getWinner()
            );

            logger.debug("비동기 배틀 처리 완료: battleId={}, winner={}", battle.getId(), result.getWinner());

        } catch (Exception e) {
            logger.error("비동기 배틀 처리 중 오류 발생: battleId={}", battle.getId(), e);
        }
    }

    /**
     * 활성 배틀 존재 여부 확인
     */
    private boolean hasActiveBattle(String userId) {
        return battleRepository.findByUserid1AndState(userId, BattleState.WAITING).isPresent();
    }

    /**
     * 카드 ID로 카드 조회 (ResourceCard 또는 CharacterCard)
     */
    private Card getCardById(int cardId) {
        // 먼저 ResourceCard에서 조회
        Card card = resourceCardRepository.findById(cardId).orElse(null);
        if (card != null) {
            return card;
        }

        // ResourceCard에 없으면 CharacterCard에서 조회
        return characterCardRepository.findById(cardId).orElse(null);
    }

    /**
     * 지연된 배틀 처리 (GPT 호출 실패 등으로 결과가 없는 경우)
     */
    private void processDelayedBattle(Battle battle) {
        try {
            GptBattleService.BattleResult result = gptBattleService.processBattle(
                    battle.getUserid1(), battle.getUserid2(),
                    battle.getUser1Card(), battle.getUser2Card()
            );

            battle.setState(result.getWinner());
            battle.setContent(result.getContent());
            battleRepository.save(battle);

            fcmService.sendBattleResultNotification(
                    battle.getUserid1(), battle.getUserid2(),
                    battle.getId(), result.getWinner()
            );

            logger.debug("지연된 배틀 처리 완료: battleId={}", battle.getId());

        } catch (Exception e) {
            logger.error("지연된 배틀 처리 실패: battleId={}", battle.getId(), e);
        }
    }
}