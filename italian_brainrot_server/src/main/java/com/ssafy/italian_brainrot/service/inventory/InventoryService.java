package com.ssafy.italian_brainrot.service.inventory;

import com.ssafy.italian_brainrot.dto.inventory.InventoryDTO;
import com.ssafy.italian_brainrot.dto.inventory.InventoryRequestDTO;

import java.util.List;

public interface InventoryService {

    List<InventoryDTO> getInventoryItemList(String userId);

    boolean addInventoryItem(InventoryRequestDTO inventoryRequestDTO);
}