package com.ssafy.italian_brainrot.service.inventory;

import com.ssafy.italian_brainrot.dto.inventory.InventoryDTO;
import com.ssafy.italian_brainrot.dto.inventory.InventoryRequestDTO;
import com.ssafy.italian_brainrot.entity.*;
import com.ssafy.italian_brainrot.enumerate.InventoryItemType;
import com.ssafy.italian_brainrot.mapper.InventoryMapper;
import com.ssafy.italian_brainrot.repository.InventoryRepository;
import com.ssafy.italian_brainrot.repository.UserRepository;
import com.ssafy.italian_brainrot.repository.CharacterCardRepository;
import com.ssafy.italian_brainrot.repository.ResourceCardRepository;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class InventoryServiceImpl implements InventoryService {
    @Autowired
    private InventoryRepository inventoryRepository;

    @Autowired
    private InventoryMapper inventoryMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CharacterCardRepository characterCardRepository;

    @Autowired
    private ResourceCardRepository resourceCardRepository;

    private final Logger log = LoggerFactory.getLogger(InventoryServiceImpl.class);

    @Override
    public List<InventoryDTO> getInventoryItemList(String userId) {
        List<Inventory> entity = inventoryRepository.findByUserId(userId);
        List<InventoryDTO> dto = entity.stream().map((inventory) -> inventoryMapper.convert(inventory)).toList();
        return dto;
    }

    @Override
    @Transactional
    public boolean addInventoryItem(InventoryRequestDTO inventoryRequestDTO) {
        try {
            User user = userRepository.findById(inventoryRequestDTO.getUserId()).orElse(null);
            if (user == null) {
                return false;
            }

            Card card = getCardById(inventoryRequestDTO.getCardId(), inventoryRequestDTO.getType());
            if (card == null) {
                return false;
            }

            List<Inventory> existingItems = inventoryRepository.findByUserId(user.getId());
            Optional<Inventory> existingItem = existingItems.stream()
                    .filter(inv -> inv.getCard().getId().equals(card.getId()))
                    .findFirst();

            if (existingItem.isPresent()) {
                Inventory inventory = existingItem.get();
                inventory.setQuantity(inventory.getQuantity() + inventoryRequestDTO.getQuantity());
                inventoryRepository.save(inventory);
            } else {
                Inventory newInventory = Inventory.builder()
                        .user(user)
                        .type(inventoryRequestDTO.getType())
                        .quantity(inventoryRequestDTO.getQuantity())
                        .card(card)
                        .build();
                inventoryRepository.save(newInventory);
            }

            return true;
        } catch (Exception e) {
            log.error(e.getMessage());
            return false;
        }
    }

    private Card getCardById(int cardId, InventoryItemType type) {
        return switch (type) {
            case CHARACTER_CARD -> characterCardRepository.findById(cardId).orElse(null);
            case RESOURCE_CARD -> resourceCardRepository.findById(cardId).orElse(null);
            case PACK -> null;
        };
    }
}