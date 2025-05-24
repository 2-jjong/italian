package com.ssafy.italian_brainrot.mapper;

import com.ssafy.italian_brainrot.dto.battle.BattleDTO;
import com.ssafy.italian_brainrot.entity.Battle;
import com.ssafy.italian_brainrot.entity.Card;
import org.springframework.stereotype.Component;

import java.time.ZoneId;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class BattleMapper {

    /**
     * Battle Entity -> BattleDTO 변환
     */
    public BattleDTO convertBattleDTO(Battle entity) {
        return BattleDTO.builder()
                .id(entity.getId())
                .userId1(entity.getUserid1())
                .userId2(entity.getUserid2())
                .user1CardId(entity.getUser1Card().getId())
                .user2CardId(entity.getUser2Card() != null ? entity.getUser2Card().getId() : 0)
                .state(entity.getState())
                .content(entity.getContent())
                .createdAt(entity.getCreatedAt().atZone(ZoneId.systemDefault()).toEpochSecond())
                .build();
    }

    /**
     * Battle Entity 리스트 -> BattleDTO 리스트 변환
     */
    public List<BattleDTO> convertBattleDTOList(List<Battle> entities) {
        return entities.stream()
                .map(this::convertBattleDTO)
                .collect(Collectors.toList());
    }
}