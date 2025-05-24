package com.ssafy.italian_brainrot.controller;

import com.ssafy.italian_brainrot.dto.battle.BattleDTO;
import com.ssafy.italian_brainrot.dto.battle.BattleRequestDTO;
import com.ssafy.italian_brainrot.service.battle.BattleService;
import com.ssafy.italian_brainrot.util.CookieUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/battle")
public class BattleController {

    private final BattleService battleService;
    private final CookieUtil cookieUtil;
    private static final Logger log = LoggerFactory.getLogger(BattleController.class);

    public BattleController(BattleService battleService,
                            CookieUtil cookieUtil) {
        this.battleService = battleService;
        this.cookieUtil = cookieUtil;
    }

    @PostMapping("")
    public Integer createBattle(@RequestBody BattleRequestDTO request,
                                                HttpServletRequest httpRequest) {
        String userId = cookieUtil.getUserIdFromRequest(httpRequest);

        return battleService.createBattle(userId, request);
    }

    @PutMapping("")
    public Integer acceptBattle(@RequestBody BattleRequestDTO request,
                                                HttpServletRequest httpRequest) {
        String userId = cookieUtil.getUserIdFromRequest(httpRequest);

        return battleService.acceptBattle(userId, request);
    }

    @GetMapping("/{id}")
    public BattleDTO getBattle(@PathVariable("id") int id, HttpServletRequest request) {
        return battleService.getBattle(id);
    }

    @GetMapping("")
    public List<BattleDTO> getUserBattles(HttpServletRequest request) {
        String userId = cookieUtil.getUserIdFromRequest(request);

        return battleService.getUserBattles(userId);
    }

}