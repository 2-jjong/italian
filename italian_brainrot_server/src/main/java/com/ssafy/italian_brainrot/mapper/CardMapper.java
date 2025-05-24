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

    /**
     * ResourceCard Entity -> ResourceCardDTO 변환
     */
    public ResourceCardDTO convertResourceCardDTO(ResourceCard entity, List<Integer> craftableCharacterCardIds) {
        ResourceCardDTO dto = new ResourceCardDTO();

        // 부모 필드 설정
        dto.setId(entity.getId());
        dto.setName(entity.getName());
        dto.setContent(entity.getContent());
        dto.setImagePath(entity.getImg());

        // 자식 필드 설정
        dto.setProduct_id(entity.getProduct().getId());
        dto.setExpected_probability(entity.getExpectedProbability());

        // Service에서 전달받은 데이터 설정
        dto.setCraftableCharacterCardIds(craftableCharacterCardIds);

        return dto;
    }

    /**
     * CharacterCard Entity -> CharacterCardDTO 변환
     */
    public CharacterCardDTO convertCharacterCardDTO(CharacterCard entity, Map<Integer, Integer> requiredResourceCards) {
        CharacterCardDTO dto = new CharacterCardDTO();

        // 부모 필드 설정
        dto.setId(entity.getId());
        dto.setName(entity.getName());
        dto.setContent(entity.getContent());
        dto.setImagePath(entity.getImg());

        // 자식 필드 설정
        dto.setGrade(entity.getGrade());
        dto.setVoicePath(entity.getVoice());
        dto.setExpectedProbability(entity.getExpectedProbability());

        // Service에서 전달받은 데이터 설정
        dto.setRequiredResourceCards(requiredResourceCards);

        return dto;
    }

    /**
     * 단순 변환 (추가 데이터 없이)
     */
    public ResourceCardDTO convertResourceCardDTOSimple(ResourceCard entity) {
        return convertResourceCardDTO(entity, List.of());
    }

    public CharacterCardDTO convertCharacterCardDTOSimple(CharacterCard entity) {
        return convertCharacterCardDTO(entity, Map.of());
    }

    /**
     * 리스트 변환
     */
    public List<ResourceCardDTO> convertResourceCardDTOList(List<ResourceCard> entities) {
        return entities.stream()
                .map(this::convertResourceCardDTOSimple)
                .collect(Collectors.toList());
    }

    public List<CharacterCardDTO> convertCharacterCardDTOList(List<CharacterCard> entities) {
        return entities.stream()
                .map(this::convertCharacterCardDTOSimple)
                .collect(Collectors.toList());
    }
}