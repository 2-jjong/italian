package com.ssafy.italian_brainrot.mapper;

import com.ssafy.italian_brainrot.dto.card.CharacterCardDTO;
import com.ssafy.italian_brainrot.dto.card.ResourceCardDTO;
import com.ssafy.italian_brainrot.entity.CharacterCard;
import com.ssafy.italian_brainrot.entity.ResourceCard;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class CardMapper {
    public ResourceCardDTO convertToResourceCardDTO(ResourceCard entity, List<Integer> craftableCharacterCardIds) {
        ResourceCardDTO dto = new ResourceCardDTO();

        dto.setId(entity.getId());
        dto.setName(entity.getName());
        dto.setContent(entity.getContent());
        dto.setImagePath(entity.getImg());

        dto.setProduct_id(entity.getProduct().getId());
        dto.setExpected_probability(entity.getExpectedProbability());

        dto.setCraftableCharacterCardIds(craftableCharacterCardIds);

        return dto;
    }

    public CharacterCardDTO convertToCharacterCardDTO(CharacterCard entity, Map<Integer, Integer> requiredResourceCards) {
        CharacterCardDTO dto = new CharacterCardDTO();

        dto.setId(entity.getId());
        dto.setName(entity.getName());
        dto.setContent(entity.getContent());
        dto.setImagePath(entity.getImg());

        dto.setGrade(entity.getGrade());
        dto.setVoicePath(entity.getVoice());
        dto.setExpectedProbability(entity.getExpectedProbability());

        dto.setRequiredResourceCards(requiredResourceCards);

        return dto;
    }

    public ResourceCardDTO convertToResourceCardDTOSimple(ResourceCard entity) {
        return convertToResourceCardDTO(entity, List.of());
    }

    public CharacterCardDTO convertToCharacterCardDTOSimple(CharacterCard entity) {
        return convertToCharacterCardDTO(entity, Map.of());
    }

    public List<ResourceCardDTO> convertToResourceCardDTOList(List<ResourceCard> entities) {
        return entities.stream()
                .map(this::convertToResourceCardDTOSimple)
                .collect(Collectors.toList());
    }

    public List<CharacterCardDTO> convertToCharacterCardDTOList(List<CharacterCard> entities) {
        return entities.stream()
                .map(this::convertToCharacterCardDTOSimple)
                .collect(Collectors.toList());
    }
}