
package com.ssafy.italian_brainrot.scheduler;

import com.ssafy.italian_brainrot.service.battle.BattleService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class BattleScheduler {

    private final BattleService battleService;
    private final Logger logger = LoggerFactory.getLogger(BattleScheduler.class);

    public BattleScheduler(final BattleService battleService) {
        this.battleService = battleService;
    }

    @Scheduled(fixedRate = 30000) // 30초마다 실행
    public void cancelExpiredBattles() {
        try {
            battleService.cancelExpiredBattles();
        } catch (Exception e) {
            logger.error("만료된 배틀 취소 스케줄링 오류", e);
        }
    }

    @Scheduled(fixedRate = 30000) // 30초마다 실행
    public void processRunningBattles() {
        try {
            battleService.processRunningBattles();
        } catch (Exception e) {
            logger.error("RUNNING 배틀 처리 스케줄링 오류", e);
        }
    }
}