package com.ssafy.italian_brainrot.service.card;

import com.ssafy.italian_brainrot.dto.card.CharacterCardDTO;
import com.ssafy.italian_brainrot.dto.card.ResourceCardDTO;

public interface CardService {
    ResourceCardDTO getResourceCard(int id);

    ResourceCardDTO getAllResourceCard(int id);

    CharacterCardDTO getCharacterCard(int id);

    CharacterCardDTO getAllCharacterCard(int id);


}
