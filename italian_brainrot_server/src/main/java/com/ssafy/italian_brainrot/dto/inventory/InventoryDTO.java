package com.ssafy.italian_brainrot.dto.inventory;

import com.ssafy.italian_brainrot.dto.card.CharacterCardDTO;
import com.ssafy.italian_brainrot.dto.card.ResourceCardDTO;
import com.ssafy.italian_brainrot.enumerate.InventoryItemType;
import lombok.*;

@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InventoryDTO {
    private int id;
    private String userId;
    private InventoryItemType type;
    private int quantity;
    private ResourceCardDTO resourceCard;
    private CharacterCardDTO characterCard;
}
