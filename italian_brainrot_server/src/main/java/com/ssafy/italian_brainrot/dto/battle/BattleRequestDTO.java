package com.ssafy.italian_brainrot.dto.battle;

import lombok.*;

@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BattleRequestDTO {
    // POST /battle (배틀 생성)
    private String userId1;
    private int user1Card;

    // PUT /battle (배틀 수락)
    private int id;
    private String userId2;
    private int user2Card;
}