package com.ssafy.italian_brainrot.dto.recipe;

import com.ssafy.italian_brainrot.dto.card.CharacterCardDTO;
import lombok.*;

@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RecipeResponseDTO {
    private boolean isSuccess; // 합성 성공 여부
    private CharacterCardDTO card; // 합성 성공 시 획득한 캐릭터 카드 (실패 시 null)
}