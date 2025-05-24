package com.ssafy.italian_brainrot.service.battle;

import com.ssafy.italian_brainrot.dto.battle.BattleDTO;
import com.ssafy.italian_brainrot.dto.battle.BattleRequestDTO;

import java.util.List;

public interface BattleService {

    int createBattle(String userId, BattleRequestDTO request);

    int acceptBattle(String userId, BattleRequestDTO request);

    BattleDTO getBattle(int battleId);

    List<BattleDTO> getUserBattles(String userId);

    void cancelExpiredBattles();

    void processRunningBattles();

}