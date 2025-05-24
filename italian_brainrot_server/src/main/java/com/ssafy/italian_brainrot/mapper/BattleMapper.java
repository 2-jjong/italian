package com.ssafy.italian_brainrot.mapper;

import com.ssafy.italian_brainrot.dto.battle.BattleDTO;
import com.ssafy.italian_brainrot.entity.Battle;
import org.springframework.stereotype.Component;

import java.time.ZoneId;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class BattleMapper {

    public BattleDTO convertToBattleDTO(Battle entity) {
        return BattleDTO.builder()
                .id(entity.getId())
                .userId1(entity.getUserid1())
                .userId2(entity.getUserid2())
                .user1CardId(entity.getUser1Card().getId())
                .user2CardId(entity.getUser2Card().getId())
                .state(entity.getState())
                .content(entity.getContent())
                .createdAt(entity.getCreatedAt().atZone(ZoneId.systemDefault()).toEpochSecond())
                .build();
    }

    public List<BattleDTO> convertToBattleDTOList(List<Battle> entities) {
        return entities.stream()
                .map(this::convertToBattleDTO)
                .collect(Collectors.toList());
    }

}