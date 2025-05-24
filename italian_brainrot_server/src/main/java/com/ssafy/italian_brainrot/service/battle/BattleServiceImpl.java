package com.ssafy.italian_brainrot.service.battle;

import com.ssafy.italian_brainrot.dto.battle.BattleDTO;
import com.ssafy.italian_brainrot.dto.battle.BattleRequestDTO;
import com.ssafy.italian_brainrot.entity.Battle;
import com.ssafy.italian_brainrot.entity.Card;
import com.ssafy.italian_brainrot.enumerate.BattleState;
import com.ssafy.italian_brainrot.mapper.BattleMapper;
import com.ssafy.italian_brainrot.repository.BattleRepository;
import com.ssafy.italian_brainrot.repository.CharacterCardRepository;
import com.ssafy.italian_brainrot.service.fcm.FcmService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.lang.constant.Constable;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
public class BattleServiceImpl implements BattleService {

    private final BattleRepository battleRepository;
    private final CharacterCardRepository characterCardRepository;
    private final BattleMapper battleMapper;
    private final GptBattleService gptBattleService;
    private final FcmService fcmService;

    private final Logger logger = LoggerFactory.getLogger(BattleServiceImpl.class);

    public BattleServiceImpl(BattleRepository battleRepository,
                             CharacterCardRepository characterCardRepository,
                             BattleMapper battleMapper,
                             GptBattleService gptBattleService,
                             FcmService fcmService){
        this.battleRepository = battleRepository;
        this.characterCardRepository = characterCardRepository;
        this.battleMapper = battleMapper;
        this.gptBattleService = gptBattleService;
        this.fcmService = fcmService;
    }

    @Override
    @Transactional
    public int createBattle(String userId, BattleRequestDTO request) {
        try {
            // 1. 중복 배틀 체크
            if (hasActiveBattle(userId)) {
                return -1;
            }

            // 2. 카드 조회
            Card user1Card = getCardById(request.getUser1Card());
            if (user1Card == null) {
                return -1;
            }

            // 3. 배틀 생성
            Battle battle = Battle.builder()
                    .userid1(userId)
                    .user1Card(user1Card)
                    .build();

            battle = battleRepository.save(battle);

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
                return -1;
            }

            // 2. 배틀 상태 확인
            if (battle.getState() != BattleState.WAITING) {
                return -1;
            }

            // 3. 자기 자신과의 배틀 방지
            if (battle.getUserid1().equals(userId)) {
                return -1;
            }

            // 4. 카드 조회
            Card user2Card = getCardById(request.getUser2Card());
            if (user2Card == null) {
                return -1;
            }

            // 5. 배틀 정보 업데이트
            battle.setUserid2(userId);
            battle.setUser2Card(user2Card);
            battle.setState(BattleState.RUNNING);
            battle = battleRepository.save(battle);

            // 6. 배틀 시작 FCM 알림 전송
            fcmService.sendBattleStartNotification(battle.getUserid1(), battle.getUserid2(), battle.getId());

            // 7. 비동기로 GPT 배틀 처리 및 1분 후 결과 전송
            processBattleAsync(battle);

            return battle.getId();
        } catch (Exception e) {
            return -1;
        }
    }

    @Override
    public BattleDTO getBattle(int battleId) {
        try {
            Battle battle = battleRepository.findById(battleId).orElse(null);
            if (battle == null) {
                return null;
            }

            return battleMapper.convertToBattleDTO(battle);
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public List<BattleDTO> getUserBattles(String userId) {
        try {
            List<Battle> battles = battleRepository.findByUserOrderByCreatedAtDesc(userId);
            return battleMapper.convertToBattleDTOList(battles);
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    @Transactional
    public void cancelExpiredBattles() {
        try {
            LocalDateTime time = LocalDateTime.now().minusMinutes(1);
            List<Battle> expiredBattles = battleRepository.findExpiredWaitingBattles(BattleState.WAITING, time);

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

    private boolean hasActiveBattle(String userId) {
        return battleRepository.findByUserid1AndState(userId, BattleState.WAITING).isPresent();
    }

    private Card getCardById(int cardId) {
        return characterCardRepository.findById(cardId).orElse(null);
    }

    @Async
    public void processBattleAsync(Battle battle) {
        try {
            // 1분 대기
            Thread.sleep(60000);

            // GPT API 호출하여 배틀 진행
            Map<String, Constable> result = gptBattleService.processBattle(
                    battle.getUserid1(), battle.getUserid2(),
                    battle.getUser1Card(), battle.getUser2Card()
            );

            // 배틀 결과 저장
            battle.setState((BattleState) result.get("winner"));
            battle.setContent((String) result.get("content"));
            battleRepository.save(battle);

            // 결과 FCM 알림 전송
            fcmService.sendBattleResultNotification(
                    battle.getUserid1(), battle.getUserid2(),
                    battle.getId(), (BattleState) result.get("winner")
            );

        } catch (Exception e) {
            logger.error("비동기 배틀 처리 중 오류 발생: battleId={}", battle.getId(), e);
        }
    }

    private void processDelayedBattle(Battle battle) {
        try {
            Map<String, Constable> result = gptBattleService.processBattle(
                    battle.getUserid1(), battle.getUserid2(),
                    battle.getUser1Card(), battle.getUser2Card()
            );

            battle.setState((BattleState) result.get("winner"));
            battle.setContent((String) result.get("content"));
            battleRepository.save(battle);

            fcmService.sendBattleResultNotification(
                    battle.getUserid1(), battle.getUserid2(),
                    battle.getId(), (BattleState) result.get("winner")
            );

        } catch (Exception e) {
            logger.error("지연된 배틀 처리 실패: battleId={}", battle.getId(), e);
        }
    }

}