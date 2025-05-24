package com.ssafy.italian_brainrot.mapper;

import com.ssafy.italian_brainrot.dto.inventory.InventoryDTO;
import com.ssafy.italian_brainrot.entity.*;
import org.springframework.stereotype.Component;

@Component
public class InventoryMapper {

    private final CardMapper cardMapper;

    public InventoryMapper(CardMapper cardMapper) {
        this.cardMapper = cardMapper;
    }

    public InventoryDTO convertToInventoryDTO(Inventory entity) {
        InventoryDTO.InventoryDTOBuilder builder = InventoryDTO.builder()
                .id(entity.getId())
                .userId(entity.getUser().getId())
                .type(entity.getType())
                .quantity(entity.getQuantity());

        if (entity.getCard() != null) {
            switch (entity.getType()) {
                case RESOURCE_CARD:
                    if (entity.getCard() instanceof ResourceCard) {
                        builder.resourceCard(cardMapper.convertToResourceCardDTOSimple((ResourceCard) entity.getCard()));
                    }
                    break;
                case CHARACTER_CARD:
                    if (entity.getCard() instanceof CharacterCard) {
                        builder.characterCard(cardMapper.convertToCharacterCardDTOSimple((CharacterCard) entity.getCard()));
                    }
                    break;
                case PACK:
                    break;
            }
        }

        return builder.build();
    }

    public Inventory convertToInventory(InventoryDTO dto) {
        Card card = null;

        switch (dto.getType()) {
            case CHARACTER_CARD:
                if (dto.getCharacterCard() != null) {
                    CharacterCard characterCard = CharacterCard.builder()
                            .id(dto.getCharacterCard().getId())
                            .build();
                    card = characterCard;
                }
                break;
            case RESOURCE_CARD:
                if (dto.getResourceCard() != null) {
                    ResourceCard resourceCard = ResourceCard.builder()
                            .id(dto.getResourceCard().getId())
                            .build();
                    card = resourceCard;
                }
                break;
            case PACK:
                break;
        }

        return Inventory.builder()
                .id(dto.getId())
                .user(User.builder().id(dto.getUserId()).build())
                .type(dto.getType())
                .quantity(dto.getQuantity())
                .card(card)
                .build();
    }
}