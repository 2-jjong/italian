package com.ssafy.italian_brainrot.controller;

import com.ssafy.italian_brainrot.dto.battle.BattleDTO;
import com.ssafy.italian_brainrot.dto.battle.BattleRequestDTO;
import com.ssafy.italian_brainrot.service.battle.BattleService;
import com.ssafy.italian_brainrot.util.CookieUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/battle")
public class BattleController {

    private static final Logger logger = LoggerFactory.getLogger(BattleController.class);

    @Autowired
    private BattleService battleService;

    @Autowired
    private CookieUtil cookieUtil;

    /**
     * 배틀 생성
     * POST /battle
     */
    @PostMapping
    public ResponseEntity<Integer> createBattle(@RequestBody BattleRequestDTO request,
                                                HttpServletRequest httpRequest) {
        // Interceptor에서 이미 인증 체크했으므로 userId 추출
        String userId = cookieUtil.getUserIdFromRequest(httpRequest);

        logger.debug("배틀 생성 요청: userId={}, user1Card={}", userId, request.getUser1Card());

        int battleId = battleService.createBattle(userId, request);

        if (battleId > 0) {
            logger.debug("배틀 생성 성공: userId={}, battleId={}", userId, battleId);
            return ResponseEntity.ok(battleId);
        } else {
            logger.warn("배틀 생성 실패: userId={}", userId);
            return ResponseEntity.ok(-1);
        }
    }

    /**
     * 배틀 수락
     * PUT /battle
     */
    @PutMapping
    public ResponseEntity<Integer> acceptBattle(@RequestBody BattleRequestDTO request,
                                                HttpServletRequest httpRequest) {
        // Interceptor에서 이미 인증 체크했으므로 userId 추출
        String userId = cookieUtil.getUserIdFromRequest(httpRequest);

        logger.debug("배틀 수락 요청: userId={}, battleId={}, user2Card={}",
                userId, request.getId(), request.getUser2Card());

        int battleId = battleService.acceptBattle(userId, request);

        if (battleId > 0) {
            logger.debug("배틀 수락 성공: userId={}, battleId={}", userId, battleId);
            return ResponseEntity.ok(battleId);
        } else {
            logger.warn("배틀 수락 실패: userId={}, battleId={}", userId, request.getId());
            return ResponseEntity.ok(-1);
        }
    }

    /**
     * 배틀 결과 조회
     * GET /battle/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<BattleDTO> getBattle(@PathVariable int id, HttpServletRequest request) {
        // Interceptor에서 이미 인증 체크했으므로 userId 추출
        String userId = cookieUtil.getUserIdFromRequest(request);

        BattleDTO battle = battleService.getBattle(id);

        if (battle != null) {
            logger.debug("배틀 조회 성공: userId={}, battleId={}, state={}", userId, id, battle.getState());
            return ResponseEntity.ok(battle);
        } else {
            logger.warn("배틀 조회 실패: userId={}, battleId={}", userId, id);
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * 특정 유저의 배틀 내역 조회
     * GET /battle
     */
    @GetMapping
    public ResponseEntity<List<BattleDTO>> getUserBattles(HttpServletRequest request) {
        // Interceptor에서 이미 인증 체크했으므로 userId 추출
        String userId = cookieUtil.getUserIdFromRequest(request);

        List<BattleDTO> battles = battleService.getUserBattles(userId);

        logger.debug("사용자 배틀 내역 조회 완료: userId={}, count={}", userId, battles.size());

        return ResponseEntity.ok(battles);
    }
}