package com.ssafy.italian_brainrot.dto.battle;

import com.ssafy.italian_brainrot.enumerate.BattleState;
import lombok.*;

@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BattleDTO {
    private int id;
    private String userId1;
    private String userId2;
    private int user1CardId;
    private int user2CardId;
    private BattleState state;
    private String content;
    private long createdAt;
}