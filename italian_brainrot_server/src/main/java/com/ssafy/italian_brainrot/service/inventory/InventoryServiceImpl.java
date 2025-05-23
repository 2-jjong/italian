package com.ssafy.italian_brainrot.service.inventory;

import com.ssafy.italian_brainrot.dto.inventory.InventoryDTO;
import com.ssafy.italian_brainrot.entity.Inventory;
import com.ssafy.italian_brainrot.mapper.InventoryMapper;
import com.ssafy.italian_brainrot.repository.InventoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class InventoryServiceImpl implements InventoryService {
    @Autowired
    private InventoryRepository inventoryRepository;

    @Autowired
    private InventoryMapper inventoryMapper;

    @Override
    public List<InventoryDTO> getInventoryItemList(String userId) {
        List<Inventory> entity = inventoryRepository.findByUser_Id(userId);
        List<InventoryDTO> dto = entity.stream().map((inventory) -> inventoryMapper.convert(inventory)).toList();
        return dto;
    }
}
