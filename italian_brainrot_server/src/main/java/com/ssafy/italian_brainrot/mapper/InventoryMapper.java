package com.ssafy.italian_brainrot.mapper;

import com.ssafy.italian_brainrot.dto.inventory.InventoryDTO;
import com.ssafy.italian_brainrot.entity.*;
import org.springframework.stereotype.Component;

@Component
public class InventoryMapper {
    // TODO: Card DTO Mapper
    public InventoryDTO convert(Inventory entity) {
        InventoryDTO dto = InventoryDTO
                .builder()
                .id(entity.getId())
                .userId(entity.getUser().getId())
                .type(entity.getType())
                .quantity(entity.getQuantity())
//                .card(entity.getCard())
                .build();
        return dto;
    }

    public Inventory convert(InventoryDTO dto) {
        Card card = null;
        switch (dto.getType()) {
            case CHARACTER_CARD -> {
                CharacterCard characterCard = CharacterCard.builder().build();
                characterCard.setId(dto.getId());
                card = characterCard;
                break;
            }
            case RESOURCE_CARD -> {
                ResourceCard resourceCard = ResourceCard.builder().build();
                resourceCard.setId(dto.getId());
                card = resourceCard;
                break;
            }
            case PACK -> {
                break;
            }
        }
        Inventory entity = Inventory
                .builder()
                .id(dto.getId())
                .user(User.builder().id(dto.getUserId()).build())
                .type(dto.getType())
                .quantity(dto.getQuantity())
                .card(card)
                .build();
        return entity;
    }
}
