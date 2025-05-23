package com.ssafy.italian_brainrot.service;

import com.ssafy.italian_brainrot.dto.InventoryDTO;

import java.util.List;

public interface InventoryService {
    List<InventoryDTO> getInventoryItemList(String userId);
}
