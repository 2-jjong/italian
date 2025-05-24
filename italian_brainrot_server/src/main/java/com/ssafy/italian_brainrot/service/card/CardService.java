package com.ssafy.italian_brainrot.service.card;

import com.ssafy.italian_brainrot.dto.card.CharacterCardDTO;
import com.ssafy.italian_brainrot.dto.card.ResourceCardDTO;

import java.util.List;

public interface CardService {

    ResourceCardDTO getResourceCard(int id);

    CharacterCardDTO getCharacterCard(int id);

    List<ResourceCardDTO> getAllResourceCards();

    List<CharacterCardDTO> getAllCharacterCards();

}