package com.ssafy.italian_brainrot.dto.inventory;

import com.ssafy.italian_brainrot.enumerate.InventoryItemType;
import lombok.*;

@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InventoryRequestDTO {
    String userId;
    InventoryItemType type;
    int quantity;
    int cardId;
}
