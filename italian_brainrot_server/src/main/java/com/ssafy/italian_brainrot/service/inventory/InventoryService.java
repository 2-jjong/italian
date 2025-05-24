package com.ssafy.italian_brainrot.service.inventory;

import com.ssafy.italian_brainrot.dto.inventory.InventoryDTO;
import com.ssafy.italian_brainrot.dto.inventory.PackOpenResponseDTO;
import com.ssafy.italian_brainrot.enumerate.InventoryItemType;

import java.util.List;

public interface InventoryService {

    List<InventoryDTO> getInventoryItemList(String userId);

    Boolean InsertItemToInventory(String userId, int productId, int quantity, InventoryItemType type);

    Boolean updateItemQuantity(String userId, int cardId, int quantity);

    Boolean removeItem(String userId, int cardId);

    PackOpenResponseDTO openCardPack(String userId, int packId);

}